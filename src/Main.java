import database.DatabaseManager;

public class Main {
    public static void main(String[] args) {
        System.out.println("Connecting to QuizQuest Database...");
        DatabaseManager.initializeDatabase();
    }
}