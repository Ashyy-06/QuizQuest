package models;

import java.util.List;

public class SingleChoiceMCQ extends Question {
    private List<String> options;
    private int correctAnswerIndex;

    public SingleChoiceMCQ(String id, String prompt, int points, List<String> options, int correctAnswerIndex) {
        super(id, prompt, points);
        this.options = options;
        this.correctAnswerIndex = correctAnswerIndex;
    }

    @Override
    public void displayQuestion() {
        System.out.println("[Single Choice] " + prompt + " (" + points + " pts)");
        for (int i = 0; i < options.size(); i++) {
            System.out.println(" " + (i + 1) + ". " + options.get(i));
        }
    }

    @Override
    public boolean evaluateAnswer(Object userAnswer) {
        if (userAnswer instanceof Integer) {
            return (Integer) userAnswer == correctAnswerIndex;
        }
        return false;
    }

    public List<String> getOptions() { return options; }
}

