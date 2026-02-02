package repository;

import entity.Book;
import entity.PrintedBook;
import entity.Ebook;
import entity.ReferenceBook;
import factory.BookFactory;
import edu.aitu.oop3.db.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookRepository implements CrudRepository<Book, Integer> {

    // Implement CrudRepository methods
    @Override
    public Optional<Book> findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM books WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapToBook(rs));
            }
        }
        return Optional.empty();
    }
    @Override
    public List<Book> findAll() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                books.add(mapToBook(rs));
            }
        }
        return books;
    }

    @Override
    public Book save(Book book) throws SQLException {
        String sql = "INSERT INTO books (isbn, title, author, year_published, book_type, available) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, book.getIsbn());
            pstmt.setString(2, book.getTitle());
            pstmt.setString(3, book.getAuthor());
            pstmt.setInt(4, book.getYearPublished());
            pstmt.setString(5, book.getBookType().name());
            pstmt.setBoolean(6, book.isAvailable());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                book.setId(rs.getInt(1));
            }
        }
        return book;
    }

    @Override
    public boolean delete(Integer id) throws SQLException {
        String sql = "DELETE FROM books WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM books";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    // Keep existing methods from Milestone 1
    public List<Book> findAvailableBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE available = true";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                books.add(mapToBook(rs));
            }
        }
        return books;
    }

    public void updateAvailability(int bookId, boolean available) throws SQLException {
        String sql = "UPDATE books SET available = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, available);
            pstmt.setInt(2, bookId);
            pstmt.executeUpdate();
        }
    }
    // Updated mapToBook method to handle book types
    private Book mapToBook(ResultSet rs) throws SQLException {
        String bookTypeStr = rs.getString("book_type");
        Book.BookType bookType;

        try {
            bookType = Book.BookType.valueOf(bookTypeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            bookType = Book.BookType.PRINTED; // Default
        }

        // Create appropriate book type using factory
        Book book = BookFactory.createBook(bookType);
        book.setId(rs.getInt("id"));
        book.setIsbn(rs.getString("isbn"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setYearPublished(rs.getInt("year_published"));
        book.setAvailable(rs.getBoolean("available"));

        // Set specific properties
        if (book instanceof PrintedBook) {
            int pages = rs.getInt("pages");
            if (!rs.wasNull()) {
                ((PrintedBook) book).setPages(pages);
            }
        } else if (book instanceof Ebook) {
            double fileSize = rs.getDouble("file_size_mb");
            if (!rs.wasNull()) {
                ((Ebook) book).setFileSizeMB(fileSize);
            }
        }

        return book;
    }
}