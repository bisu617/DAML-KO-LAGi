package com.registration.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Student model class representing a student in the registration system
 */
public class Student implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String studentId;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private String phoneNumber;
    private String address;
    private List<String> registeredCourses;
    
    public Student() {
        this.registeredCourses = new ArrayList<>();
    }
    
    public Student(String studentId, String firstName, String lastName, String email, 
                   String username, String password) {
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.registeredCourses = new ArrayList<>();
    }
    
    // Getters and Setters
    public String getStudentId() {
        return studentId;
    }
    
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public List<String> getRegisteredCourses() {
        return registeredCourses;
    }
    
    public void setRegisteredCourses(List<String> registeredCourses) {
        this.registeredCourses = registeredCourses;
    }
    
    public void addCourse(String courseId) {
        if (!registeredCourses.contains(courseId)) {
            registeredCourses.add(courseId);
        }
    }
    
    public void removeCourse(String courseId) {
        registeredCourses.remove(courseId);
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    @Override
    public String toString() {
        return "Student{" +
                "studentId='" + studentId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}