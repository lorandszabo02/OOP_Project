package controllers;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    private final String connectionURL;
    private final String driver;
    private final String username;
    private final String password;


    public DatabaseConnection(){
        connectionURL = "jdbc:postgresql://localhost:5432/travel";
        driver = "org.postgresql.Driver";
        username = "postgres";
        password = "postgres";
    }

    public Connection getConnection() throws Exception {
        Class.forName(driver);
        return DriverManager.getConnection(connectionURL, username, password);
    }

    public static void main(String[] args) {
        DatabaseConnection db = new DatabaseConnection();
        try {
            Connection connection = db.getConnection();
            System.out.println("Database connected successfully");
            connection.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
