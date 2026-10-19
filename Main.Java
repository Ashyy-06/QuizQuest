import models.*;

public class Main {
    public static void main(String[] args) {
        // Test creating and displaying a True/False question
        Question q = new TrueFalseQuestion("1", "Java is a programming language.", 10, true);
        
        q.displayQuestion();
        boolean result = q.evaluateAnswer(true);
        
        System.out.println("Evaluation Result: " + result); // Should print true
    }
}


