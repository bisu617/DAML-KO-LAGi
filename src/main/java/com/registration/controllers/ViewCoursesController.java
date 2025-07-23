package com.registration.controllers;

import com.registration.Main;
import com.registration.models.Course;
import com.registration.models.Student;
import com.registration.utils.FileHandler;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for viewing registered courses
 */
public class ViewCoursesController implements Initializable {
    
    @FXML private Button backButton;
    @FXML private Label welcomeLabel;
    @FXML private Button logoutButton;
    @FXML private Button refreshButton;
    @FXML private Button dropSelectedButton;
    @FXML private Label messageLabel;
    @FXML private TableView<Course> coursesTable;
    @FXML private TableColumn<Course, String> courseIdColumn;
    @FXML private TableColumn<Course, String> courseNameColumn;
    @FXML private TableColumn<Course, String> instructorColumn;
    @FXML private TableColumn<Course, String> scheduleColumn;
    @FXML private TableColumn<Course, Integer> creditsColumn;
    @FXML private TableColumn<Course, String> enrollmentColumn;
    @FXML private TableColumn<Course, Void> actionsColumn;
    @FXML private Label totalCoursesLabel;
    @FXML private Label totalCreditsLabel;
    @FXML private Label averageCreditsLabel;
    @FXML private Button registerMoreButton;
    @FXML private Button printScheduleButton;
    
    private Student currentUser;
    private ObservableList<Course> registeredCourses;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentUser = LoginController.getCurrentUser();
        
        if (currentUser == null) {
            Main.switchScene("/fxml/login.fxml", "Login - Student Registration System");
            return;
        }
        
        setupUI();
        setupTable();
        loadRegisteredCourses();
    }
    
    private void setupUI() {
        welcomeLabel.setText("Welcome, " + currentUser.getFirstName());
        registeredCourses = FXCollections.observableArrayList();
        coursesTable.setItems(registeredCourses);
        
        // Enable multiple selection
        coursesTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        // Update drop button state when selection changes
        coursesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            dropSelectedButton.setDisable(newSelection == null);
        });
        
        dropSelectedButton.setDisable(true);
    }
    
    private void setupTable() {
        // Set up table columns
        courseIdColumn.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        instructorColumn.setCellValueFactory(new PropertyValueFactory<>("instructor"));
        scheduleColumn.setCellValueFactory(new PropertyValueFactory<>("schedule"));
        creditsColumn.setCellValueFactory(new PropertyValueFactory<>("credits"));
        
        // Custom enrollment column to show current/max
        enrollmentColumn.setCellValueFactory(cellData -> {
            Course course = cellData.getValue();
            return new SimpleStringProperty(course.getCurrentEnrollment() + "/" + course.getMaxCapacity());
        });
        
        // Set up actions column with drop button
        setupActionsColumn();
        
        // Style the table
        coursesTable.setRowFactory(tv -> {
            TableRow<Course> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Course selectedCourse = row.getItem();
                    showCourseDetails(selectedCourse);
                }
            });
            return row;
        });
    }
    
    private void setupActionsColumn() {
        actionsColumn.setCellFactory(new Callback<TableColumn<Course, Void>, TableCell<Course, Void>>() {
            @Override
            public TableCell<Course, Void> call(TableColumn<Course, Void> param) {
                return new TableCell<Course, Void>() {
                    private final Button dropButton = new Button("Drop");
                    
                    {
                        dropButton.getStyleClass().add("danger-button");
                        dropButton.setOnAction(event -> {
                            Course course = getTableView().getItems().get(getIndex());
                            handleDropCourse(course);
                        });
                    }
                    
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(dropButton);
                        }
                    }
                };
            }
        });
    }
    
    private void loadRegisteredCourses() {
        try {
            List<String> registeredCourseIds = FileHandler.getStudentRegistrations(currentUser.getStudentId());
            List<Course> allCourses = FileHandler.loadCourses();
            
            registeredCourses.clear();
            
            for (String courseId : registeredCourseIds) {
                for (Course course : allCourses) {
                    if (course.getCourseId().equals(courseId)) {
                        registeredCourses.add(course);
                        break;
                    }
                }
            }
            
            updateSummary();
            
            if (registeredCourses.isEmpty()) {
                showMessage("You haven't registered for any courses yet.", "#17a2b8");
            } else {
                showMessage("Loaded " + registeredCourses.size() + " registered courses.", "#28a745");
            }
            
        } catch (Exception e) {
            showMessage("Error loading registered courses: " + e.getMessage(), "#dc3545");
            e.printStackTrace();
        }
    }
    
    private void updateSummary() {
        int totalCourses = registeredCourses.size();
        int totalCredits = registeredCourses.stream().mapToInt(Course::getCredits).sum();
        double averageCredits = totalCourses > 0 ? (double) totalCredits / totalCourses : 0.0;
        
        totalCoursesLabel.setText(String.valueOf(totalCourses));
        totalCreditsLabel.setText(String.valueOf(totalCredits));
        averageCreditsLabel.setText(String.format("%.1f", averageCredits));
    }
    
    private void handleDropCourse(Course course) {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Drop Course");
            alert.setHeaderText("Drop " + course.getCourseName());
            alert.setContentText("Are you sure you want to drop this course?\n\n" +
                                "Course: " + course.getCourseId() + " - " + course.getCourseName() + "\n" +
                                "Credits: " + course.getCredits() + "\n" +
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
                    
                    // Refresh the table
                    loadRegisteredCourses();
                }
            });
            
        } catch (Exception e) {
            showMessage("Error dropping course: " + e.getMessage(), "#dc3545");
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleDropSelected() {
        ObservableList<Course> selectedCourses = coursesTable.getSelectionModel().getSelectedItems();
        
        if (selectedCourses.isEmpty()) {
            showMessage("Please select courses to drop.", "#ffc107");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Drop Multiple Courses");
        alert.setHeaderText("Drop " + selectedCourses.size() + " course(s)");
        
        StringBuilder content = new StringBuilder("Are you sure you want to drop the following courses?\\n\\n");
        for (Course course : selectedCourses) {
            content.append("• ").append(course.getCourseId()).append(" - ").append(course.getCourseName()).append("\\n");
        }
        content.append("\\nThis action cannot be undone.");
        
        alert.setContentText(content.toString());
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    List<Course> allCourses = FileHandler.loadCourses();
                    int droppedCount = 0;
                    
                    for (Course course : new ArrayList<>(selectedCourses)) {
                        // Remove registration
                        FileHandler.removeRegistration(currentUser.getStudentId(), course.getCourseId());
                        
                        // Update course enrollment
                        for (Course c : allCourses) {
                            if (c.getCourseId().equals(course.getCourseId())) {
                                c.decrementEnrollment();
                                break;
                            }
                        }
                        droppedCount++;
                    }
                    
                    // Save updated courses
                    FileHandler.saveCourses(allCourses);
                    
                    showMessage("Successfully dropped " + droppedCount + " course(s).", "#ffc107");
                    loadRegisteredCourses();
                    
                } catch (Exception e) {
                    showMessage("Error dropping courses: " + e.getMessage(), "#dc3545");
                    e.printStackTrace();
                }
            }
        });
    }
    
    private void showCourseDetails(Course course) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Course Details");
        alert.setHeaderText(course.getCourseId() + " - " + course.getCourseName());
        
        StringBuilder content = new StringBuilder();
        content.append("Instructor: ").append(course.getInstructor()).append("\\n");
        content.append("Schedule: ").append(course.getSchedule()).append("\\n");
        content.append("Credits: ").append(course.getCredits()).append("\\n");
        content.append("Enrollment: ").append(course.getCurrentEnrollment()).append("/").append(course.getMaxCapacity()).append("\\n");
        
        if (course.getDescription() != null && !course.getDescription().isEmpty()) {
            content.append("\\nDescription: ").append(course.getDescription()).append("\\n");
        }
        
        if (course.getPrerequisites() != null && !course.getPrerequisites().isEmpty()) {
            content.append("\\nPrerequisites: ").append(course.getPrerequisites()).append("\\n");
        }
        
        alert.setContentText(content.toString());
        alert.showAndWait();
    }
    
    @FXML
    private void handleRefresh() {
        loadRegisteredCourses();
    }
    
    @FXML
    private void handleRegisterMore() {
        Main.switchScene("/fxml/course-registration.fxml", "Course Registration - Student Registration System");
    }
    
    @FXML
    private void handlePrintSchedule() {
        if (registeredCourses.isEmpty()) {
            showMessage("No courses to print. Register for courses first.", "#ffc107");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Course Schedule");
        alert.setHeaderText("Schedule for " + currentUser.getFullName());
        
        StringBuilder schedule = new StringBuilder();
        schedule.append("Student ID: ").append(currentUser.getStudentId()).append("\\n");
        schedule.append("Total Courses: ").append(registeredCourses.size()).append("\\n");
        schedule.append("Total Credits: ").append(registeredCourses.stream().mapToInt(Course::getCredits).sum()).append("\\n\\n");
        
        schedule.append("Registered Courses:\\n");
        schedule.append("─".repeat(50)).append("\\n");
        
        for (Course course : registeredCourses) {
            schedule.append(course.getCourseId()).append(" - ").append(course.getCourseName()).append("\\n");
            schedule.append("  Instructor: ").append(course.getInstructor()).append("\\n");
            schedule.append("  Schedule: ").append(course.getSchedule()).append("\\n");
            schedule.append("  Credits: ").append(course.getCredits()).append("\\n\\n");
        }
        
        alert.setContentText(schedule.toString());
        
        // Make the dialog resizable and larger for better readability
        alert.setResizable(true);
        alert.getDialogPane().setPrefSize(600, 400);
        
        alert.showAndWait();
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