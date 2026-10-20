package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:quizquest.db";

    private static volatile DatabaseManager instance;

    private DatabaseManager() {
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null) {
                    instance = new DatabaseManager();
                }
            }
        }
        return instance;
    }

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static Connection getConnection() throws SQLException {
        return getInstance().connect();
    }

    public static void initializeDatabase() {
        getInstance().initTables();
    }

    public void initTables() {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT UNIQUE NOT NULL, "
                + "password TEXT NOT NULL, "
                + "role TEXT NOT NULL"
                + ");";

        String createQuestionsTable = "CREATE TABLE IF NOT EXISTS questions ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "topic TEXT NOT NULL, "
                + "type TEXT NOT NULL, "
                + "question_text TEXT NOT NULL, "
                + "options TEXT, "
                + "correct_answer TEXT NOT NULL"
                + ");";

        String createResultsTable = "CREATE TABLE IF NOT EXISTS quiz_results ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id INTEGER NOT NULL, "
                + "topic TEXT NOT NULL, "
                + "score INTEGER NOT NULL, "
                + "total INTEGER NOT NULL, "
                + "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY(user_id) REFERENCES users(id)"
                + ");";

        Connection conn = null;
        Statement stmt = null;

        try {
            conn = connect();
            stmt = conn.createStatement();

            stmt.execute(createUsersTable);
            stmt.execute(createQuestionsTable);
            stmt.execute(createResultsTable);

            System.out.println("Database and tables initialized successfully!");
        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Failed to close statement: " + e.getMessage());
            }

            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Failed to close connection: " + e.getMessage());
            }
        }
    }
}