package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:quizquest.db";

    static {
        try {
            // Explicitly force the JVM to register the SQLite driver
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("CRITICAL: SQLite JDBC Driver class not found in classpath: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT UNIQUE NOT NULL, "
                + "password TEXT NOT NULL, "
                + "role TEXT NOT NULL)";

        String createQuestionsTable = "CREATE TABLE IF NOT EXISTS questions ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "topic TEXT NOT NULL, "
                + "type TEXT NOT NULL, "
                + "question_text TEXT NOT NULL, "
                + "options TEXT, "
                + "correct_answer TEXT NOT NULL)";

        String createResultsTable = "CREATE TABLE IF NOT EXISTS quiz_results ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT NOT NULL, "
                + "topic TEXT NOT NULL, "
                + "score INTEGER NOT NULL, "
                + "total INTEGER NOT NULL, "
                + "date_taken TEXT NOT NULL)";

        Connection conn = null;
        Statement stmt = null;

        try {
            conn = getConnection();
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
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Failed to close DB resources: " + e.getMessage());
            }
        }
    }
}