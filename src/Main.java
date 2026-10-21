import database.DatabaseManager;
import database.QuestionDAO;
import database.UserDAO;
import javax.swing.SwingUtilities;
import ui.AuthView;

public class Main 
{
    public static void main(String[] args) 
    {

        DatabaseManager.getInstance().initializeDatabase();

        QuestionDAO questionDAO = new QuestionDAO();
        questionDAO.seedDefaultQuestions();
        UserDAO userDAO = new UserDAO();
        userDAO.seedAdminIfEmpty();

        SwingUtilities.invokeLater(new Runnable() 
        {
            @Override
            public void run() 
            {
                try 
                {
                    AuthView view = new AuthView();
                    view.setVisible(true);
                } catch (Throwable t) 
                {
                    System.err.println("CRASH DURING AUTHVIEW LAUNCH:");
                    t.printStackTrace();
                }
            }
        });
    }
}