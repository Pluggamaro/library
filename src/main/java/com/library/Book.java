package com.library;

public class Book {
    private int id;
    private String title;
    private boolean isAvailable;
    private String author;

    public Book(int id, String title, String author, boolean isAvailable){
        this.id = id;
        this.title = title;
        this.author = author;
        this.isAvailable = isAvailable;
    }

    public int getId(){return id;}
    public String getAuthor(){return author;}
    public boolean isAvailable(){ return isAvailable;}
    public String getTitle(){return title;}

    //overrideing the toString method from string class from custom representations

    @Override
    public String toString(){
        return String.format("ID: %-3d | Title: %-30s | Author: %-20s | Available: %s",
                                    id,title,author,(isAvailable?"Yes":"No") );
    }



}
