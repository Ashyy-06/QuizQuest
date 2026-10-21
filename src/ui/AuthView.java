package ui;

import database.UserDAO;
import models.Admin;
import models.StudentUser;
import models.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class AuthView extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private UserDAO userDAO;

    public AuthView() {
        this.userDAO = new UserDAO();
        initUI();
        SwingUtilities.invokeLater(() ->this.getContentPane().requestFocusInWindow());
    }
    private void initUI() {
        setTitle("QuizQuest - Login & Registration");
        setSize(420, 340);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(245, 247, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel headerPanel = new JPanel(new GridLayout(2, 1,2, 2));
        headerPanel.setBackground(new Color(245, 247, 250));

        JLabel titleLabel = new JLabel("QuizQuest", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(41, 128, 185));

        JLabel welcomeLabel = new JLabel("Welcome! Please log in or register.", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        welcomeLabel.setForeground(new Color(100, 100, 100));

        headerPanel.add(titleLabel);
        headerPanel.add(welcomeLabel);

        gbc.gridx = 0;
        gbc.gridy = 0;  
        gbc.gridwidth = 2;
        mainPanel.add(headerPanel, gbc);

        JPanel formPanel = new JPanel(new GridLayout(2, 1, 10, 12));
        formPanel.setBackground(new Color(245, 247, 250));

        usernameField = new JTextField("Enter username");
        styleTextField(usernameField);
        usernameField.setText("Enter username");
        usernameField.setForeground(Color.GRAY);
        addPlaceholder(usernameField, "Enter username");
        formPanel.add(usernameField);

        passwordField = new JPasswordField("Enter password");
        stylePasswordField(passwordField);
        passwordField.setText("Enter password");
        passwordField.setForeground(Color.GRAY);
        addPlaceholderPassword(passwordField, "Enter password");
        formPanel.add(passwordField);

        gbc.gridy = 1;
        mainPanel.add(formPanel, gbc);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setBackground(new Color(245, 247, 250));

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register Student");

        styleButton(loginButton, new Color(41, 128, 185));
        styleButton(registerButton, new Color(39, 174, 96));

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        gbc.gridy = 2;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);
        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> handleRegister());
        this.getContentPane().requestFocusInWindow();
    }
    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setForeground(Color.GRAY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
    }
    private void stylePasswordField(JPasswordField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setForeground(Color.GRAY);
        field.setEchoChar((char) 0);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
    }
    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
    }
    private void addPlaceholder(JTextField textField, String placeholder) {
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(Color.GRAY);
                    textField.setText(placeholder);
                }
            }
        });
    }
    private void addPlaceholderPassword(JPasswordField passwordField, String placeholder) {
        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                String passText= new String(passwordField.getPassword());
                if (passText.equals(placeholder)) {
                    passwordField.setText("");
                    passwordField.setForeground(Color.BLACK);
                    passwordField.setEchoChar('•');
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                String passText = new String(passwordField.getPassword());
                if (passText.isEmpty()) {
                    passwordField.setForeground(Color.GRAY);
                    passwordField.setText(placeholder);
                    passwordField.setEchoChar((char) 0);
                }
            }
        });
    }
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty() || username.equals("Enter username") || password.equals("Enter password")) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = userDAO.login(username, password);
        if (user != null) {
            JOptionPane.showMessageDialog(this, "Login successful! Welcome, " + user.getUsername());
            this.dispose();
            if (user instanceof Admin) {
                new AdminDashboardView((Admin) user).setVisible(true);
            } else if (user instanceof StudentUser) {
                new UserDashboardView((StudentUser) user).setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty() || username.equals("Enter username") || password.equals("Enter password")) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = userDAO.createUser(username, password,"Student");
        if (success) {
            JOptionPane.showMessageDialog(this, "Registration successful! You can now log in.");
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed. Username may already exist.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}