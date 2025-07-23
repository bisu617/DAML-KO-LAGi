package com.registration.models;

import java.io.Serializable;

/**
 * Course model class representing a course in the registration system
 */
public class Course implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String courseId;
    private String courseName;
    private String instructor;
    private String schedule;
    private int credits;
    private int maxCapacity;
    private int currentEnrollment;
    private String description;
    private String prerequisites;
    
    public Course() {
        this.currentEnrollment = 0;
    }
    
    public Course(String courseId, String courseName, String instructor, 
                  String schedule, int credits, int maxCapacity) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.instructor = instructor;
        this.schedule = schedule;
        this.credits = credits;
        this.maxCapacity = maxCapacity;
        this.currentEnrollment = 0;
    }
    
    // Getters and Setters
    public String getCourseId() {
        return courseId;
    }
    
    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
    
    public String getCourseName() {
        return courseName;
    }
    
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
    
    public String getInstructor() {
        return instructor;
    }
    
    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }
    
    public String getSchedule() {
        return schedule;
    }
    
    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }
    
    public int getCredits() {
        return credits;
    }
    
    public void setCredits(int credits) {
        this.credits = credits;
    }
    
    public int getMaxCapacity() {
        return maxCapacity;
    }
    
    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }
    
    public int getCurrentEnrollment() {
        return currentEnrollment;
    }
    
    public void setCurrentEnrollment(int currentEnrollment) {
        this.currentEnrollment = currentEnrollment;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getPrerequisites() {
        return prerequisites;
    }
    
    public void setPrerequisites(String prerequisites) {
        this.prerequisites = prerequisites;
    }
    
    public boolean isAvailable() {
        return currentEnrollment < maxCapacity;
    }
    
    public void incrementEnrollment() {
        if (currentEnrollment < maxCapacity) {
            currentEnrollment++;
        }
    }
    
    public void decrementEnrollment() {
        if (currentEnrollment > 0) {
            currentEnrollment--;
        }
    }
    
    public int getAvailableSpots() {
        return maxCapacity - currentEnrollment;
    }
    
    @Override
    public String toString() {
        return "Course{" +
                "courseId='" + courseId + '\'' +
                ", courseName='" + courseName + '\'' +
                ", instructor='" + instructor + '\'' +
                ", schedule='" + schedule + '\'' +
                ", credits=" + credits +
                ", currentEnrollment=" + currentEnrollment +
                ", maxCapacity=" + maxCapacity +
                '}';
    }
}