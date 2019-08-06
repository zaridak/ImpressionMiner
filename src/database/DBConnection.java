package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class DBConnection { //package private class

    private static DBConnection instance;
    private Connection connection;

    private final String url = "jdbc:postgresql://localhost/postgres";
    private final String username = "postgres";
    private final String password = "root";

    private DBConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            this.connection = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException ex) {
            System.out.println("Database Connection Creation Failed : " + ex.getMessage());
        }
    }
    //package private method
    Connection getConnection() {
        return this.connection;
    }
    //package private method
    static DBConnection getInstance() throws SQLException {
        if (instance == null) {
            instance = new DBConnection();
        } else if (instance.getConnection().isClosed()) {
            instance = new DBConnection();
        }
        return instance;
    }
}