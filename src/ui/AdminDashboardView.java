package ui;

import database.QuestionDAO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;
import models.Question;
import models.User;

public class AdminDashboardView extends JFrame {

    private final User adminUser;
    private final QuestionDAO questionDAO;

    public AdminDashboardView(User adminUser) {
        this.adminUser = adminUser;
        this.questionDAO = new QuestionDAO();

        setTitle("QuizQuest - Admin Dashboard");
        setSize(420, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Top Header
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JLabel titleLabel = new JLabel("QuizQuest Admin Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        String username = (this.adminUser != null) ? this.adminUser.getUsername() : "Admin";
        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        headerPanel.add(titleLabel);
        headerPanel.add(welcomeLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Center Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 12, 12));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 20, 40));

        JButton btnAddQuestion = new JButton("Add Question");
        JButton btnViewQuestions = new JButton("View Questions");
        JButton btnDeleteQuestion = new JButton("Delete Question");
        JButton btnLogout = new JButton("Logout");

        buttonPanel.add(btnAddQuestion);
        buttonPanel.add(btnViewQuestions);
        buttonPanel.add(btnDeleteQuestion);
        buttonPanel.add(btnLogout);
        add(buttonPanel, BorderLayout.CENTER);

        // Action Listeners
        btnAddQuestion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addQuestion();
            }
        });

        btnViewQuestions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewQuestions();
            }
        });

        btnDeleteQuestion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteQuestion();
            }
        });

        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new AuthView().setVisible(true);
            }
        });

        setVisible(true);
    }

    /**
     * Collects question details in the exact format QuestionDAO expects:
     * - Topic string
     * - Type: "MCQ" or "TRUE_FALSE"
     * - Options delimited with '|'
     * - Answer: "1"-"4" for MCQ (parsed as int by DAO), or "true"/"false" for TRUE_FALSE
     */
    private void addQuestion() {
        String topic = JOptionPane.showInputDialog(this, "Enter the topic (e.g., Science, Social Science):");
        if (topic == null || topic.trim().isEmpty()) return;

        String prompt = JOptionPane.showInputDialog(this, "Enter the question prompt:");
        if (prompt == null || prompt.trim().isEmpty()) return;

        String[] types = {"Single Choice MCQ", "True / False"};
        int selectedType = JOptionPane.showOptionDialog(
                this,
                "Select question type:",
                "Question Type",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                types,
                types[0]
        );

        if (selectedType == JOptionPane.CLOSED_OPTION) return;

        boolean success = false;

        if (selectedType == 0) { // MCQ
            String opt1 = JOptionPane.showInputDialog(this, "Option 1:");
            if (opt1 == null || opt1.trim().isEmpty()) return;

            String opt2 = JOptionPane.showInputDialog(this, "Option 2:");
            if (opt2 == null || opt2.trim().isEmpty()) return;

            String opt3 = JOptionPane.showInputDialog(this, "Option 3:");
            if (opt3 == null || opt3.trim().isEmpty()) return;

            String opt4 = JOptionPane.showInputDialog(this, "Option 4:");
            if (opt4 == null || opt4.trim().isEmpty()) return;

            String[] choices = {
                "1 (" + opt1.trim() + ")",
                "2 (" + opt2.trim() + ")",
                "3 (" + opt3.trim() + ")",
                "4 (" + opt4.trim() + ")"
            };

            int answerChoice = JOptionPane.showOptionDialog(
                    this,
                    "Select which option is the correct answer:",
                    "Correct Answer",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    choices,
                    choices[0]
            );

            if (answerChoice == JOptionPane.CLOSED_OPTION) return;

            // Formats with pipe '|' to match QuestionDAO's split("\\|")
            String optionsCombined = opt1.trim() + "|" + opt2.trim() + "|" + opt3.trim() + "|" + opt4.trim();
            // Answers as "1", "2", "3", or "4" to match QuestionDAO's Integer.parseInt(answer.trim())
            String answerIndex = String.valueOf(answerChoice + 1);

            success = questionDAO.addQuestion(topic.trim(), "MCQ", prompt.trim(), optionsCombined, answerIndex);

        } else if (selectedType == 1) { // True / False
            String[] tfOptions = {"True", "False"};
            int tfChoice = JOptionPane.showOptionDialog(
                    this,
                    "Select the correct answer:",
                    "Correct Answer",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    tfOptions,
                    tfOptions[0]
            );

            if (tfChoice == JOptionPane.CLOSED_OPTION) return;

            // Type must be "TRUE_FALSE", options empty, and answer boolean string for Boolean.parseBoolean(...)
            String answer = (tfChoice == 0) ? "true" : "false";

            success = questionDAO.addQuestion(topic.trim(), "TRUE_FALSE", prompt.trim(), "", answer);
        }

        if (success) {
            JOptionPane.showMessageDialog(this, "Question added and saved to database successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save question to database.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Reads all questions using QuestionDAO.getAllQuestions() and displays ID, points, and prompt.
     */
    private void viewQuestions() {
        List<Question> questions = questionDAO.getAllQuestions();

        if (questions == null || questions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No questions found in the database.", "All Questions", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder details = new StringBuilder();

        for (Question q : questions) {
            details.append("[ID: ").append(q.getId()).append("] (")
                   .append(q.getPoints()).append(" pts)\n")
                   .append(q.getPrompt()).append("\n\n");
        }

        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 350));

        JOptionPane.showMessageDialog(this, scrollPane, "All Questions (Total: " + questions.size() + ")", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Prompts for Question ID and calls questionDAO.deleteQuestion(id).
     */
    private void deleteQuestion() {
        String inputId = JOptionPane.showInputDialog(this, "Enter Question ID to delete:");
        if (inputId == null || inputId.trim().isEmpty()) return;

        try {
            int questionId = Integer.parseInt(inputId.trim());
            boolean success = questionDAO.deleteQuestion(questionId);

            if (success) {
                JOptionPane.showMessageDialog(this, "Question [ID: " + questionId + "] deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Question ID " + questionId + " could not be found.", "Not Found", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric ID.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }
}