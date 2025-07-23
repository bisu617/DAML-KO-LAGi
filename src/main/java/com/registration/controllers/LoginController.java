package com.registration.controllers;

import com.registration.Main;
import com.registration.models.Student;
import com.registration.utils.FileHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the login screen
 */
public class LoginController implements Initializable {
    
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button clearButton;
    @FXML private Label errorLabel;
    
    private static Student currentUser;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up event handlers
        setupEventHandlers();
        
        // Clear any previous error messages
        errorLabel.setText("");
    }
    
    private void setupEventHandlers() {
        // Enter key should trigger login
        usernameField.setOnAction(e -> handleLogin());
        passwordField.setOnAction(e -> handleLogin());
        
        // Clear error message when user starts typing
        usernameField.textProperty().addListener((obs, oldText, newText) -> errorLabel.setText(""));
        passwordField.textProperty().addListener((obs, oldText, newText) -> errorLabel.setText(""));
    }
    
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        
        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }
        
        try {
            // Authenticate user
            Student user = FileHandler.authenticateUser(username, password);
            
            if (user != null) {
                currentUser = user;
                showSuccess("Login successful! Welcome " + user.getFirstName() + "!");
                
                // Navigate to dashboard
                Main.switchScene("/fxml/dashboard.fxml", "Dashboard - Student Registration System");
            } else {
                showError("Invalid username or password. Please try again.");
                passwordField.clear();
            }
        } catch (Exception e) {
            showError("An error occurred during login. Please try again.");
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleClear() {
        usernameField.clear();
        passwordField.clear();
        errorLabel.setText("");
        usernameField.requestFocus();
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: #dc3545;");
    }
    
    private void showSuccess(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: #28a745;");
    }
    
    // Static method to get current logged-in user
    public static Student getCurrentUser() {
        return currentUser;
    }
    
    // Static method to clear current user (for logout)
    public static void clearCurrentUser() {
        currentUser = null;
    }
}