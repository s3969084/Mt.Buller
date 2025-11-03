package com.rmit.mtbuller.db;

import java.sql.Connection;
import java.sql.ResultSet;

/** Ensures DB schema exists; migrates legacy customers(name) and upgrades bookings schema. */
public final class Bootstrap {
    private Bootstrap() {}

    public static void ensureSchemaAndSeed() {
        try (Connection c = Database.get(); var s = c.createStatement()) {
            // --- Users (unchanged)
            s.execute("""
                CREATE TABLE IF NOT EXISTS users(
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  username   TEXT UNIQUE NOT NULL,
                  first_name TEXT NOT NULL,
                  last_name  TEXT NOT NULL,
                  email      TEXT NOT NULL,
                  password_hash TEXT NOT NULL,
                  role TEXT NOT NULL CHECK (role IN ('MASTER','ADMIN'))
                )""");

            // --- Accommodations (unchanged)
            s.execute("""
                CREATE TABLE IF NOT EXISTS accommodations(
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  property_type     TEXT NOT NULL,
                  room_type         TEXT NOT NULL,
                  capacity          INTEGER NOT NULL CHECK (capacity>0),
                  base_price_cents  INTEGER NOT NULL CHECK (base_price_cents>=0),
                  location          TEXT,
                  status            TEXT NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','INACTIVE'))
                )""");

            // --- Customers: target schema (first_name,last_name,email,phone,skill_level)
            s.execute("""
                CREATE TABLE IF NOT EXISTS customers(
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  first_name TEXT NOT NULL,
                  last_name  TEXT NOT NULL,
                  email TEXT,
                  phone TEXT
                )""");

            // Migrate legacy customers(name → first/last)
            if (customersHasLegacyNameColumn(c)) {
                s.execute("ALTER TABLE customers RENAME TO customers_old");
                s.execute("""
                    CREATE TABLE customers(
                      id INTEGER PRIMARY KEY AUTOINCREMENT,
                      first_name TEXT NOT NULL,
                      last_name  TEXT NOT NULL,
                      email TEXT,
                      phone TEXT
                    )""");
                try (var rs = s.executeQuery("SELECT id, name, email, phone FROM customers_old");
                     var ins = c.prepareStatement(
                             "INSERT INTO customers(id, first_name, last_name, email, phone) VALUES (?,?,?,?,?)")) {
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String[] parts = splitName(rs.getString("name"));
                        ins.setInt(1, id);
                        ins.setString(2, parts[0]);
                        ins.setString(3, parts[1]);
                        ins.setString(4, rs.getString("email"));
                        ins.setString(5, rs.getString("phone"));
                        ins.addBatch();
                    }
                    ins.executeBatch();
                }
                s.execute("DROP TABLE customers_old");
            }

            // Add new customer column if missing
            addColumnIfMissing(c, "customers", "skill_level", "TEXT DEFAULT 'Beginner'");
            // Backfill NULLs to 'Beginner'
            s.execute("UPDATE customers SET skill_level='Beginner' WHERE skill_level IS NULL");

            // --- Bookings (base schema)
            s.execute("""
                CREATE TABLE IF NOT EXISTS bookings(
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  accommodation_id INTEGER NOT NULL,
                  customer_id      INTEGER NOT NULL,
                  -- ISO dates for safe lexicographic comparisons
                  check_in  TEXT NOT NULL,
                  check_out TEXT NOT NULL,
                  nightly_price_cents INTEGER NOT NULL CHECK (nightly_price_cents >= 0),
                  status TEXT NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING','CONFIRMED','CANCELLED')),
                  created_at TEXT NOT NULL DEFAULT (datetime('now')),
                  FOREIGN KEY(accommodation_id) REFERENCES accommodations(id) ON DELETE RESTRICT,
                  FOREIGN KEY(customer_id)      REFERENCES customers(id)      ON DELETE RESTRICT,
                  CHECK (check_out > check_in)
                )""");

            // If bookings exists but missing non-null base fields, recreate
            if (!columnExists(c, "bookings", "created_at") || !columnExists(c, "bookings", "status")) {
                recreateBookingsWithNewSchema(c);
            }

            // ---- New bookings columns (lift pass + lessons + price snapshots)
            // Keep ALTERs simple (SQLite: allow DEFAULT/NULL; enforce in code)
            addColumnIfMissing(c, "bookings", "lift_pass_type",         "TEXT DEFAULT 'NONE'");  // NONE | DAYS | SEASON
            addColumnIfMissing(c, "bookings", "lift_pass_days",         "INTEGER DEFAULT 0");
            addColumnIfMissing(c, "bookings", "lesson_level",           "TEXT");                 // informational
            addColumnIfMissing(c, "bookings", "lesson_count",           "INTEGER DEFAULT 0");    // flat-rate count

            // Price snapshots (so history survives price changes)
            addColumnIfMissing(c, "bookings", "accom_price_cents",      "INTEGER");              // total for stay
            addColumnIfMissing(c, "bookings", "lift_pass_price_cents",  "INTEGER DEFAULT 0");
            addColumnIfMissing(c, "bookings", "lesson_price_cents",     "INTEGER DEFAULT 0");
            addColumnIfMissing(c, "bookings", "total_price_cents",      "INTEGER");

            // Helpful indexes
            s.execute("""
                CREATE INDEX IF NOT EXISTS idx_bookings_accom_dates
                ON bookings(accommodation_id, check_in, check_out, status)
            """);
            s.execute("""
                CREATE INDEX IF NOT EXISTS idx_bookings_customer
                ON bookings(customer_id, status, check_in, check_out)
            """);

            // --- Seeds
            try (var rs = s.executeQuery("SELECT COUNT(*) FROM users")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    s.execute("""
                      INSERT INTO users(username,first_name,last_name,email,password_hash,role) VALUES
                      ('master','System','Master','master@example.com','Master123!','MASTER'),
                      ('admin','Default','Admin','admin@example.com','Admin123!','ADMIN')
                    """);
                }
            }
            try (var rs = s.executeQuery("SELECT COUNT(*) FROM accommodations")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    s.execute("""
                      INSERT INTO accommodations(property_type,room_type,capacity,base_price_cents,location,status) VALUES
                      ('Hotel','Single',  1,20000,'Mt Buller Village','ACTIVE'),
                      ('Lodge','Double',  2,15000,'Northside','ACTIVE'),
                      ('Apartment','Twin',4,21000,'Southside','ACTIVE'),
                      ('Chalet','Family', 6,28000,'Northside','ACTIVE')
                    """);
                }
            }
            try (var rs = s.executeQuery("SELECT COUNT(*) FROM customers")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    s.execute("""
                        INSERT INTO customers(first_name,last_name,email,phone,skill_level) VALUES
                        ('Alice','Smith','alice@example.com','0400 111 222','Beginner'),
                        ('Bob','Jones','bob@example.com','0400 333 444','Intermediate')
                    """);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("DB bootstrap failed", e);
        }
    }

    // ---------- helpers ----------

    private static boolean customersHasLegacyNameColumn(Connection c) {
        try (var ps = c.prepareStatement("PRAGMA table_info(customers)");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                if ("name".equalsIgnoreCase(rs.getString("name"))) return true;
            }
        } catch (Exception ignored) {}
        return false;
    }

    private static boolean columnExists(Connection c, String table, String column) {
        try (var ps = c.prepareStatement("PRAGMA table_info(" + table + ")");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                if (column.equalsIgnoreCase(rs.getString("name"))) return true;
            }
        } catch (Exception ignored) {}
        return false;
    }

    /** Add a column to a table if it's not already present (SQLite). */
    private static void addColumnIfMissing(Connection c, String table, String column, String definition) {
        if (columnExists(c, table, column)) return;
        try (var s = c.createStatement()) {
            s.execute("ALTER TABLE " + table + " ADD COLUMN " + column + " " + definition);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add column " + table + "." + column + " (" + definition + ")", e);
        }
    }

    private static void recreateBookingsWithNewSchema(Connection c) {
        try (var s = c.createStatement()) {
            s.execute("ALTER TABLE bookings RENAME TO bookings_old");
            s.execute("""
                CREATE TABLE bookings(
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  accommodation_id INTEGER NOT NULL,
                  customer_id      INTEGER NOT NULL,
                  check_in  TEXT NOT NULL,
                  check_out TEXT NOT NULL,
                  nightly_price_cents INTEGER NOT NULL CHECK (nightly_price_cents >= 0),
                  status TEXT NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING','CONFIRMED','CANCELLED')),
                  created_at TEXT NOT NULL DEFAULT (datetime('now')),
                  FOREIGN KEY(accommodation_id) REFERENCES accommodations(id) ON DELETE RESTRICT,
                  FOREIGN KEY(customer_id)      REFERENCES customers(id)      ON DELETE RESTRICT,
                  CHECK (check_out > check_in)
                )""");
            // Copy best-effort (fill created_at now)
            try (var rs = s.executeQuery("""
                     SELECT id, accommodation_id, customer_id, check_in, check_out, nightly_price_cents,
                            COALESCE(status,'PENDING') AS status
                     FROM bookings_old
                """);
                 var ins = c.prepareStatement("""
                     INSERT INTO bookings(id, accommodation_id, customer_id, check_in, check_out,
                                          nightly_price_cents, status, created_at)
                     VALUES (?,?,?,?,?,?,?, datetime('now'))
                 """)) {
                while (rs.next()) {
                    ins.setInt(1, rs.getInt("id"));
                    ins.setInt(2, rs.getInt("accommodation_id"));
                    ins.setInt(3, rs.getInt("customer_id"));
                    ins.setString(4, rs.getString("check_in"));
                    ins.setString(5, rs.getString("check_out"));
                    ins.setInt(6, rs.getInt("nightly_price_cents"));
                    ins.setString(7, rs.getString("status"));
                    ins.addBatch();
                }
                ins.executeBatch();
            }
            s.execute("DROP TABLE bookings_old");
        } catch (Exception e) {
            throw new RuntimeException("Bookings schema migration failed", e);
        }
    }

    // Split "Full Name" → ["First","Last"]; single token → ["Full",""]
    private static String[] splitName(String full) {
        if (full == null || full.isBlank()) return new String[]{"",""};
        String t = full.trim();
        int p = t.lastIndexOf(' ');
        if (p <= 0 || p >= t.length()-1) return new String[]{ t, "" };
        return new String[]{ t.substring(0, p).trim(), t.substring(p+1).trim() };
    }
}
