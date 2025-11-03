package com.rmit.mtbuller.db;

import java.nio.file.*;
import java.sql.*;

public class Database {
    private static final Path DB_PATH  = Path.of(System.getProperty("user.dir"), "mtbuller.db");
    private static final String URL   = "jdbc:sqlite:" + DB_PATH;

    public static Connection get() throws SQLException {
        try { Files.createDirectories(DB_PATH.getParent()); } catch (Exception ignored) {}
        Connection c = DriverManager.getConnection(URL);
        try (Statement s = c.createStatement()) {
            s.execute("PRAGMA foreign_keys=ON");
            s.execute("PRAGMA busy_timeout=3000");
            s.execute("PRAGMA journal_mode=WAL");
        }
        return c;
    }
}
