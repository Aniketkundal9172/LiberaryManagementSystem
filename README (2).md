# 📚 Library Management System (Java)

This project is a **Library Management System** built in **Java** that uses **OOP concepts, Collections, Custom Exceptions, and File Handling**.  
It allows you to manage books in a library with features like adding, removing, updating, borrowing, returning, searching, and advanced search with scoring.

---

## ✨ Features

✅ **Add New Books** with ISBN, title, author, and publication year  
✅ **Update Book Details** (title, author, publication year)  
✅ **Remove Books** by ISBN  
✅ **Borrow and Return Books** with borrower tracking  
✅ **Search Books** by title or author  
✅ **Advanced Search** with relevance scoring (based on keywords)  
✅ **Persistent Storage** (data saved in `library_data.ser` file)  
✅ **Custom Exceptions** for better error handling:
- `BookNotFoundException`
- `DuplicateBookException`
- `BookUnavailableException`

---

## 🏗️ Tech Stack

- **Java (JDK 17 or above recommended)**
- **OOPs principles**
- **Collections Framework**
- **Serialization (File Handling)**

---

## 📂 Project Structure

```
LibrarySystem.java       # Main class with menu-driven application
LibraryManager.java      # Core manager handling book operations and file handling
Book.java                # Book entity class with borrow/return logic
Custom Exceptions        # BookNotFoundException, DuplicateBookException, BookUnavailableException
library_data.ser         # Auto-generated file storing serialized book data
```

---

## 🚀 How to Run

1. **Clone or download** this project.
2. Open in your favorite Java IDE (IntelliJ, Eclipse, or VS Code) **or** compile in terminal.
3. Ensure your Java version is 17 or above.
4. Compile and run:

```bash
javac LibrarySystem.java
java LibrarySystem
```

5. A menu-driven console application will appear.

---

## 📋 Menu Options

| Option | Description |
|--------|-------------|
| 1 | Add New Book |
| 2 | Search Books |
| 3 | Update Book Details |
| 4 | Remove Book |
| 5 | Borrow Book |
| 6 | Return Book |
| 7 | List All Books |
| 8 | Advanced Search (with relevance scoring) |
| 9 | Exit |

---

## 💾 Data Persistence

- All book records are **serialized** and stored in `library_data.ser`.
- On every add, update, borrow, return, or delete operation, the data file is automatically updated.

---

## 🧪 Sample Input & Output

**Example – Add Book**
```
Enter ISBN: 12345
Enter Title: Java Basics
Enter Author: John Doe
Enter Publication Year: 2024
Book added successfully!
```

**Example – Borrow Book**
```
Enter ISBN of book to borrow: 12345
Enter borrower name: Alice
Book borrowed successfully!
```

---

## 🔧 Future Enhancements

- ✅ Add a GUI using JavaFX or Swing
- ✅ Implement categories or genres
- ✅ Add user accounts with authentication
- ✅ Export reports (CSV/PDF)

---

## 👨‍💻 Author

**Developed by:** Aniket Kundal  
📌 Feel free to fork and enhance this project!

---

✨ *Happy Coding!*
