package com.registration.controllers;

import com.registration.Main;
import com.registration.models.Course;
import com.registration.models.Student;
import com.registration.utils.FileHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the course registration screen
 */
public class CourseRegistrationController implements Initializable {
    
    @FXML private Button backButton;
    @FXML private Label welcomeLabel;
    @FXML private Button logoutButton;
    @FXML private Button refreshButton;
    @FXML private Label messageLabel;
    @FXML private VBox coursesContainer;
    @FXML private Label selectedCoursesLabel;
    @FXML private Label totalCreditsLabel;
    @FXML private Label alreadyRegisteredLabel;
    
    private Student currentUser;
    private List<Course> availableCourses;
    private List<String> userRegistrations;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentUser = LoginController.getCurrentUser();
        
        if (currentUser == null) {
            Main.switchScene("/fxml/login.fxml", "Login - Student Registration System");
            return;
        }
        
        setupUI();
        loadCourses();
    }
    
    private void setupUI() {
        welcomeLabel.setText("Welcome, " + currentUser.getFirstName());
    }
    
    private void loadCourses() {
        try {
            availableCourses = FileHandler.loadCourses();
            userRegistrations = FileHandler.getStudentRegistrations(currentUser.getStudentId());
            
            displayCourses();
            updateSummary();
            
        } catch (Exception e) {
            showMessage("Error loading courses: " + e.getMessage(), "#dc3545");
            e.printStackTrace();
        }
    }
    
    private void displayCourses() {
        coursesContainer.getChildren().clear();
        
        if (availableCourses.isEmpty()) {
            Label noCoursesLabel = new Label("No courses available at this time.");
            noCoursesLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #6c757d; -fx-padding: 20px;");
            coursesContainer.getChildren().add(noCoursesLabel);
            return;
        }
        
        for (Course course : availableCourses) {
            VBox courseCard = createCourseCard(course);
            coursesContainer.getChildren().add(courseCard);
        }
    }
    
    private VBox createCourseCard(Course course) {
        VBox card = new VBox();
        card.getStyleClass().add("course-card");
        card.setSpacing(10);
        card.setPadding(new Insets(15));
        
        // Course title
        Label titleLabel = new Label(course.getCourseId() + " - " + course.getCourseName());
        titleLabel.getStyleClass().add("course-title");
        
        // Course details
        Label instructorLabel = new Label("Instructor: " + course.getInstructor());
        instructorLabel.getStyleClass().add("course-instructor");
        
        Label scheduleLabel = new Label("Schedule: " + course.getSchedule());
        scheduleLabel.getStyleClass().add("course-details");
        
        Label creditsLabel = new Label("Credits: " + course.getCredits());
        creditsLabel.getStyleClass().add("course-details");
        
        Label capacityLabel = new Label("Enrollment: " + course.getCurrentEnrollment() + "/" + course.getMaxCapacity() + 
                                      " (" + course.getAvailableSpots() + " spots available)");
        capacityLabel.getStyleClass().add("course-details");
        
        // Description
        if (course.getDescription() != null && !course.getDescription().isEmpty()) {
            Label descriptionLabel = new Label("Description: " + course.getDescription());
            descriptionLabel.getStyleClass().add("course-details");
            descriptionLabel.setWrapText(true);
            card.getChildren().add(descriptionLabel);
        }
        
        // Prerequisites
        if (course.getPrerequisites() != null && !course.getPrerequisites().isEmpty()) {
            Label prereqLabel = new Label("Prerequisites: " + course.getPrerequisites());
            prereqLabel.getStyleClass().add("course-details");
            prereqLabel.setWrapText(true);
            card.getChildren().add(prereqLabel);
        }
        
        // Action buttons
        HBox buttonBox = new HBox();
        buttonBox.setSpacing(10);
        
        boolean isRegistered = userRegistrations.contains(course.getCourseId());
        boolean isAvailable = course.isAvailable();
        
        if (isRegistered) {
            Label registeredLabel = new Label("✓ Already Registered");
            registeredLabel.setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;");
            buttonBox.getChildren().add(registeredLabel);
            
            Button dropButton = new Button("Drop Course");
            dropButton.getStyleClass().add("danger-button");
            dropButton.setOnAction(e -> handleDropCourse(course));
            buttonBox.getChildren().add(dropButton);
            
        } else if (isAvailable) {
            Button registerButton = new Button("Register");
            registerButton.getStyleClass().add("success-button");
            registerButton.setOnAction(e -> handleRegisterCourse(course));
            buttonBox.getChildren().add(registerButton);
            
        } else {
            Label fullLabel = new Label("Course Full");
            fullLabel.setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");
            buttonBox.getChildren().add(fullLabel);
        }
        
        card.getChildren().addAll(titleLabel, instructorLabel, scheduleLabel, creditsLabel, capacityLabel, buttonBox);
        
        return card;
    }
    
    private void handleRegisterCourse(Course course) {
        try {
            // Show confirmation dialog
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Course Registration");
            alert.setHeaderText("Register for " + course.getCourseName());
            alert.setContentText("Are you sure you want to register for this course?\n\n" +
                                "Course: " + course.getCourseId() + " - " + course.getCourseName() + "\n" +
                                "Credits: " + course.getCredits() + "\n" +
                                "Schedule: " + course.getSchedule());
            
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // Save registration
                    FileHandler.saveRegistration(currentUser.getStudentId(), course.getCourseId());
                    
                    // Update course enrollment
                    course.incrementEnrollment();
                    List<Course> courses = FileHandler.loadCourses();
                    for (int i = 0; i < courses.size(); i++) {
                        if (courses.get(i).getCourseId().equals(course.getCourseId())) {
                            courses.set(i, course);
                            break;
                        }
                    }
                    FileHandler.saveCourses(courses);
                    
                    showMessage("Successfully registered for " + course.getCourseName() + "!", "#28a745");
                    
                    // Refresh the display
                    loadCourses();
                }
            });
            
        } catch (Exception e) {
            showMessage("Error registering for course: " + e.getMessage(), "#dc3545");
            e.printStackTrace();
        }
    }
    
    private void handleDropCourse(Course course) {
        try {
            // Show confirmation dialog
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Drop Course");
            alert.setHeaderText("Drop " + course.getCourseName());
            alert.setContentText("Are you sure you want to drop this course?\n\n" +
                                "Course: " + course.getCourseId() + " - " + course.getCourseName() + "\n" +
                                "This action cannot be undone.");
            
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // Remove registration
                    FileHandler.removeRegistration(currentUser.getStudentId(), course.getCourseId());
                    
                    // Update course enrollment
                    course.decrementEnrollment();
                    List<Course> courses = FileHandler.loadCourses();
                    for (int i = 0; i < courses.size(); i++) {
                        if (courses.get(i).getCourseId().equals(course.getCourseId())) {
                            courses.set(i, course);
                            break;
                        }
                    }
                    FileHandler.saveCourses(courses);
                    
                    showMessage("Successfully dropped " + course.getCourseName() + ".", "#ffc107");
                    
                    // Refresh the display
                    loadCourses();
                }
            });
            
        } catch (Exception e) {
            showMessage("Error dropping course: " + e.getMessage(), "#dc3545");
            e.printStackTrace();
        }
    }
    
    private void updateSummary() {
        try {
            int selectedCourses = 0;
            int totalCredits = 0;
            int alreadyRegistered = userRegistrations.size();
            
            for (String courseId : userRegistrations) {
                for (Course course : availableCourses) {
                    if (course.getCourseId().equals(courseId)) {
                        selectedCourses++;
                        totalCredits += course.getCredits();
                        break;
                    }
                }
            }
            
            selectedCoursesLabel.setText(String.valueOf(selectedCourses));
            totalCreditsLabel.setText(String.valueOf(totalCredits));
            alreadyRegisteredLabel.setText(String.valueOf(alreadyRegistered));
            
        } catch (Exception e) {
            System.err.println("Error updating summary: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleRefresh() {
        loadCourses();
        showMessage("Course data refreshed successfully!", "#28a745");
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
        alert.setContentText("Are you sure you want to logout?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                LoginController.clearCurrentUser();
                Main.switchScene("/fxml/login.fxml", "Login - Student Registration System");
            }
        });
    }
    
    private void showMessage(String message, String color) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
        
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
}