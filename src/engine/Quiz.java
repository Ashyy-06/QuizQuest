package engine;

import models.StudentUser;

public class Quiz
{
    private StudentUser student;
    private String topic;
    private int totalQuestions;
    private int correctAnswers;

    public Quiz(StudentUser student, String topic, int totalQuestions)
    {
        this.student = student;
        this.topic = topic;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = 0;
    }

    public StudentUser getStudent()
    {
        return student;
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

    public void addCorrectAnswer()
    {
        if (correctAnswers < totalQuestions)
        {
            correctAnswers++;
        }
    }

    public int getScore()
    {
        return correctAnswers;
    }

    public void displayQuizStatus()
    {
        System.out.println("\n========== QUIZ STATUS ==========");
        System.out.println("Student: " + student.getUsername());
        System.out.println("Selected Subject: " + topic);
        System.out.println("Total Questions: " + totalQuestions);
        System.out.println("Correct Answers: " + correctAnswers);
        System.out.println("Score: " + getScore());
        System.out.println("=================================");
    }
}