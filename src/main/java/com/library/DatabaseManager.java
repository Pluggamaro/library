package com.library;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    //hardcoding database connection parameters

    private static final String URL = "jdbc:postgresql://localhost:5434/Library";
    private static final String USER = "AdminOne";
    private static final String PASSWORD = "El3l0h31$r@3l";
    
    //connection to database
    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }


}
