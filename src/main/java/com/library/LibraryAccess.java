package com.library;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
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

    /*
     *borrow book uses a query that checks the availability column
     *using a unique id. Upon success, all that is put into the 
     *transactions table.   
     */    

    public void borrowBook(int bookId, int userId){
        String availabilityCheck = "SELECT is_available FROM books WHERE book_id = ?";
        //if found, set to false for availability
        String bookUpdate = "UPDATE books SET is_available = FALSE WHERE book_id = ?";
        String insertIntoTransactions = "INSERT INTO transactions(book_id, user_id, borrow_date) VALUE(?,?,?)";

        Connection conn = null;

        try{
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false);
            //checking availability
            try(PreparedStatement checkStmnt = conn.prepareStatement(availabilityCheck)){
                checkStmnt.setInt(1, bookId);
                ResultSet rs = checkStmnt.executeQuery();
                if(!rs.next() || !rs.getBoolean("is_available")){
                    //throwing exeption
                    throw new SQLException("Not available for borrowing!");
                }
            }
            
            //updating availability
            try(PreparedStatement updtStmnt = conn.prepareStatement(bookUpdate)){
                updtStmnt.setInt(1, bookId);
                updtStmnt.executeUpdate();
            }

            try(PreparedStatement insrtStmnt = conn.prepareStatement(insertIntoTransactions)){
                insrtStmnt.setInt(1, bookId);
                insrtStmnt.setInt(2, userId);
                insrtStmnt.setDate(3, Date.valueOf(LocalDate.now()));
                insrtStmnt.executeUpdate();
            }

            //upon success, then commiting manually to transaction
            conn.commit();
            System.out.println("Book borrowed successfully!");
        }catch(SQLException e){
            System.err.println("Failed! "+ e.getMessage());
            //rolling back changes to avoid false or partial updates
            try{
                if(conn != null){
                    conn.rollback();
                }
            }catch(SQLException ex){
                System.err.println("Error rolling back " + ex.getMessage());
            }
        }finally {
            try{
                if(conn != null){
                    conn.setAutoCommit(true);
                    conn.close();
                }
            }catch(SQLException e){
                System.err.println("Connection NOT closed! " + e.getMessage());
            }
        }
    }

    //return book resets is_available to true and sets return date on transactions

    public void bookReturn(int bookId, int userId){
        String updateTrans = "UPDATE transactions SET return_date = ? WHERE book_id = ? AND user_id = ? AND return_date IS NULL";
        String updateBook = "UPDATE books SET is_available = TRUE WHERE book_id = ?";
        Connection conn = null;
        
        try{
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false);

            //defining variable to track success of query
            int rowsAffected;

            try(PreparedStatement updateTransStmnt = conn.prepareStatement(updateTrans)){
                updateTransStmnt.setDate(1, Date.valueOf(LocalDate.now()));
                updateTransStmnt.setInt(2, bookId);
                updateTransStmnt.setInt(3, userId);
                rowsAffected = updateTransStmnt.executeUpdate();

            }

            if(rowsAffected == 0){
                throw new SQLException("No record found!");
            }

            try(PreparedStatement bookUpdtStmnt = conn.prepareStatement(updateBook)){
                bookUpdtStmnt.setInt(1, bookId);
                bookUpdtStmnt.executeUpdate();
            }

            conn.commit();
            System.out.println("Success! Book Returned!");

        }catch(SQLException e){
            System.err.println("Failed " + e.getMessage());
            try{
                if(conn != null){
                    conn.rollback();

                }
            }catch(SQLException ex){
                System.err.println("Error rolling back: " + ex.getMessage());
            }finally{
                try{
                    if(conn != null){
                        conn.setAutoCommit(true);
                        conn.close();
                    }
                }catch(SQLException exe){
                    System.err.println("Connection NOT closed! ");
                }
            }
        }
    }

}
