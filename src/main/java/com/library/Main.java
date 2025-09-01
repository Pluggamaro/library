package com.library;

import java.util.List;
import java.util.Scanner;

public class Main {

    private static LibraryAccess dataAccess = new LibraryAccess();
    private static Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {

        //loop control variable
        boolean stop = false;

        while(!stop){
            printMenu();
            int choice = getChoice();

            switch(choice){
                case 1: viewBooks(); break;
                case 2: addBook(); break;
                case 3: borrowBook(); break;
                case 4: returnBook(); break;
                case 5: System.out.println("Exiting Library...");
                        stop = true;scanner.close(); break;
                default: System.out.println("Invalid Choice!");
            }
        }
        
    }

    private static void printMenu(){
        System.out.println("========Library Management System========");
        System.out.println("           1. View Books                 ");
        System.out.println("           2. Add Book                   ");
        System.out.println("           3. Borrow                     ");
        System.out.println("           4. Return                     ");
        System.out.println("           5. EXIT                       ");
        System.out.print(          "Make a choice (1-5) :");
    }

    private static int getChoice(){
        try{
            return Integer.parseInt(scanner.nextLine());
        }catch(NumberFormatException e){
            return -1;
        }
    }

    private static void viewBooks(){
        List<Book> books = dataAccess.getAvailableBooks();

        if(books.isEmpty()){
            System.err.println("NO BOOKS FOUND!");
        }else{
            System.out.println("==== BOOKS FOUND ====");
            books.forEach(System.out::println);
        }
    }

    private static void addBook(){
        System.out.print("Enter TITLE: ");
        String title = scanner.nextLine();
        System.out.print("Enter Book AUTHOR: ");
        String author = scanner.nextLine();
        
        dataAccess.addBook(title, author);

    }

    private static void borrowBook(){
        System.out.print("Enter your USER ID: ");
        int userId = Integer.parseInt(scanner.nextLine());
        System.out.print("Now, enter BOOK ID: ");
        int bookId = Integer.parseInt(scanner.nextLine());

        dataAccess.borrowBook(bookId, userId);
    }

    private static void returnBook(){
        System.out.print("Enter your User ID: ");
        int userId = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter ID of Book You Are Returning: ");
        int bookId = Integer.parseInt(scanner.nextLine());

        dataAccess.bookReturn(bookId, userId);
    }

}