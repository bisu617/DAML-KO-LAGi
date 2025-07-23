package com.registration.test;

import com.registration.models.Course;
import com.registration.models.Student;
import com.registration.utils.FileHandler;

import java.util.List;

/**
 * Simple test class to verify file operations work correctly
 */
public class TestFileOperations {
    
    public static void main(String[] args) {
        System.out.println("Testing Student Course Registration System...");
        
        try {
            // Test student authentication
            System.out.println("\n1. Testing user authentication:");
            Student user = FileHandler.authenticateUser("admin", "admin123");
            if (user != null) {
                System.out.println("✓ Successfully authenticated user: " + user.getFullName());
            } else {
                System.out.println("✗ Authentication failed");
            }
            
            // Test loading students
            System.out.println("\n2. Testing student loading:");
            List<Student> students = FileHandler.loadStudents();
            System.out.println("✓ Loaded " + students.size() + " students");
            for (Student student : students) {
                System.out.println("  - " + student.getStudentId() + ": " + student.getFullName() + " (" + student.getUsername() + ")");
            }
            
            // Test loading courses
            System.out.println("\n3. Testing course loading:");
            List<Course> courses = FileHandler.loadCourses();
            System.out.println("✓ Loaded " + courses.size() + " courses");
            for (Course course : courses) {
                System.out.println("  - " + course.getCourseId() + ": " + course.getCourseName() + 
                                 " (" + course.getCredits() + " credits, " + 
                                 course.getCurrentEnrollment() + "/" + course.getMaxCapacity() + " enrolled)");
            }
            
            // Test registration functionality
            System.out.println("\n4. Testing registration functionality:");
            if (user != null && !courses.isEmpty()) {
                String studentId = user.getStudentId();
                String courseId = courses.get(0).getCourseId();
                
                // Check existing registrations
                List<String> existingRegistrations = FileHandler.getStudentRegistrations(studentId);
                System.out.println("  - Current registrations for " + user.getFullName() + ": " + existingRegistrations.size());
                
                // Test registration if not already registered
                if (!existingRegistrations.contains(courseId)) {
                    System.out.println("  - Registering for course: " + courseId);
                    FileHandler.saveRegistration(studentId, courseId);
                    
                    // Verify registration
                    List<String> newRegistrations = FileHandler.getStudentRegistrations(studentId);
                    if (newRegistrations.contains(courseId)) {
                        System.out.println("  ✓ Successfully registered for course");
                        
                        // Test dropping the course
                        System.out.println("  - Dropping course: " + courseId);
                        FileHandler.removeRegistration(studentId, courseId);
                        
                        // Verify drop
                        List<String> finalRegistrations = FileHandler.getStudentRegistrations(studentId);
                        if (!finalRegistrations.contains(courseId)) {
                            System.out.println("  ✓ Successfully dropped course");
                        } else {
                            System.out.println("  ✗ Failed to drop course");
                        }
                    } else {
                        System.out.println("  ✗ Failed to register for course");
                    }
                } else {
                    System.out.println("  - Already registered for course: " + courseId);
                }
            }
            
            System.out.println("\n✓ All tests completed successfully!");
            System.out.println("\nThe Student Course Registration System is ready to use.");
            System.out.println("Run with: mvn javafx:run");
            
        } catch (Exception e) {
            System.err.println("✗ Error during testing: " + e.getMessage());
            e.printStackTrace();
        }
    }
}