package ui;

import javax.swing.*;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import database.UserDAO;
import models.User;

public class AuthView extends JFrame implements ActionListener{
        private JTextField usernameField;
        private JPasswordField passwordField;
        private JButton loginButton;
        private JButton registerButton;
        private JLabel messageLabel;
        private UserDAO userDAO;

        public AuthView() {
            userDAO = new UserDAO();
            setTitle("QuizQuest - Login");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);           
            JPanel panel = new JPanel(new GridLayout(5, 2,10,10));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            panel.add(new JLabel("QuizQuest Login"));
            panel.add(new JLabel(""));
            panel.add(new JLabel("Username:"));
            usernameField = new JTextField();
            panel.add(usernameField);
            panel.add(new JLabel("Password:"));
            passwordField = new JPasswordField();
            panel.add(passwordField);
            loginButton=new JButton("Login");
            registerButton=new JButton("Register");
            loginButton.addActionListener(this);
            registerButton.addActionListener(this);
            panel.add(loginButton);
            panel.add(registerButton);
            messageLabel=new JLabel("Enter your login details");
            panel.add(messageLabel);
            add(panel);
            setVisible(true);
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource() == loginButton) {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());
                if(username.isEmpty() || password.isEmpty()) {
                    messageLabel.setText("Please enter both username and password.");
                    return;
                }
                User user= userDAO.login(username, password);
                if(user == null) {
                    messageLabel.setText("Invalid username or password.");
                } else {
                    dispose();
                    if(user.getRole().equalsIgnoreCase("Admin")) {
                        new AdminDashboardView(user);
                    } else {
                        new UserDashboardView(user);
                    }
                }
            } else if(e.getSource() == registerButton) {
                showRegistrationDialog();
            }
        }

        private void showRegistrationDialog() {
            JTextField usernameInput = new JTextField();
            JPasswordField passwordInput = new JPasswordField();
            Object[] fields = {
                "Username:", usernameInput,
                "Password:", passwordInput
            };
            int choice = JOptionPane.showConfirmDialog(this, fields, "Register Student", JOptionPane.OK_CANCEL_OPTION);
            if (choice == JOptionPane.OK_OPTION) {
                String username = usernameInput.getText().trim();
                String password = new String(passwordInput.getPassword());
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Fields cannot be empty.");
                    return;
                }
                boolean registered = userDAO.register(username, password);
                if (registered) {
                    JOptionPane.showMessageDialog(this, "Registration successful. Please log in.");
                } else {
                    JOptionPane.showMessageDialog(this, "Registration failed. Username may already exist.");
                }
            }
        }       
}
