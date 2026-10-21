import database.DatabaseManager;
import database.QuestionDAO;
import database.UserDAO;
import javax.swing.SwingUtilities;
import ui.AuthView;

public class Main {
    public static void main(String[] args) {

        DatabaseManager.initializeDatabase();

        QuestionDAO questionDAO = new QuestionDAO();
        questionDAO.seedQuestionsIfEmpty();

        UserDAO userDAO = new UserDAO();
        userDAO.seedAdminIfEmpty();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AuthView();
            }
        });
    }
}