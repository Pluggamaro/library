package com.library;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/*
 * This class is the blueprint of 
 * operations performed to the database
 * it is a database access object
 */

public class LibraryAccess {
    
    public void addBook(String title, String author){
        //query for adding to database
        String sql = "INSERT INTO books(title, author) VALUES(?, ?)";
        //using try-with-resources for resource managemet from leakage
        try(Connection conn = DatabaseManager.getConnection();
            PreparedStatement prepStmnt = conn.prepareStatement(sql)){
                prepStmnt.setString(1, title);
                prepStmnt.setString(2, author);
                //actioning/executing prepared statements
                prepStmnt.executeUpdate();
                System.out.println("Success!");
            }catch(SQLException e){
                System.err.println("Error! " + e.getMessage());
            }
    }

    public List<Book> getAvailableBooks(){
        
    }

}
