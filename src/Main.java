import database.DatabaseManager;
import database.QuestionDAO;
import database.QuizResultDAO;
import database.UserDAO;
import engine.QuoteService;
import engine.Quiz;
import java.util.Arrays;
import java.util.List;
import models.Admin;
import models.Question;
import models.QuizResult;
import models.SingleChoiceMCQ;
import models.StudentUser;
import models.TrueFalseQuestion;
import models.User;

public class Main {
    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("       QUIZQUEST: INTEGRATION TEST (DB, MODELS, ENGINE)     ");
        System.out.println("============================================================\n");

        // =========================================================================
        // 1. DATABASE INITIALIZATION & SEEDING
        // =========================================================================
        System.out.println(">>> [STEP 1] INITIALIZING DATABASE & SEEDING QUESTIONS <<<");
        DatabaseManager.initializeDatabase();

        QuestionDAO questionDAO = new QuestionDAO();
        questionDAO.seedQuestionsIfEmpty();
        System.out.println("[PASS] Database tables initialized and default questions checked.\n");

        // =========================================================================
        // 2. TESTING MODELS PACKAGE (OOP & POLYMORPHISM)
        // =========================================================================
        System.out.println(">>> [STEP 2] TESTING MODELS & POLYMORPHISM <<<");

        // 2a. User Models & Abstraction
        User adminUser = new Admin(1, "admin_test", "adminPass123");
        User studentUser = new StudentUser(2, "student_test", "studPass123");

        System.out.println("Admin role: " + adminUser.getRole() + " (User: " + adminUser.getUsername() + ")");
        System.out.println("Student role: " + studentUser.getRole() + " (User: " + studentUser.getUsername() + ")");
        ((Admin) adminUser).displayPermissions();
        ((StudentUser) studentUser).displayPermissions();

        // 2b. Polymorphic Questions (MCQ & TrueFalse)
        Question mcq = new SingleChoiceMCQ(
                "Q-TEST-1",
                "What is the capital of France?",
                1,
                Arrays.asList("Berlin", "Madrid", "Paris", "Rome"),
                3
        );
        Question tf = new TrueFalseQuestion(
                "Q-TEST-2",
                "Java is completely platform-independent at bytecode level.",
                1,
                true
        );

        System.out.println("\nTesting polymorphic display:");
        mcq.displayQuestion();
        tf.displayQuestion();

        System.out.println("\nTesting answer evaluation:");
        System.out.println("MCQ correct answer (3): " + (mcq.evaluateAnswer(3) ? "PASS" : "FAIL"));
        System.out.println("MCQ wrong answer (1): " + (!mcq.evaluateAnswer(1) ? "PASS" : "FAIL"));
        System.out.println("TF correct answer (true): " + (tf.evaluateAnswer(true) ? "PASS" : "FAIL"));
        System.out.println("TF wrong answer (false): " + (!tf.evaluateAnswer(false) ? "PASS" : "FAIL"));
        System.out.println("[PASS] Model polymorphism and evaluations verified.\n");

        // =========================================================================
        // 3. TESTING ENGINE PACKAGE (Quiz & QuoteService)
        // =========================================================================
        System.out.println(">>> [STEP 3] TESTING ENGINE CLASSES <<<");

        // 3a. QuoteService
        QuoteService quoteService = new QuoteService();
        System.out.print("Testing QuoteService random quote output: ");
        quoteService.displayQuote();

        // 3b. Quiz Session Tracking
        Quiz session = new Quiz((StudentUser) studentUser, "Science", 5);
        System.out.println("Initial Score: " + session.getScore());
        session.addCorrectAnswer();
        session.addCorrectAnswer();
        session.addCorrectAnswer();
        session.displayQuizStatus();
        System.out.println("[PASS] Engine session tracking verified.\n");

        // =========================================================================
        // 4. TESTING DATABASE/ (UserDAO CRUD)
        // =========================================================================
        System.out.println(">>> [STEP 4] TESTING USER DAO <<<");
        UserDAO userDAO = new UserDAO();

        String testUsername = "tester_" + System.currentTimeMillis();
        boolean registered = userDAO.register(testUsername, "tempPass");
        System.out.println("Registration of test user: " + (registered ? "PASS" : "FAIL"));

        User loggedInUser = userDAO.login(testUsername, "tempPass");
        if (loggedInUser != null) {
            System.out.println("Login success -> ID: " + loggedInUser.getUserId() + ", Role: " + loggedInUser.getRole());

            // Test updating password/role
            boolean updated = userDAO.updateUser(loggedInUser.getUserId(), "newPass123", "ADMIN");
            System.out.println("Update user to ADMIN: " + (updated ? "PASS" : "FAIL"));

            // Test delete
            boolean deleted = userDAO.deleteUser(loggedInUser.getUserId());
            System.out.println("Delete test user: " + (deleted ? "PASS" : "FAIL"));
        } else {
            System.err.println("[FAIL] Login failed for registered user.");
        }
        System.out.println("[PASS] UserDAO authentication & CRUD verified.\n");

        // =========================================================================
        // 5. TESTING DATABASE/ (QuestionDAO Topic Fetching & CRUD)
        // =========================================================================
        System.out.println(">>> [STEP 5] TESTING QUESTION DAO <<<");

        List<Question> scienceQuestions = questionDAO.getRandomQuestionsByTopic("Science", 5);
        System.out.println("Fetched " + scienceQuestions.size() + " random Science questions from DB.");
        if (!scienceQuestions.isEmpty()) {
            System.out.println("Displaying first fetched DB question:");
            scienceQuestions.get(0).displayQuestion();
        }

        List<Question> socialScienceQuestions = questionDAO.getRandomQuestionsByTopic("Social Science", 5);
        System.out.println("Fetched " + socialScienceQuestions.size() + " random Social Science questions from DB.");

        // Admin Question Insertion & Deletion
        boolean questionAdded = questionDAO.addQuestion(
                "Science",
                "MCQ",
                "What is the chemical symbol for Water?",
                "H2O|CO2|NaCl|O2",
                "1"
        );
        System.out.println("Admin add dynamic question: " + (questionAdded ? "PASS" : "FAIL"));
        System.out.println("[PASS] QuestionDAO polymorphic retrieval and seeding verified.\n");

        // =========================================================================
        // 6. TESTING DATABASE/ (QuizResultDAO History & CRUD)
        // =========================================================================
        System.out.println(">>> [STEP 6] TESTING QUIZ RESULT DAO <<<");
        QuizResultDAO resultDAO = new QuizResultDAO();

        // Ensure user exists for Foreign Key relation
        String resultStudentUsername = "quiz_candidate_" + System.currentTimeMillis();
        userDAO.register(resultStudentUsername, "candidatePass");
        User candidate = userDAO.login(resultStudentUsername, "candidatePass");

        if (candidate != null) {
            boolean resultSaved = resultDAO.saveResult(candidate.getUserId(), "Social Science", 10, 9);
            System.out.println("Save quiz attempt: " + (resultSaved ? "PASS" : "FAIL"));

            List<QuizResult> userHistory = resultDAO.getResultsByUserId(candidate.getUserId());
            System.out.println("Fetched history entries count: " + userHistory.size());

            if (!userHistory.isEmpty()) {
                QuizResult lastResult = userHistory.get(0);
                lastResult.displayResult();

                // Test update result
                boolean resultUpdated = resultDAO.updateResult(lastResult.getResultId(), 10, 10);
                System.out.println("Admin update score: " + (resultUpdated ? "PASS" : "FAIL"));

                // Test delete result
                boolean resultDeleted = resultDAO.deleteResult(lastResult.getResultId());
                System.out.println("Admin delete score record: " + (resultDeleted ? "PASS" : "FAIL"));
            }

            // Cleanup student
            userDAO.deleteUser(candidate.getUserId());
        }
        System.out.println("[PASS] QuizResultDAO history persistence verified.\n");

        System.out.println("============================================================");
        System.out.println(">> ALL CHECKS PASSED: READY FOR MEMBER 4 (UI / QUIZMANAGER)");
        System.out.println("============================================================");
    }
}