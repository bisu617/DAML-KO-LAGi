package com.registration.utils;

import com.registration.models.Course;
import com.registration.models.Student;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * FileHandler utility class for managing data persistence
 */
public class FileHandler {
    private static final String DATA_DIR = "data";
    private static final String STUDENTS_FILE = DATA_DIR + "/students.txt";
    private static final String COURSES_FILE = DATA_DIR + "/courses.txt";
    private static final String REGISTRATIONS_FILE = DATA_DIR + "/registrations.txt";
    
    static {
        createDataDirectory();
        initializeDefaultData();
    }
    
    private static void createDataDirectory() {
        try {
            Path dataPath = Paths.get(DATA_DIR);
            if (!Files.exists(dataPath)) {
                Files.createDirectories(dataPath);
            }
        } catch (IOException e) {
            System.err.println("Error creating data directory: " + e.getMessage());
        }
    }
    
    private static void initializeDefaultData() {
        // Initialize default courses if file doesn't exist
        if (!Files.exists(Paths.get(COURSES_FILE))) {
            initializeDefaultCourses();
        }
        
        // Initialize default admin user if file doesn't exist
        if (!Files.exists(Paths.get(STUDENTS_FILE))) {
            initializeDefaultStudents();
        }
    }
    
    private static void initializeDefaultCourses() {
        List<Course> defaultCourses = new ArrayList<>();
        
        Course course1 = new Course("CS101", "Introduction to Programming", "Dr. Smith", "MWF 10:00-11:00", 3, 30);
        course1.setDescription("Basic programming concepts using Java");
        course1.setPrerequisites("None");
        
        Course course2 = new Course("CS201", "Data Structures", "Dr. Johnson", "TTh 2:00-3:30", 3, 25);
        course2.setDescription("Advanced data structures and algorithms");
        course2.setPrerequisites("CS101");
        
        Course course3 = new Course("MATH101", "Calculus I", "Prof. Wilson", "MWF 9:00-10:00", 4, 40);
        course3.setDescription("Differential and integral calculus");
        course3.setPrerequisites("High School Math");
        
        Course course4 = new Course("ENG101", "English Composition", "Dr. Brown", "TTh 11:00-12:30", 3, 35);
        course4.setDescription("Academic writing and composition");
        course4.setPrerequisites("None");
        
        Course course5 = new Course("PHYS101", "General Physics", "Dr. Davis", "MWF 1:00-2:00", 4, 30);
        course5.setDescription("Mechanics and thermodynamics");
        course5.setPrerequisites("MATH101");
        
        defaultCourses.add(course1);
        defaultCourses.add(course2);
        defaultCourses.add(course3);
        defaultCourses.add(course4);
        defaultCourses.add(course5);
        
        saveCourses(defaultCourses);
    }
    
    private static void initializeDefaultStudents() {
        List<Student> defaultStudents = new ArrayList<>();
        
        Student admin = new Student("ADMIN001", "Admin", "User", "admin@university.edu", "admin", "admin123");
        admin.setPhoneNumber("555-0000");
        admin.setAddress("Admin Office");
        
        Student student1 = new Student("STU001", "John", "Doe", "john.doe@student.edu", "johndoe", "password123");
        student1.setPhoneNumber("555-1234");
        student1.setAddress("123 Student St");
        
        defaultStudents.add(admin);
        defaultStudents.add(student1);
        
        saveStudents(defaultStudents);
    }
    
    // Student operations
    public static List<Student> loadStudents() {
        List<Student> students = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(STUDENTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 6) {
                    Student student = new Student(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]);
                    if (parts.length > 6) student.setPhoneNumber(parts[6]);
                    if (parts.length > 7) student.setAddress(parts[7]);
                    students.add(student);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading students: " + e.getMessage());
        }
        return students;
    }
    
    public static void saveStudents(List<Student> students) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(STUDENTS_FILE))) {
            for (Student student : students) {
                writer.println(student.getStudentId() + "|" + 
                             student.getFirstName() + "|" + 
                             student.getLastName() + "|" + 
                             student.getEmail() + "|" + 
                             student.getUsername() + "|" + 
                             student.getPassword() + "|" + 
                             (student.getPhoneNumber() != null ? student.getPhoneNumber() : "") + "|" + 
                             (student.getAddress() != null ? student.getAddress() : ""));
            }
        } catch (IOException e) {
            System.err.println("Error saving students: " + e.getMessage());
        }
    }
    
    // Course operations
    public static List<Course> loadCourses() {
        List<Course> courses = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(COURSES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 6) {
                    Course course = new Course(parts[0], parts[1], parts[2], parts[3], 
                                             Integer.parseInt(parts[4]), Integer.parseInt(parts[5]));
                    if (parts.length > 6) course.setCurrentEnrollment(Integer.parseInt(parts[6]));
                    if (parts.length > 7) course.setDescription(parts[7]);
                    if (parts.length > 8) course.setPrerequisites(parts[8]);
                    courses.add(course);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading courses: " + e.getMessage());
        }
        return courses;
    }
    
    public static void saveCourses(List<Course> courses) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(COURSES_FILE))) {
            for (Course course : courses) {
                writer.println(course.getCourseId() + "|" + 
                             course.getCourseName() + "|" + 
                             course.getInstructor() + "|" + 
                             course.getSchedule() + "|" + 
                             course.getCredits() + "|" + 
                             course.getMaxCapacity() + "|" + 
                             course.getCurrentEnrollment() + "|" + 
                             (course.getDescription() != null ? course.getDescription() : "") + "|" + 
                             (course.getPrerequisites() != null ? course.getPrerequisites() : ""));
            }
        } catch (IOException e) {
            System.err.println("Error saving courses: " + e.getMessage());
        }
    }
    
    // Registration operations
    public static void saveRegistration(String studentId, String courseId) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(REGISTRATIONS_FILE, true))) {
            writer.println(studentId + "|" + courseId);
        } catch (IOException e) {
            System.err.println("Error saving registration: " + e.getMessage());
        }
    }
    
    public static void removeRegistration(String studentId, String courseId) {
        List<String> registrations = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(REGISTRATIONS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2 && !(parts[0].equals(studentId) && parts[1].equals(courseId))) {
                    registrations.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading registrations: " + e.getMessage());
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(REGISTRATIONS_FILE))) {
            for (String registration : registrations) {
                writer.println(registration);
            }
        } catch (IOException e) {
            System.err.println("Error saving registrations: " + e.getMessage());
        }
    }
    
    public static List<String> getStudentRegistrations(String studentId) {
        List<String> courseIds = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(REGISTRATIONS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2 && parts[0].equals(studentId)) {
                    courseIds.add(parts[1]);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading registrations: " + e.getMessage());
        }
        return courseIds;
    }
    
    public static Student authenticateUser(String username, String password) {
        List<Student> students = loadStudents();
        for (Student student : students) {
            if (student.getUsername().equals(username) && student.getPassword().equals(password)) {
                // Load registered courses
                List<String> registeredCourses = getStudentRegistrations(student.getStudentId());
                student.setRegisteredCourses(registeredCourses);
                return student;
            }
        }
        return null;
    }
}