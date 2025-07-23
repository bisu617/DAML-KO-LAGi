# Student Course Registration System

A complete JavaFX application for managing student course registration with a user-friendly interface and file-based data persistence.

## Features

### 🔐 Authentication System
- Secure login with username/password
- File-based user authentication
- Session management

### 📊 Dashboard
- Welcome interface with quick stats
- Navigation to all major features
- Course registration summary

### 👤 Profile Management
- View and edit student information
- Update personal details (name, email, phone, address)
- Password management
- Registration statistics

### 📚 Course Registration
- Browse available courses with detailed information
- View course descriptions, prerequisites, and schedules
- Register for courses with capacity checking
- Drop courses with confirmation dialogs

### 📋 Course Management
- View all registered courses in a table format
- Drop individual or multiple courses
- Print course schedule
- Registration summary and statistics

### 💾 Data Persistence
- File-based storage for all data
- Automatic data initialization with sample content
- Real-time updates and synchronization

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- JavaFX 17+ (included as Maven dependency)

## Installation & Running

### 1. Clone the Repository
```bash
git clone <repository-url>
cd DAML-KO-LAGi
```

### 2. Build the Project
```bash
mvn clean compile
```

### 3. Run the Application
```bash
mvn javafx:run
```

### 4. Alternative: Create Executable JAR
```bash
mvn clean package
java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -jar target/student-course-registration-1.0.0.jar
```

## Demo Credentials

The application comes with pre-configured demo accounts:

### Admin Account
- **Username:** admin
- **Password:** admin123
- **Name:** Admin User

### Student Account
- **Username:** johndoe
- **Password:** password123
- **Name:** John Doe

## Sample Data

The application automatically creates sample data including:

### Courses
- **CS101** - Introduction to Programming (3 credits)
- **CS201** - Data Structures (3 credits)
- **MATH101** - Calculus I (4 credits)
- **ENG101** - English Composition (3 credits)
- **PHYS101** - General Physics (4 credits)

### Features Demonstrated
- Course enrollment with capacity limits
- Prerequisites checking
- Registration and drop functionality
- Profile management
- Data persistence

## File Structure

```
src/
├── main/
    ├── java/
    │   ├── com/
    │   │   ├── registration/
    │   │   │   ├── Main.java                    # Application entry point
    │   │   │   ├── controllers/                 # UI Controllers
    │   │   │   │   ├── LoginController.java
    │   │   │   │   ├── DashboardController.java
    │   │   │   │   ├── ProfileController.java
    │   │   │   │   ├── CourseRegistrationController.java
    │   │   │   │   └── ViewCoursesController.java
    │   │   │   ├── models/                      # Data Models
    │   │   │   │   ├── Student.java
    │   │   │   │   └── Course.java
    │   │   │   └── utils/                       # Utilities
    │   │   │       └── FileHandler.java
    │   │   └── module-info.java                 # Module configuration
    └── resources/
        ├── fxml/                                # UI Layouts
        │   ├── login.fxml
        │   ├── dashboard.fxml
        │   ├── profile.fxml
        │   ├── course-registration.fxml
        │   └── view-courses.fxml
        └── css/
            └── styles.css                       # Application styling
```

## Data Storage

The application uses simple text files for data storage:

- `data/students.txt` - Student information
- `data/courses.txt` - Course catalog
- `data/registrations.txt` - Student course registrations

## Architecture

### Model-View-Controller (MVC) Pattern
- **Models**: Student and Course data classes
- **Views**: FXML files defining UI layouts
- **Controllers**: Handle user interactions and business logic

### Key Components
- **FileHandler**: Manages all file I/O operations
- **Main**: Application entry point and scene management
- **Controllers**: Handle specific screen functionality

## Technical Features

### UI/UX
- Professional styling with CSS
- Responsive design
- Consistent navigation
- Confirmation dialogs for important actions
- Input validation and error handling

### Data Management
- Automatic file creation and initialization
- Data integrity checks
- Real-time updates
- Error handling for file operations

### Validation
- Email format validation
- Password requirements
- Required field checking
- Course capacity validation

## Testing

Run the included test to verify functionality:

```bash
mvn compile exec:java -Dexec.mainClass="com.registration.test.TestFileOperations"
```

This test verifies:
- User authentication
- Data loading and saving
- Course registration/dropping
- File operations

## Customization

### Adding New Courses
Edit `data/courses.txt` or use the FileHandler utility methods to programmatically add courses.

### Adding New Students
Edit `data/students.txt` or extend the FileHandler to include user registration functionality.

### Modifying UI
- Update FXML files in `src/main/resources/fxml/`
- Modify CSS in `src/main/resources/css/styles.css`
- Adjust controllers in `src/main/java/com/registration/controllers/`

## Troubleshooting

### Common Issues

1. **JavaFX Module Errors**
   - Ensure JavaFX is properly configured in your PATH
   - Use the Maven JavaFX plugin: `mvn javafx:run`

2. **File Permission Errors**
   - Ensure the application has write permissions for the `data/` directory

3. **Display Issues on Linux**
   - Set DISPLAY environment variable: `export DISPLAY=:0`
   - Install JavaFX for your platform

### Getting Help

If you encounter issues:
1. Check the console output for error messages
2. Verify all prerequisites are installed
3. Ensure data files are not corrupted
4. Try running the test class to isolate issues

## License

This project is for educational purposes. Feel free to modify and extend as needed.