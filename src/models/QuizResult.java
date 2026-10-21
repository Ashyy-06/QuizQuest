package models;

public class QuizResult
{
    private int resultId;
    private int userId;
    private String username;
    private String topic;
    private int totalQuestions;
    private int correctAnswers;
    private int score;

    public QuizResult(int resultId, int userId,String username, String topic,int totalQuestions, int correctAnswers)
    {
        this.resultId = resultId;
        this.userId = userId;
        this.username = username;
        this.topic = topic;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.score = correctAnswers;
    }

    public int getResultId()
    {
        return resultId;
    }

    public int getUserId()
    {
        return userId;
    }

    public String getUsername()
    {
        return username;
    }

    public String getTopic()
    {
        return topic;
    }

    public int getTotalQuestions()
    {
        return totalQuestions;
    }

    public int getCorrectAnswers()
    {
        return correctAnswers;
    }

    public int getScore()
    {
        return score;
    }

    public void displayResult()
    {
        System.out.println("\n========== QUIZ RESULT ==========");
        System.out.println("Student: " + username);
        System.out.println("Subject: " + topic);
        System.out.println("Total Questions: " + totalQuestions);
        System.out.println("Correct Answers: " + correctAnswers);
        System.out.println("Score: " + score + "/" + totalQuestions);
        System.out.println("=================================");
    }
}