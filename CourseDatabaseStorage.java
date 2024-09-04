import java.io.*;
import java.util.*;

public class CourseDatabaseStorage {
    private static final String FILE_NAME = "courses.txt";
    
    public static void saveCourses(Collection<CourseDatabase.Course> courses) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (CourseDatabase.Course course : courses) {
                writer.write(String.join(",",
                    course.getCode(),
                    course.getTitle(),
                    course.getDescription(),
                    Integer.toString(course.getCapacity()),
                    Integer.toString(course.getEnrolledStudents()),
                    course.getSchedule()
                ));
                writer.newLine();
            }
        }
    }

    public static Map<String, CourseDatabase.Course> loadCourses() throws IOException {
        Map<String, CourseDatabase.Course> courses = new HashMap<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String code = parts[0];
                String title = parts[1];
                String description = parts[2];
                int capacity = Integer.parseInt(parts[3]);
                int enrolledStudents = Integer.parseInt(parts[4]);
                String schedule = parts[5];

                CourseDatabase.Course course = new CourseDatabase.Course(code, title, description, capacity, schedule);
                for (int i = 0; i < enrolledStudents; i++) {
                    course.enrollStudent(); // Adjust based on how you store enrolled students
                }
                courses.put(code, course);
            }
        }
        return courses;
    }
}
