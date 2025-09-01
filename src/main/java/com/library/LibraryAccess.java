package com.library;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
        /*
         *List to store books retrieved.
         *Along with a query that retrieves data 
         *using the is_available column then arrange
         *by title.
         */

        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE is_available = TRUE ORDER BY title";

        /*using createStatement instead of prepStatement considering
         *that there will not be concurrent/heavy requests 
         */
        try(Connection conn = DatabaseManager.getConnection();
            Statement prepStmnt = conn.createStatement();
            ResultSet rs = prepStmnt.executeQuery(sql)) {
                //iterating over result set and appending books
                while(rs.next()){
                    books.add(new Book(
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getBoolean("is_available")
                        ));
                }
            }catch(SQLException e){
                System.err.println("Error! "+e.getMessage());
            }
            return books;
    }

    

    public void borrowBook(int bookId, int userId){
        String 
    }

}
