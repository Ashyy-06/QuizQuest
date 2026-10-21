import database.DatabaseManager;
import database.QuestionDAO;
import javax.swing.SwingUtilities;
import ui.AuthView;

public class Main {
    public static void main(String[] args) {
        // 1. Initialize SQLite Database & Tables (Singleton Pattern)
        DatabaseManager.initializeDatabase();

        // 2. Seed default Science and Social Science questions if empty
        QuestionDAO questionDAO = new QuestionDAO();
        questionDAO.seedQuestionsIfEmpty();

        // 3. Launch the Swing GUI on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AuthView();
            }
        });
    }
}