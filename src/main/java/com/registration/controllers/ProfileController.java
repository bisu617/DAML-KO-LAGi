package com.registration.controllers;

import com.registration.Main;
import com.registration.models.Course;
import com.registration.models.Student;
import com.registration.utils.FileHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the profile screen
 */
public class ProfileController implements Initializable {
    
    @FXML private Button backButton;
    @FXML private Label welcomeLabel;
    @FXML private Button logoutButton;
    @FXML private TextField studentIdField;
    @FXML private TextField usernameField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private PasswordField passwordField;
    @FXML private TextArea addressField;
    @FXML private Label messageLabel;
    @FXML private Button saveButton;
    @FXML private Button resetButton;
    @FXML private Button cancelButton;
    @FXML private Label registeredCoursesLabel;
    @FXML private Label totalCreditsLabel;
    
    private Student currentUser;
    private Student originalUser;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentUser = LoginController.getCurrentUser();
        
        if (currentUser == null) {
            Main.switchScene("/fxml/login.fxml", "Login - Student Registration System");
            return;
        }
        
        // Create a copy for reset functionality
        originalUser = copyStudent(currentUser);
        
        setupUI();
        loadUserData();
        updateStatistics();
    }
    
    private void setupUI() {
        welcomeLabel.setText("Welcome, " + currentUser.getFirstName());
        
        // Make certain fields non-editable
        studentIdField.setEditable(false);
        usernameField.setEditable(false);
        
        // Set up validation
        setupValidation();
    }
    
    private void setupValidation() {
        // Email validation
        emailField.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.isEmpty() && !isValidEmail(newText)) {
                emailField.setStyle("-fx-border-color: #dc3545;");
            } else {
                emailField.setStyle("");
            }
        });
        
        // Phone validation
        phoneField.textProperty().addListener((obs, oldText, newText) -> {
            // Remove non-digit characters except for common separators
            if (!newText.matches("[0-9\\-\\(\\)\\+\\s]*")) {
                phoneField.setText(oldText);
            }
        });
    }
    
    private void loadUserData() {
        studentIdField.setText(currentUser.getStudentId());
        usernameField.setText(currentUser.getUsername());
        firstNameField.setText(currentUser.getFirstName());
        lastNameField.setText(currentUser.getLastName());
        emailField.setText(currentUser.getEmail());
        phoneField.setText(currentUser.getPhoneNumber() != null ? currentUser.getPhoneNumber() : "");
        passwordField.setText(currentUser.getPassword());
        addressField.setText(currentUser.getAddress() != null ? currentUser.getAddress() : "");
    }
    
    private void updateStatistics() {
        try {
            List<String> registeredCourseIds = FileHandler.getStudentRegistrations(currentUser.getStudentId());
            registeredCoursesLabel.setText(String.valueOf(registeredCourseIds.size()));
            
            // Calculate total credits
            List<Course> allCourses = FileHandler.loadCourses();
            int totalCredits = 0;
            for (String courseId : registeredCourseIds) {
                for (Course course : allCourses) {
                    if (course.getCourseId().equals(courseId)) {
                        totalCredits += course.getCredits();
                        break;
                    }
                }
            }
            totalCreditsLabel.setText(String.valueOf(totalCredits));
            
        } catch (Exception e) {
            System.err.println("Error updating profile statistics: " + e.getMessage());
            registeredCoursesLabel.setText("N/A");
            totalCreditsLabel.setText("N/A");
        }
    }
    
    @FXML
    private void handleSave() {
        if (!validateFields()) {
            return;
        }
        
        try {
            // Update current user with form data
            currentUser.setFirstName(firstNameField.getText().trim());
            currentUser.setLastName(lastNameField.getText().trim());
            currentUser.setEmail(emailField.getText().trim());
            currentUser.setPhoneNumber(phoneField.getText().trim());
            currentUser.setPassword(passwordField.getText());
            currentUser.setAddress(addressField.getText().trim());
            
            // Save to file
            List<Student> students = FileHandler.loadStudents();
            for (int i = 0; i < students.size(); i++) {
                if (students.get(i).getStudentId().equals(currentUser.getStudentId())) {
                    students.set(i, currentUser);
                    break;
                }
            }
            FileHandler.saveStudents(students);
            
            // Update original user for reset functionality
            originalUser = copyStudent(currentUser);
            
            showMessage("Profile updated successfully!", "#28a745");
            
        } catch (Exception e) {
            showMessage("Error saving profile: " + e.getMessage(), "#dc3545");
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleReset() {
        loadUserData();
        showMessage("Profile reset to saved values.", "#17a2b8");
    }
    
    @FXML
    private void handleCancel() {
        handleBack();
    }
    
    @FXML
    private void handleBack() {
        Main.switchScene("/fxml/dashboard.fxml", "Dashboard - Student Registration System");
    }
    
    @FXML
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout Confirmation");
        alert.setHeaderText("Logout");
        alert.setContentText("Are you sure you want to logout? Any unsaved changes will be lost.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                LoginController.clearCurrentUser();
                Main.switchScene("/fxml/login.fxml", "Login - Student Registration System");
            }
        });
    }
    
    private boolean validateFields() {
        String errors = "";
        
        if (firstNameField.getText().trim().isEmpty()) {
            errors += "First name is required.\n";
        }
        
        if (lastNameField.getText().trim().isEmpty()) {
            errors += "Last name is required.\n";
        }
        
        if (emailField.getText().trim().isEmpty()) {
            errors += "Email is required.\n";
        } else if (!isValidEmail(emailField.getText().trim())) {
            errors += "Please enter a valid email address.\n";
        }
        
        if (passwordField.getText().isEmpty()) {
            errors += "Password is required.\n";
        } else if (passwordField.getText().length() < 6) {
            errors += "Password must be at least 6 characters long.\n";
        }
        
        if (!errors.isEmpty()) {
            showMessage("Please fix the following errors:\n" + errors, "#dc3545");
            return false;
        }
        
        return true;
    }
    
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    
    private void showMessage(String message, String color) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: " + color + ";");
        
        // Clear message after 5 seconds
        new Thread(() -> {
            try {
                Thread.sleep(5000);
                javafx.application.Platform.runLater(() -> messageLabel.setText(""));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
    
    private Student copyStudent(Student original) {
        Student copy = new Student();
        copy.setStudentId(original.getStudentId());
        copy.setFirstName(original.getFirstName());
        copy.setLastName(original.getLastName());
        copy.setEmail(original.getEmail());
        copy.setUsername(original.getUsername());
        copy.setPassword(original.getPassword());
        copy.setPhoneNumber(original.getPhoneNumber());
        copy.setAddress(original.getAddress());
        copy.setRegisteredCourses(original.getRegisteredCourses());
        return copy;
    }
}