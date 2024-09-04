import fi.iki.elonen.NanoHTTPD;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class StudentCourseRegistrationSystem extends NanoHTTPD {
    private final CourseDatabase courseDatabase = new CourseDatabase();
    private final StudentDatabase studentDatabase = new StudentDatabase();

    public StudentCourseRegistrationSystem() throws IOException {
        super(8081);
        start(SOCKET_READ_TIMEOUT, false);
        System.out.println("Server started on http://localhost:8081/");

        // Load courses and students from storage
        try {
            loadData();
        } catch (IOException e) {
            System.err.println("Failed to load data: " + e.getMessage());
        }
    }

    private void loadData() throws IOException {
        courseDatabase.getAllCourses().addAll(CourseDatabaseStorage.loadCourses().values());
        studentDatabase.getAllStudents().addAll(StudentDatabaseStorage.loadStudents().values());
    }

    private void saveData() throws IOException {
        CourseDatabaseStorage.saveCourses(courseDatabase.getAllCourses());
        StudentDatabaseStorage.saveStudents(studentDatabase.getAllStudents());
    }

    @Override
    public Response serve(IHTTPSession session) {
        String msg = "<html><head><style>"
                   + "body { font-family: Arial, sans-serif; background-color: #e0f7fa; color: #333; text-align: center; padding: 20px; }"
                   + "h1 { color: #00796b; }"
                   + "form { margin: 0 auto; width: 300px; padding: 20px; background-color: #ffffff; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }"
                   + "label { font-size: 16px; display: block; margin: 10px 0 5px; }"
                   + "input[type='text'], input[type='submit'] { width: 100%; padding: 10px; margin: 10px 0; border: 1px solid #ccc; border-radius: 5px; box-sizing: border-box; }"
                   + "input[type='submit'] { background-color: #00796b; color: white; border: none; cursor: pointer; }"
                   + "input[type='submit']:hover { background-color: #004d40; }"
                   + "p { font-size: 18px; font-weight: bold; }"
                   + "</style></head><body>";

        Map<String, List<String>> parameters = session.getParameters();

        // Display available courses
        if (parameters.get("action") == null || "listCourses".equals(parameters.get("action").get(0))) {
            msg += "<h1>Available Courses</h1>";
            msg += "<table border='1' style='margin: 0 auto;'><tr><th>Code</th><th>Title</th><th>Description</th><th>Capacity</th><th>Enrolled</th><th>Schedule</th></tr>";

            for (CourseDatabase.Course course : courseDatabase.getAllCourses()) {
                msg += "<tr>";
                msg += "<td>" + course.getCode() + "</td>";
                msg += "<td>" + course.getTitle() + "</td>";
                msg += "<td>" + course.getDescription() + "</td>";
                msg += "<td>" + course.getCapacity() + "</td>";
                msg += "<td>" + course.getEnrolledStudents() + "</td>";
                msg += "<td>" + course.getSchedule() + "</td>";
                msg += "</tr>";
            }

            msg += "</table>";
            msg += "<br><a href='?action=registerStudent'>Register for a Course</a>";
            msg += "<br><a href='?action=dropCourse'>Drop a Course</a>";
        } else if ("registerStudent".equals(parameters.get("action").get(0))) {
            msg += "<h1>Register for a Course</h1>";

            // Show form for registering a student
            msg += "<form action='?' method='get'>";
            msg += "<label for='studentId'>Student ID:</label>";
            msg += "<input type='text' id='studentId' name='studentId'><br>";
            msg += "<label for='courseCode'>Course Code:</label>";
            msg += "<input type='text' id='courseCode' name='courseCode'><br>";
            msg += "<input type='hidden' name='action' value='processRegistration'>";
            msg += "<input type='submit' value='Register'>";
            msg += "</form>";
        } else if ("dropCourse".equals(parameters.get("action").get(0))) {
            msg += "<h1>Drop a Course</h1>";

            // Show form for dropping a course
            msg += "<form action='?' method='get'>";
            msg += "<label for='studentId'>Student ID:</label>";
            msg += "<input type='text' id='studentId' name='studentId'><br>";
            msg += "<label for='courseCode'>Course Code:</label>";
            msg += "<input type='text' id='courseCode' name='courseCode'><br>";
            msg += "<input type='hidden' name='action' value='processDrop'>";
            msg += "<input type='submit' value='Drop'>";
            msg += "</form>";
        } else if ("processRegistration".equals(parameters.get("action").get(0))) {
            // Handle course registration
            String studentId = parameters.get("studentId").get(0);
            String courseCode = parameters.get("courseCode").get(0);

            CourseDatabase.Course course = courseDatabase.getCourse(courseCode);
            StudentDatabase.Student student = studentDatabase.getStudent(studentId);

            if (course != null && student != null) {
                if (course.getEnrolledStudents() < course.getCapacity()) {
                    course.enrollStudent();
                    student.registerCourse(courseCode);
                    msg += "<p>Registration successful!</p>";
                } else {
                    msg += "<p>Course is full!</p>";
                }
            } else {
                msg += "<p>Invalid student ID or course code.</p>";
            }
            msg += "<a href='?action=listCourses'>Back to Course List</a>";
        } else if ("processDrop".equals(parameters.get("action").get(0))) {
            // Handle course drop
            String studentId = parameters.get("studentId").get(0);
            String courseCode = parameters.get("courseCode").get(0);

            CourseDatabase.Course course = courseDatabase.getCourse(courseCode);
            StudentDatabase.Student student = studentDatabase.getStudent(studentId);

            if (course != null && student != null) {
                if (student.getRegisteredCourses().remove(courseCode)) {
                    course.removeStudent();
                    msg += "<p>Course dropped successfully!</p>";
                } else {
                    msg += "<p>Student is not registered for this course.</p>";
                }
            } else {
                msg += "<p>Invalid student ID or course code.</p>";
            }
            msg += "<a href='?action=listCourses'>Back to Course List</a>";
        }

        msg += "</body></html>";

        // Save data before responding
        try {
            saveData();
        } catch (IOException e) {
            System.err.println("Failed to save data: " + e.getMessage());
        }

        return newFixedLengthResponse(msg);
    }

    public static void main(String[] args) {
        try {
            new StudentCourseRegistrationSystem();
        } catch (IOException ioe) {
            System.err.println("Couldn't start server:\n" + ioe);
        }
    }
}
