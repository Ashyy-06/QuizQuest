package engine;

import java.util.List;

import database.QuestionDAO;
import database.QuizResultDAO;
import models.Question;
import models.StudentUser;

public class QuizManager {

    private QuestionDAO questionDAO;
    private QuizResultDAO resultDAO;
    private List<Question> questions;
    private Quiz quiz;
    private int currentQuestion;

    public QuizManager() {
        questionDAO = new QuestionDAO();
        resultDAO = new QuizResultDAO();
        currentQuestion = 0;
    }

    public boolean startQuiz(StudentUser student, String topic, int numberOfQuestions) {

        questions = questionDAO.getRandomQuestionsByTopic(topic, numberOfQuestions);

        if (questions == null || questions.isEmpty()) {
            quiz = null;
            return false;
        }

        quiz = new Quiz(student, topic, questions.size());
        currentQuestion = 0;

        return true;
    }

    public Question getCurrentQuestion() {

        if (questions == null || currentQuestion >= questions.size()) {
            return null;
        }

        return questions.get(currentQuestion);
    }

    public boolean submitAnswer(Object answer) {

        Question question = getCurrentQuestion();

        if (question == null) {
            return false;
        }

        boolean correct = question.evaluateAnswer(answer);

        if (correct) {
            quiz.addCorrectAnswer();
        }

        currentQuestion++;

        return correct;
    }

    public boolean isQuizFinished() {
        return questions == null || currentQuestion >= questions.size();
    }

    public int getCorrectAnswers() {
        return quiz == null ? 0 : quiz.getCorrectAnswers();
    }

    public int getTotalQuestions() {
        return quiz == null ? 0 : quiz.getTotalQuestions();
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public boolean saveResult() {

        if (quiz == null) {
            return false;
        }

        return resultDAO.saveResult(
            quiz.getStudent().getUserId(),
            quiz.getTopic(),
            quiz.getTotalQuestions(),
            quiz.getCorrectAnswers()
        );
    }
}

