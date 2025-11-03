package com.rmit.mtbuller.db;

import java.util.*;
import java.util.stream.Collectors;

public class Table {
    private final String name;

    public Table(String name) { this.name = name; }


    public int insert(LinkedHashMap<String, Object> values) {
        if (values == null || values.isEmpty()) return 0;
        String cols = String.join(",", values.keySet());
        String qs = values.keySet().stream().map(k -> "?").collect(Collectors.joining(","));
        String sql = "INSERT INTO " + name + "(" + cols + ") VALUES(" + qs + ")";
        return Sql.update(sql, values.values().toArray());
    }


    public int update(LinkedHashMap<String, Object> values, String where, Object... whereParams) {
        if (values == null || values.isEmpty()) return 0;
        String set = values.keySet().stream()
                .map(k -> k + "=?")
                .collect(Collectors.joining(", "));
        String sql = "UPDATE " + name + " SET " + set
                + (where == null || where.isBlank() ? "" : " WHERE " + where);
        Object[] params = concat(values.values().toArray(), whereParams);
        return Sql.update(sql, params);
    }


    public int update(String setClause, String whereClause, Object... params) {
        if (setClause == null || setClause.isBlank())
            throw new IllegalArgumentException("setClause must not be blank");
        String sql = "UPDATE " + name + " SET " + setClause
                + (whereClause == null || whereClause.isBlank() ? "" : " WHERE " + whereClause);
        return Sql.update(sql, params);
    }


    public int delete(String where, Object... whereParams) {
        String sql = "DELETE FROM " + name
                + (where == null || where.isBlank() ? "" : " WHERE " + where);
        return Sql.update(sql, whereParams);
    }


    public <T> List<T> select(String columns, String whereOrderBy, RowMapper<T> rm, Object... params) {
        String sql = "SELECT " + (columns == null || columns.isBlank() ? "*" : columns)
                + " FROM " + name
                + (whereOrderBy == null || whereOrderBy.isBlank() ? "" : " " + whereOrderBy);
        return Sql.list(sql, rm, params);
    }

    // ---------- helpers ----------
    private static Object[] concat(Object[] a, Object[] b) {
        if (a == null || a.length == 0) return b == null ? new Object[0] : b.clone();
        if (b == null || b.length == 0) return a.clone();
        Object[] r = Arrays.copyOf(a, a.length + b.length);
        System.arraycopy(b, 0, r, a.length, b.length);
        return r;
    }
}
