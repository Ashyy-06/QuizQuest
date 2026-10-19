package models;

public abstract class Question {
    protected String id;
    protected String prompt;
    protected int points;

    public Question(String id, String prompt, int points) {
        this.id = id;
        this.prompt = prompt;
        this.points = points;
    }

    public abstract void displayQuestion();
    public abstract boolean evaluateAnswer(Object userAnswer);

    public String getId() { return id; }
    public String getPrompt() { return prompt; }
    public int getPoints() { return points; }
}
