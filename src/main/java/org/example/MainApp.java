package org.example;

import java.sql.*;

public class MainApp {
    private static Connection connection;
    public static Statement stmt;
    private static PreparedStatement psInsert;

    public static void main(String[] args) {
        try {
            connect();
            clearTable();
            rollback();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }

    }

    public static void rollback() throws SQLException {
        stmt.executeUpdate("INSERT INTO students (name, score) VALUES ('Bob1', 21)");
        Savepoint sp1 = connection.setSavepoint();
        stmt.executeUpdate("INSERT INTO students (name, score) VALUES ('Bob2', 221)");
        connection.rollback(sp1);
        stmt.executeUpdate("INSERT INTO students (name, score) VALUES ('Bob3', 2221)");
        connection.commit();
    }

    public static void batchFillTable() throws SQLException {
        connection.setAutoCommit(false);
        for (int i = 0; i < 1000; i++) {
            psInsert.setString(1, "Bob"+i);
            psInsert.setInt(2, i*15%100);
            psInsert.addBatch();
        }
        psInsert.executeBatch();
        connection.setAutoCommit(true);
    }

    public static void fillTable() throws SQLException {
        connection.setAutoCommit(false);
        for (int i = 0; i < 1000; i++) {
            psInsert.setString(1, "Bob"+i);
            psInsert.setInt(2, i*15%100);
            psInsert.executeUpdate();
        }
        connection.setAutoCommit(true);
    }

    public static void prepareAllStatements() throws SQLException {
        psInsert = connection.prepareStatement("INSERT INTO students (name, score) VALUES (?, ?)");
    }

    //CRUD create read update delete
    public static void exSelect() throws SQLException{
        ResultSet rs = stmt.executeQuery("SELECT name, score FROM students WHERE score > 49;");
        while (rs.next()){
            System.out.println(rs.getString("name") + " "+ rs.getInt("score"));
        }
        rs.close();
    }
    public static void clearTable() throws SQLException{
        stmt.executeUpdate("DELETE FROM students;");
    }
    public static void exDelete() throws SQLException{
        stmt.executeUpdate("DELETE FROM students WHERE score == 100;");
    }
    public static void exUpdate() throws SQLException{
        stmt.executeUpdate("UPDATE students SET score = 100 WHERE score >100;");
    }
    public static void exInsert() throws SQLException {
        stmt.executeUpdate("INSERT INTO students (name, score) VALUES ('Bob5', 221)");
        stmt.executeUpdate("INSERT INTO students (name, score)" +
                "VALUES ('Bob44', 11), ('Bob8', 241), ('Bob9', 21);");
    }
    public static void connect() throws Exception {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:main.db");
        stmt = connection.createStatement();
    }

    public static void disconnect() {
        try {
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
