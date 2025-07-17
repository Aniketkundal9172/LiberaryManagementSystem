import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

// Custom Exceptions
class BookNotFoundException extends Exception {
    public BookNotFoundException(String message) {
        super(message);
    }
}

class DuplicateBookException extends Exception {
    public DuplicateBookException(String message) {
        super(message);
    }
}

class BookUnavailableException extends Exception {
    public BookUnavailableException(String message) {
        super(message);
    }
}

// Book Class (OOPs Core)
class Book implements Serializable {
    private final String isbn;
    private String title;
    private String author;
    private int publicationYear;
    private boolean isAvailable;
    private String borrower;

    public Book(String isbn, String title, String author, int publicationYear) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.isAvailable = true;
        this.borrower = null;
    }

    // Getters and Setters
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public int getPublicationYear() { return publicationYear; }
    public void setPublicationYear(int publicationYear) { this.publicationYear = publicationYear; }
    public boolean isAvailable() { return isAvailable; }
    public String getBorrower() { return borrower; }

    // Borrow and Return operations
    public void borrowBook(String borrower) {
        this.isAvailable = false;
        this.borrower = borrower;
    }

    public void returnBook() {
        this.isAvailable = true;
        this.borrower = null;
    }

    @Override
    public String toString() {
        return String.format("ISBN: %s | Title: %-25s | Author: %-20s | Year: %d | Status: %s",
                isbn, title, author, publicationYear, 
                isAvailable ? "Available" : "Borrowed by: " + borrower);
    }
}

// Library Management Core (Collections + Exception Handling)
class LibraryManager {
    private final Map<String, Book> booksByIsbn;
    private final List<Book> allBooks;
    private final String dataFilePath;

    public LibraryManager(String dataFilePath) {
        this.booksByIsbn = new HashMap<>();
        this.allBooks = new ArrayList<>();
        this.dataFilePath = dataFilePath;
        loadBooksFromFile();
    }

    // File Handling
    private void loadBooksFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataFilePath))) {
            List<Book> savedBooks = (List<Book>) ois.readObject();
            allBooks.addAll(savedBooks);
            allBooks.forEach(book -> booksByIsbn.put(book.getIsbn(), book));
            System.out.println("Library data loaded successfully.");
        } catch (FileNotFoundException e) {
            System.out.println("No existing data file. Starting fresh library.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading data: " + e.getMessage());
        }
    }

    public void saveBooksToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dataFilePath))) {
            oos.writeObject(allBooks);
            System.out.println("Library data saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    // Book Operations
    public void addBook(Book book) throws DuplicateBookException {
        if (booksByIsbn.containsKey(book.getIsbn())) {
            throw new DuplicateBookException("Book with ISBN " + book.getIsbn() + " already exists!");
        }
        booksByIsbn.put(book.getIsbn(), book);
        allBooks.add(book);
        saveBooksToFile();
    }

    public void removeBook(String isbn) throws BookNotFoundException {
        if (!booksByIsbn.containsKey(isbn)) {
            throw new BookNotFoundException("Book with ISBN " + isbn + " not found!");
        }
        Book book = booksByIsbn.remove(isbn);
        allBooks.remove(book);
        saveBooksToFile();
    }

    public void updateBook(String isbn, String title, String author, int year) 
            throws BookNotFoundException {
        Book book = getBookByIsbn(isbn);
        book.setTitle(title);
        book.setAuthor(author);
        book.setPublicationYear(year);
        saveBooksToFile();
    }

    public void borrowBook(String isbn, String borrower) 
            throws BookNotFoundException, BookUnavailableException {
        Book book = getBookByIsbn(isbn);
        if (!book.isAvailable()) {
            throw new BookUnavailableException("Book is already borrowed!");
        }
        book.borrowBook(borrower);
        saveBooksToFile();
    }

    public void returnBook(String isbn) throws BookNotFoundException {
        Book book = getBookByIsbn(isbn);
        book.returnBook();
        saveBooksToFile();
    }

    // Search Operations (String Manipulation + Collections)
    public List<Book> searchByTitle(String keyword) {
        return allBooks.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Book> searchByAuthor(String keyword) {
        return allBooks.stream()
                .filter(book -> book.getAuthor().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Book getBookByIsbn(String isbn) throws BookNotFoundException {
        Book book = booksByIsbn.get(isbn);
        if (book == null) {
            throw new BookNotFoundException("Book with ISBN " + isbn + " not found!");
        }
        return book;
    }

    public List<Book> getAllBooks() {
        return Collections.unmodifiableList(allBooks);
    }

    // Keyword scoring for search relevance
    public Map<Book, Integer> searchWithScoring(String query) {
        Map<Book, Integer> scores = new HashMap<>();
        String[] keywords = query.toLowerCase().split("\\s+");

        for (Book book : allBooks) {
            int score = 0;
            String bookText = (book.getTitle() + " " + book.getAuthor() + " " + book.getIsbn()).toLowerCase();
            
            for (String keyword : keywords) {
                if (bookText.contains(keyword)) {
                    score += 3; // Base score for keyword match
                    
                    // Higher score for title matches
                    if (book.getTitle().toLowerCase().contains(keyword)) {
                        score += 2;
                    }
                }
            }
            
            if (score > 0) {
                scores.put(book, score);
            }
        }
        
        return scores.entrySet().stream()
                .sorted(Map.Entry.<Book, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
}

// Main Application with Menu System
public class LibrarySystem {
    private static final Scanner scanner = new Scanner(System.in);
    private static final LibraryManager library = new LibraryManager("library_data.ser");

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n===== Library Management System =====");
            System.out.println("1. Add New Book");
            System.out.println("2. Search Books");
            System.out.println("3. Update Book Details");
            System.out.println("4. Remove Book");
            System.out.println("5. Borrow Book");
            System.out.println("6. Return Book");
            System.out.println("7. List All Books");
            System.out.println("8. Advanced Search");
            System.out.println("9. Exit");
            System.out.print("Enter choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                
                switch (choice) {
                    case 1 -> addNewBook();
                    case 2 -> searchBooks();
                    case 3 -> updateBook();
                    case 4 -> removeBook();
                    case 5 -> borrowBook();
                    case 6 -> returnBook();
                    case 7 -> listAllBooks();
                    case 8 -> advancedSearch();
                    case 9 -> {
                        System.out.println("Exiting system...");
                        return;
                    }
                    default -> System.out.println("Invalid choice!");
                }
            } catch (NumberFormatException e) {
                System.err.println("Please enter a valid number!");
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    private static void addNewBook() throws Exception {
        System.out.println("\n--- Add New Book ---");
        System.out.print("Enter ISBN: ");
        String isbn = scanner.nextLine();
        
        System.out.print("Enter Title: ");
        String title = scanner.nextLine();
        
        System.out.print("Enter Author: ");
        String author = scanner.nextLine();
        
        System.out.print("Enter Publication Year: ");
        int year = Integer.parseInt(scanner.nextLine());
        
        library.addBook(new Book(isbn, title, author, year));
        System.out.println("Book added successfully!");
    }

    private static void searchBooks() {
        System.out.println("\n--- Search Books ---");
        System.out.print("Enter search term: ");
        String term = scanner.nextLine();
        
        System.out.println("\nMatching by Title:");
        library.searchByTitle(term).forEach(System.out::println);
        
        System.out.println("\nMatching by Author:");
        library.searchByAuthor(term).forEach(System.out::println);
    }

    private static void updateBook() throws Exception {
        System.out.println("\n--- Update Book ---");
        System.out.print("Enter ISBN of book to update: ");
        String isbn = scanner.nextLine();
        
        System.out.print("Enter new title: ");
        String title = scanner.nextLine();
        
        System.out.print("Enter new author: ");
        String author = scanner.nextLine();
        
        System.out.print("Enter new publication year: ");
        int year = Integer.parseInt(scanner.nextLine());
        
        library.updateBook(isbn, title, author, year);
        System.out.println("Book updated successfully!");
    }

    private static void removeBook() throws Exception {
        System.out.println("\n--- Remove Book ---");
        System.out.print("Enter ISBN of book to remove: ");
        String isbn = scanner.nextLine();
        
        library.removeBook(isbn);
        System.out.println("Book removed successfully!");
    }

    private static void borrowBook() throws Exception {
        System.out.println("\n--- Borrow Book ---");
        System.out.print("Enter ISBN of book to borrow: ");
        String isbn = scanner.nextLine();
        
        System.out.print("Enter borrower name: ");
        String borrower = scanner.nextLine();
        
        library.borrowBook(isbn, borrower);
        System.out.println("Book borrowed successfully!");
    }

    private static void returnBook() throws Exception {
        System.out.println("\n--- Return Book ---");
        System.out.print("Enter ISBN of book to return: ");
        String isbn = scanner.nextLine();
        
        library.returnBook(isbn);
        System.out.println("Book returned successfully!");
    }

    private static void listAllBooks() {
        System.out.println("\n--- All Books ---");
        library.getAllBooks().forEach(System.out::println);
    }

    private static void advancedSearch() {
        System.out.println("\n--- Advanced Search ---");
        System.out.print("Enter search query: ");
        String query = scanner.nextLine();
        
        Map<Book, Integer> results = library.searchWithScoring(query);
        
        if (results.isEmpty()) {
            System.out.println("No matching books found!");
        } else {
            System.out.println("\nSearch Results (Relevance Score):");
            results.forEach((book, score) -> 
                System.out.println(book + " | Relevance: " + score)
            );
        }
    }
}