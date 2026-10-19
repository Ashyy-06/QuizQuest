package models;

import java.util.Map;

public class MatchTheFollowingQuestion extends Question {
    private Map<String, String> correctPairs;

    public MatchTheFollowingQuestion(String id, String prompt, int points, Map<String, String> correctPairs) {
        super(id, prompt, points);
        this.correctPairs = correctPairs;
    }

    @Override
    public void displayQuestion() {
        System.out.println("[Match the Following] " + prompt + " (" + points + " pts)");
        int i = 1;
        for (Map.Entry<String, String> entry : correctPairs.entrySet()) {
            System.out.println(" " + i + ". " + entry.getKey() + " <---> [ ? ]");
            i++;
        }
    }

    @Override
    public boolean evaluateAnswer(Object userAnswer) {
        if (userAnswer instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, String> userPairs = (Map<String, String>) userAnswer;
            return userPairs.equals(correctPairs);
        }
        return false;
    }

    public Map<String, String> getCorrectPairs() { return correctPairs; }
}

