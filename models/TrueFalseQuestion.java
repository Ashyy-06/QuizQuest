package models;

public class TrueFalseQuestion extends Question {
    private boolean correctAnswer;

    public TrueFalseQuestion(String id, String prompt, int points, boolean correctAnswer) {
        super(id, prompt, points);
        this.correctAnswer = correctAnswer;
    }

    @Override
    public void displayQuestion() {
        System.out.println("[True/False] " + prompt + " (" + points + " pts)");
        System.out.println(" 1. True");
        System.out.println(" 2. False");
    }

    @Override
    public boolean evaluateAnswer(Object userAnswer) {
        if (userAnswer instanceof Boolean) {
            return (Boolean) userAnswer == correctAnswer;
        }
        return false;
    }

    public boolean isCorrectAnswer() { return correctAnswer; }
}

