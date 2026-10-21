package ui;

import database.QuizResultDAO;
import engine.QuizManager;
import engine.QuoteService;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import models.Question;
import models.QuizResult;
import models.SingleChoiceMCQ;
import models.StudentUser;
import models.TrueFalseQuestion;
import models.User;

public class UserDashboardView extends JFrame implements ActionListener {
    private final User user;
    private JComboBox<String> topicBox;
    private JButton startButton;    
    private JButton instructionsButton;
    private JButton scorecardButton;
    private JButton logoutButton;
    private QuizResultDAO resultDAO;
    private QuoteService quoteService;

    public UserDashboardView(User user) {
        this.user = user;
        this.resultDAO = new QuizResultDAO();
        this.quoteService = new QuoteService();

        setTitle("QuizQuest - Student Dashboard");
        setSize(450, 360);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel welcomeLabel = new JLabel("Welcome, " + this.user.getUsername() + "!", JLabel.CENTER);
        JLabel topicLabel = new JLabel("Select a Quiz Topic:");
        topicBox = new JComboBox<>(new String[]{"Science", "Social Science"});

        startButton = new JButton("Start Quiz");
        instructionsButton = new JButton("Instructions");
        scorecardButton = new JButton("View Scorecard");
        logoutButton = new JButton("Logout");

        startButton.addActionListener(this);
        instructionsButton.addActionListener(this);
        scorecardButton.addActionListener(this);
        logoutButton.addActionListener(this);

        panel.add(welcomeLabel);
        panel.add(topicLabel);
        panel.add(topicBox);
        panel.add(startButton);
        panel.add(instructionsButton);
        panel.add(scorecardButton);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(logoutButton);

        add(panel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            startQuizFlow();
        } else if (e.getSource() == instructionsButton) {
            JOptionPane.showMessageDialog(
                this,
                "Instructions:\n"
                + "1. Select your preferred topic from the dropdown.\n"
                + "2. Answer each question carefully.\n"
                + "3. For MCQ: select an option (1-4).\n"
                + "4. For True/False: select True or False.\n"
                + "5. Your results will automatically be saved to your history.",
                "Quiz Instructions",
                JOptionPane.INFORMATION_MESSAGE
            );
        } else if (e.getSource() == scorecardButton) {
            viewScorecard();
        } else if (e.getSource() == logoutButton) {
            dispose();
            new AuthView();
        }
    }

    /**
     * Connects QuizManager to the GUI flow
     */
    private void startQuizFlow() {
        String topic = (String) topicBox.getSelectedItem();
        if (topic == null) {
            JOptionPane.showMessageDialog(this, "Please select a quiz topic.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        StudentUser student;
        if (user instanceof StudentUser) {
            student = (StudentUser) user;
        } else {
            student = new StudentUser(user.getUserId(), user.getUsername(), user.getPassword());
        }

        QuizManager manager = new QuizManager();
        boolean started = manager.startQuiz(student, topic, 5); // Load 5 random questions

        if (!started) {
            JOptionPane.showMessageDialog(this, "No questions found for " + topic + "!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int questionNumber = 1;
        while (!manager.isQuizFinished()) {
            Question q = manager.getCurrentQuestion();
            if (q == null) break;

            if (q instanceof SingleChoiceMCQ) {
                SingleChoiceMCQ mcq = (SingleChoiceMCQ) q;
                List<String> options = mcq.getOptions();
                String[] optionChoices = new String[options.size()];
                for (int i = 0; i < options.size(); i++) {
                    optionChoices[i] = (i + 1) + ". " + options.get(i);
                }

                String selection = (String) JOptionPane.showInputDialog(
                    this,
                    "Question " + questionNumber + ":\n" + mcq.getPrompt(),
                    "Quiz: " + topic,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    optionChoices,
                    optionChoices[0]
                );

                if (selection == null) {
                    JOptionPane.showMessageDialog(this, "Quiz cancelled.");
                    return;
                }

                // Determine chosen index (1-based)
                int chosenIndex = 1;
                for (int i = 0; i < optionChoices.length; i++) {
                    if (optionChoices[i].equals(selection)) {
                        chosenIndex = i + 1;
                        break;
                    }
                }
                manager.submitAnswer(chosenIndex);

            } else if (q instanceof TrueFalseQuestion) {
                TrueFalseQuestion tf = (TrueFalseQuestion) q;
                String[] tfChoices = new String[]{"True", "False"};

                String selection = (String) JOptionPane.showInputDialog(
                    this,
                    "Question " + questionNumber + ":\n" + tf.getPrompt(),
                    "Quiz: " + topic,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    tfChoices,
                    "True"
                );

                if (selection == null) {
                    JOptionPane.showMessageDialog(this, "Quiz cancelled.");
                    return;
                }

                boolean chosenBool = "True".equalsIgnoreCase(selection);
                manager.submitAnswer(chosenBool);
            }

            questionNumber++;
        }

        // Save result to DB
        manager.saveResult();

        // Show Score Summary & Motivational Quote
        String quote = quoteService.getRandomQuote();
        String summary = "Quiz Completed!\n\n"
                       + "Topic: " + topic + "\n"
                       + "Score: " + manager.getCorrectAnswers() + " / " + manager.getTotalQuestions() + "\n\n"
                       + "\"" + quote + "\"";

        JOptionPane.showMessageDialog(this, summary, "Results Saved", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Connects QuizResultDAO to view past attempts
     */
    private void viewScorecard() {
        List<QuizResult> history = resultDAO.getResultsByUserId(user.getUserId());

        if (history == null || history.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No quiz attempts recorded yet.", "Scorecard", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-6s | %-15s | %-8s | %-10s\n", "ID", "Topic", "Score", "Total"));
        sb.append("--------------------------------------------------\n");

        for (QuizResult r : history) {
            sb.append(String.format("%-6d | %-15s | %-8d | %-10d\n", 
                r.getResultId(), r.getTopic(), r.getScore(), r.getTotalQuestions()));
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(380, 220));

        JOptionPane.showMessageDialog(this, scrollPane, "Quiz History for " + user.getUsername(), JOptionPane.INFORMATION_MESSAGE);
    }
}