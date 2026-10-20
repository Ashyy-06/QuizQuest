package ui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.event.*;
import models.User;

public class UserDashboardView extends JFrame implements ActionListener {
    private final User user;
    private JComboBox<String> topicBox;
    private JButton startButton;    
    private JButton InstructionsButton;
    private JButton scorecardButton;
    private JButton logoutButton;

    public UserDashboardView(User user) {
        this.user = user;
        setTitle("QuizQuest - Student Dashboard");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel welcomeLabel = new JLabel("Welcome, " + this.user.getUsername() + "!", JLabel.CENTER);

        JLabel topicLabel = new JLabel("Select a Quiz Topic:");
        topicBox = new JComboBox<>(new String[]{"Science", "Social Science"});

        startButton = new JButton("Start Quiz");
        InstructionsButton = new JButton("Instructions");
        scorecardButton = new JButton("View Scorecard");
        logoutButton = new JButton("Logout");
        startButton.addActionListener(this);
        InstructionsButton.addActionListener(this);
        scorecardButton.addActionListener(this);
        logoutButton.addActionListener(this);

        panel.add(welcomeLabel);
        panel.add(topicLabel);
        panel.add(topicBox);
        panel.add(startButton);
        panel.add(InstructionsButton);
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
            String topic = (String) topicBox.getSelectedItem();
            if (topic == null) {
                JOptionPane.showMessageDialog(this, "Please select a quiz topic.","Warning", JOptionPane.WARNING_MESSAGE);
                return;
            } 
            JOptionPane.showMessageDialog(this, "Selected Topic: " + topic + "\nQuiz functionality will be connected next.");
        }
        else if (e.getSource() == InstructionsButton) {
            JOptionPane.showMessageDialog(this, "Instructions:\n1. Select a quiz topic.\n2. Answer each questions.\n3. Submit your answers.\n4. View your score.");
        } else if (e.getSource() == scorecardButton) {
            JOptionPane.showMessageDialog(this, "Your scorecard will be connected next.");
        } else if (e.getSource() == logoutButton) {
            dispose();
            new AuthView();
        }
    }
}
