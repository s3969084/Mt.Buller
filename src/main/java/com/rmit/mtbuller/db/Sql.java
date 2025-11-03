package com.rmit.mtbuller.db;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Sql {
    private Sql() {}

    public static int update(String sql, Object... params) {
        try (Connection c = Database.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, params);
            return ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public static <T> List<T> list(String sql, RowMapper<T> rm, Object... params) {
        try (Connection c = Database.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                List<T> out = new ArrayList<>();
                while (rs.next()) out.add(rm.map(rs));
                return out;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public static <T> T single(String sql, RowMapper<T> rm, Object... params) {
        var list = list(sql, rm, params);
        return list.isEmpty() ? null : list.get(0);
    }

    private static void bind(PreparedStatement ps, Object... params)
        throws SQLException {
            for (int i = 0; i < params.length; i++) {
                Object v = params[i];
                if (v instanceof Enum<?> e) v = e.name();
                if (v instanceof LocalDate d) v = d.toString();
                ps.setObject(i + 1, v);
            }
    }
}
