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
 * Controller for the dashboard screen
 */
public class DashboardController implements Initializable {
    
    @FXML private Label navTitle;
    @FXML private Label welcomeLabel;
    @FXML private Button logoutButton;
    @FXML private Label welcomeTitle;
    @FXML private Label welcomeSubtitle;
    @FXML private Label registeredCoursesCount;
    @FXML private Label availableCoursesCount;
    
    private Student currentUser;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentUser = LoginController.getCurrentUser();
        
        if (currentUser == null) {
            // Redirect to login if no user is logged in
            Main.switchScene("/fxml/login.fxml", "Login - Student Registration System");
            return;
        }
        
        setupUI();
        updateStats();
    }
    
    private void setupUI() {
        welcomeLabel.setText("Welcome, " + currentUser.getFirstName());
        welcomeTitle.setText("Welcome, " + currentUser.getFullName() + "!");
        welcomeSubtitle.setText("Student ID: " + currentUser.getStudentId() + " | Select an option below to get started");
    }
    
    private void updateStats() {
        try {
            // Update registered courses count
            List<String> registeredCourseIds = FileHandler.getStudentRegistrations(currentUser.getStudentId());
            registeredCoursesCount.setText(String.valueOf(registeredCourseIds.size()));
            
            // Update available courses count
            List<Course> allCourses = FileHandler.loadCourses();
            long availableCourses = allCourses.stream().filter(Course::isAvailable).count();
            availableCoursesCount.setText(String.valueOf(availableCourses));
            
        } catch (Exception e) {
            System.err.println("Error updating dashboard stats: " + e.getMessage());
            registeredCoursesCount.setText("N/A");
            availableCoursesCount.setText("N/A");
        }
    }
    
    @FXML
    private void handleProfile() {
        Main.switchScene("/fxml/profile.fxml", "My Profile - Student Registration System");
    }
    
    @FXML
    private void handleCourseRegistration() {
        Main.switchScene("/fxml/course-registration.fxml", "Course Registration - Student Registration System");
    }
    
    @FXML
    private void handleViewCourses() {
        Main.switchScene("/fxml/view-courses.fxml", "My Courses - Student Registration System");
    }
    
    @FXML
    private void handleRefresh() {
        updateStats();
        showTemporaryMessage("Data refreshed successfully!", "#28a745");
    }
    
    @FXML
    private void handleLogout() {
        // Show confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout Confirmation");
        alert.setHeaderText("Logout");
        alert.setContentText("Are you sure you want to logout?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                LoginController.clearCurrentUser();
                Main.switchScene("/fxml/login.fxml", "Login - Student Registration System");
            }
        });
    }
    
    private void showTemporaryMessage(String message, String color) {
        Label tempLabel = new Label(message);
        tempLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
        
        // You could add this to the UI temporarily, but for simplicity we'll just update the subtitle
        String originalSubtitle = welcomeSubtitle.getText();
        welcomeSubtitle.setText(message);
        welcomeSubtitle.setStyle("-fx-text-fill: " + color + ";");
        
        // Reset after 3 seconds
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                javafx.application.Platform.runLater(() -> {
                    welcomeSubtitle.setText(originalSubtitle);
                    welcomeSubtitle.setStyle("-fx-text-fill: #6c757d;");
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}