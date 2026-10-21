package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import models.User;
import models.Question;
import database.QuestionDAO;

public class AdminDashboardView extends JFrame implements ActionListener {
    private final User user;
    private QuestionDAO questionDAO;
    private JButton addButton;
    private JButton viewButton;
    private JButton deleteButton;
    private JButton logoutButton;

    public AdminDashboardView(User user) {
        this.user = user;
        questionDAO = new QuestionDAO();
        setTitle("QuizQuest - Admin Dashboard");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));

        JLabel welcomeLabel = new JLabel("Welcome, " + this.user.getUsername() + "!", JLabel.CENTER);
        addButton = new JButton("Add Question");
        viewButton = new JButton("View Questions");
        deleteButton = new JButton("Delete Question");
        logoutButton = new JButton("Logout");

        addButton.addActionListener(this);
        viewButton.addActionListener(this);
        deleteButton.addActionListener(this);
        logoutButton.addActionListener(this);
        panel.add(welcomeLabel);
        panel.add(addButton);
        panel.add(viewButton);
        panel.add(deleteButton);
        panel.add(logoutButton);
        add(panel);
        setVisible(true);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            addQuestion();
        } else if (e.getSource() == viewButton) {
            viewQuestions();
        } else if (e.getSource() == deleteButton) {
            deleteQuestion();
        } else if (e.getSource() == logoutButton) {
            dispose();
            new AuthView();
        }
    }
    private void addQuestion() {
        String topic = JOptionPane.showInputDialog(this, "Enter the topic:");
        if (topic == null || topic.trim().isEmpty()) {
            return;
        }
        String type = (String) JOptionPane.showInputDialog(this, "Select the question type:", "Question Type", JOptionPane.QUESTION_MESSAGE, null, new String[]{"MCQ", "TrueFalse"}, "MCQ");
        if (type == null) {
            return;
        }
        String prompt = JOptionPane.showInputDialog(this, "Enter the question:");
        if (prompt == null || prompt.trim().isEmpty()) {
            return;
        }
        String options = "";
        String answer;
        if (type.equals("MCQ")) {
            String option1 = JOptionPane.showInputDialog(this, "Enter option 1:");
            if (option1 == null || option1.trim().isEmpty()) {
                return;
            }
            String option2 = JOptionPane.showInputDialog(this, "Enter option 2:");
            if (option2 == null || option2.trim().isEmpty()) {
                return;
            }
            String option3 = JOptionPane.showInputDialog(this, "Enter option 3:");
            if (option3 == null || option3.trim().isEmpty()) {
                return;
            }
            String option4 = JOptionPane.showInputDialog(this, "Enter option 4:");
            if (option4 == null || option4.trim().isEmpty()) {
                return;
            }
            options = option1 + "|" + option2 + "|" + option3 + "|" + option4;
             answer = JOptionPane.showInputDialog(this, "Enter the correct option number (1-4):");
            if (answer == null || answer.trim().isEmpty() || !answer.matches("[1-4]")) {
                JOptionPane.showMessageDialog(this, "Invalid answer. Please enter a number between 1 and 4.");
                return;
            }
        } else {
             answer = (String) JOptionPane.showInputDialog(this, "Select the correct answer:", "Correct Answer", JOptionPane.QUESTION_MESSAGE, null, new String[]{"True", "False"}, "True");
            if (answer == null) {
                return;
            }
        }
        boolean success = questionDAO.addQuestion(topic.trim(), type, prompt.trim(), options, answer);
        if (success) {
            JOptionPane.showMessageDialog(this, "Question added successfully.");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add question. Please try again.");
        }
    }
    
    private void viewQuestions() {
    List<Question> questions = questionDAO.getAllQuestions();

    if (questions == null || questions.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No questions available.", "All Questions", JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    StringBuilder details = new StringBuilder();

    for (Question question : questions) {
        details.append("[ID: ").append(question.getId()).append("] ")
               .append(question.getPrompt())
               .append(" (").append(question.getPoints()).append(" pts)")
               .append("\n\n");
    }

    JTextArea textArea = new JTextArea(details.toString());
    textArea.setEditable(false);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true); // Wraps long question text cleanly onto new lines

    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setPreferredSize(new Dimension(450, 300));

    JOptionPane.showMessageDialog(this, scrollPane, "All Questions", JOptionPane.INFORMATION_MESSAGE);
}


    private void deleteQuestion() {

        String input = JOptionPane.showInputDialog(this, "Enter the Question ID to delete:");

        if (input == null || input.trim().isEmpty()) {
            return;
        }

        int id;

        try {
            id = Integer.parseInt(input.trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric Question ID.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,"Are you sure you want to delete Question ID " + id + "?","Confirm Deletion",JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        boolean success = questionDAO.deleteQuestion(id);

        if (success) {
            JOptionPane.showMessageDialog(this, "Question deleted successfully.");
        } else {
            JOptionPane.showMessageDialog(this, "Question not found or deletion failed.");
        }
    }
}
            

