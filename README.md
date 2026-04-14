# Library Management System

A Java-based command-line library management system that enables book cataloguing, borrowing, and returning — backed by a PostgreSQL database.

## Overview

The **Library Management System** is a lightweight Java utility application that provides a menu-driven interface for managing a library's book inventory and user transactions. It demonstrates core Java JDBC patterns including connection management, prepared statements, and transaction handling with rollback support.

## Key Features

- **View Books** – List all books in the library, sorted alphabetically by title, with availability status
- **Add Books** – Insert new books (title & author) into the catalogue
- **Borrow Books** – Check availability, mark a book as borrowed, and record the transaction atomically
- **Return Books** – Update the transaction record with a return date and restore book availability
- **Transaction Safety** – All multi-step operations (borrow / return) run inside explicit database transactions with automatic rollback on failure
- **Resource Safety** – Uses try-with-resources throughout to prevent connection and statement leaks

## Technology Stack

| Component      | Technology            |
|----------------|-----------------------|
| Language       | Java 17               |
| Build Tool     | Apache Maven          |
| Database       | PostgreSQL            |
| JDBC Driver    | postgresql 42.7.3     |

## Project Structure

```
library/
├── pom.xml                         # Maven build descriptor
└── src/
    └── main/
        └── java/
            └── com/
                └── library/
                    ├── Book.java           # Book model / entity
                    ├── DatabaseManager.java # JDBC connection factory
                    ├── LibraryAccess.java  # Data Access Object (DAO)
                    └── Main.java           # CLI entry point
```

## Prerequisites

- Java 17 or higher
- Apache Maven 3.6+
- PostgreSQL 12+ instance with the following schema:

```sql
CREATE TABLE books (
    book_id      SERIAL PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    author       VARCHAR(255) NOT NULL,
    is_available BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE transactions (
    transaction_id SERIAL PRIMARY KEY,
    book_id        INTEGER REFERENCES books(book_id),
    user_id        INTEGER NOT NULL,
    borrow_date    DATE NOT NULL,
    return_date    DATE
);
```

## Configuration

Database connection settings are managed in `DatabaseManager.java`. Update the constants to match your environment:

```java
private static final String URL      = "jdbc:postgresql://<host>:<port>/<database>";
private static final String USER     = "<db-user>";
private static final String PASSWORD = "<db-password>";
```

## Building

```bash
# Clone the repository
git clone https://github.com/Pluggamaro/library.git
cd library

# Compile and package
mvn package

# Run the application
java -cp target/library-1.0-SNAPSHOT.jar com.library.Main
```

## Usage

When launched, the application presents an interactive menu:

```
========Library Management System========
           1. View Books
           2. Add Book
           3. Borrow
           4. Return
           5. EXIT
Make a choice (1-5) :
```

### View Books

Select **1** to list all books with their ID, title, author, and availability:

```
==== BOOKS FOUND ====
ID: 1   | Title: Clean Code                     | Author: Robert C. Martin      | Available: Yes
ID: 2   | Title: The Pragmatic Programmer        | Author: David Thomas           | Available: No
```

### Add a Book

Select **2**, then enter the title and author when prompted:

```
Enter TITLE: Effective Java
Enter Book AUTHOR: Joshua Bloch
Success!
```

### Borrow a Book

Select **3**, then provide your user ID and the book ID:

```
Enter your USER ID: 42
Now, enter BOOK ID: 1
Book borrowed successfully!
```

### Return a Book

Select **4**, then provide your user ID and the book ID:

```
Enter your User ID: 42
Enter ID of Book You Are Returning: 1
Success! Book Returned!
```

## API Documentation

### `Book`

Immutable model representing a library book.

| Method | Return Type | Description |
|---|---|---|
| `getId()` | `int` | Returns the book's unique identifier |
| `getTitle()` | `String` | Returns the book title |
| `getAuthor()` | `String` | Returns the author name |
| `isAvailable()` | `boolean` | Returns `true` if the book can be borrowed |
| `toString()` | `String` | Formatted single-line summary |

### `DatabaseManager`

Connection factory. Call `getConnection()` inside a try-with-resources block.

| Method | Return Type | Description |
|---|---|---|
| `getConnection()` | `Connection` | Opens and returns a new JDBC connection |

### `LibraryAccess`

Data Access Object exposing all library operations.

| Method | Parameters | Description |
|---|---|---|
| `addBook(title, author)` | `String, String` | Inserts a new book into the catalogue |
| `getAvailableBooks()` | — | Returns all books ordered by title |
| `borrowBook(bookId, userId)` | `int, int` | Marks a book borrowed and records the transaction |
| `bookReturn(bookId, userId)` | `int, int` | Sets return date and restores availability |

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Commit your changes: `git commit -m "Add your feature"`
4. Push to the branch: `git push origin feature/your-feature`
5. Open a Pull Request

Please ensure your code follows the existing style and that all database operations use prepared statements to prevent SQL injection.

## License

This project is open-source. See the repository settings for the applicable license.
