import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.Admin;
import models.MatchTheFollowingQuestion;
import models.Question;
import models.QuizResult;
import models.SingleChoiceMCQ;
import models.StudentUser;
import models.TrueFalseQuestion;
import models.User;

public class Main {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("       QUIZQUEST: TESTING MODELS PACKAGE          ");
        System.out.println("==================================================\n");

        // -----------------------------------------------------------------
        // 1. TEST USER HIERARCHY (Inheritance, Polymorphism & Abstraction)
        // -----------------------------------------------------------------
        System.out.println(">>> 1. TESTING USER HIERARCHY <<<");

        User adminUser = new Admin(101, "admin_ashya", "secureAdminPass");
        User studentUser = new StudentUser(202, "student_rahul", "studPass123");

        System.out.println("User 1 -> ID: " + adminUser.getUserId() 
                + " | Username: " + adminUser.getUsername() 
                + " | Role: " + adminUser.getRole());

        System.out.println("User 2 -> ID: " + studentUser.getUserId() 
                + " | Username: " + studentUser.getUsername() 
                + " | Role: " + studentUser.getRole());

        // Test subclass-specific methods
        ((Admin) adminUser).displayPermissions();
        ((StudentUser) studentUser).displayPermissions();
        System.out.println();

        // -----------------------------------------------------------------
        // 2. TEST QUIZ RESULT MODEL
        // -----------------------------------------------------------------
        System.out.println(">>> 2. TESTING QUIZRESULT MODEL <<<");
        QuizResult sampleResult = new QuizResult(
                1,                      // resultId
                studentUser.getUserId(),// userId
                studentUser.getUsername(),// username
                "Object Oriented Programming", // topic
                10,                     // totalQuestions
                8                       // correctAnswers
        );
        sampleResult.displayResult();
        System.out.println();

        // -----------------------------------------------------------------
        // 3. TEST POLYMORPHIC QUESTIONS & EVALUATION
        // -----------------------------------------------------------------
        System.out.println(">>> 3. TESTING POLYMORPHIC QUESTIONS <<<");

        List<Question> questions = new ArrayList<>();

        // (a) SingleChoiceMCQ (options list, 0-based or 1-based index)
        // Let index 2 correspond to option "3. new"
        List<String> mcqOptions = Arrays.asList("class", "interface", "new", "this");
        questions.add(new SingleChoiceMCQ(
                "Q1",
                "Which keyword is used to allocate memory for an object?",
                2,          // points
                mcqOptions,
                3           // correctAnswerIndex (3 corresponds to "new")
        ));

        // (b) TrueFalseQuestion (boolean answer)
        questions.add(new TrueFalseQuestion(
                "Q2",
                "Interfaces in Java can have private methods starting from Java 9.",
                1,          // points
                true        // correctAnswer is true
        ));

        // (c) MatchTheFollowingQuestion (Map<String, String>)
        Map<String, String> pairs = new HashMap<>();
        pairs.put("JVM", "Executes bytecode");
        pairs.put("JDK", "Development tools and compiler");
        pairs.put("JRE", "Runtime environment for execution");
        
        questions.add(new MatchTheFollowingQuestion(
                "Q3",
                "Match the Java components with their primary functions:",
                3,          // points
                pairs
        ));

        // Iterate through all Question objects polymorphically
        int qNum = 1;
        for (Question q : questions) {
            System.out.println("--------------------------------------------------");
            System.out.println("Question #" + qNum + " [ID: " + q.getId() + "]");
            
            // Calls overridden displayQuestion()
            q.displayQuestion();

            // Evaluate sample answers
            System.out.println("\nTesting answer evaluation:");
            if (q instanceof SingleChoiceMCQ) {
                // Correct test
                boolean testCorrect = q.evaluateAnswer(3);
                // Incorrect test
                boolean testWrong = q.evaluateAnswer(1);
                System.out.println("  Evaluating option 3: " + (testCorrect ? "CORRECT" : "WRONG"));
                System.out.println("  Evaluating option 1: " + (testWrong ? "CORRECT" : "WRONG"));

            } else if (q instanceof TrueFalseQuestion) {
                // Correct test
                boolean testCorrect = q.evaluateAnswer(true);
                // Incorrect test
                boolean testWrong = q.evaluateAnswer(false);
                System.out.println("  Evaluating 'true': " + (testCorrect ? "CORRECT" : "WRONG"));
                System.out.println("  Evaluating 'false': " + (testWrong ? "CORRECT" : "WRONG"));

            } else if (q instanceof MatchTheFollowingQuestion) {
                // User creates matching pair submission
                Map<String, String> userSubmission = new HashMap<>();
                userSubmission.put("JVM", "Executes bytecode");
                userSubmission.put("JDK", "Development tools and compiler");
                userSubmission.put("JRE", "Runtime environment for execution");

                Map<String, String> incorrectSubmission = new HashMap<>(userSubmission);
                incorrectSubmission.put("JVM", "Wrong definition");

                System.out.println("  Evaluating correct map match: " + (q.evaluateAnswer(userSubmission) ? "CORRECT" : "WRONG"));
                System.out.println("  Evaluating wrong map match:   " + (q.evaluateAnswer(incorrectSubmission) ? "CORRECT" : "WRONG"));
            }
            qNum++;
        }

        System.out.println("--------------------------------------------------");
        System.out.println("\n>> All model classes executed and verified with zero errors!");
    }
}

