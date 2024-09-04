import java.util.*;

public class CourseDatabase {
    private final Map<String, Course> courses = new HashMap<>();

    public static class Course {
        private final String code;
        private final String title;
        private final String description;
        private final int capacity;
        private final String schedule;
        private int enrolledStudents = 0;

        public Course(String code, String title, String description, int capacity, String schedule) {
            this.code = code;
            this.title = title;
            this.description = description;
            this.capacity = capacity;
            this.schedule = schedule;
        }

        public String getCode() {
            return code;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public int getCapacity() {
            return capacity;
        }

        public int getEnrolledStudents() {
            return enrolledStudents;
        }

        public String getSchedule() {
            return schedule;
        }

        public void enrollStudent() {
            if (enrolledStudents < capacity) {
                enrolledStudents++;
            }
        }

        public void removeStudent() {
            if (enrolledStudents > 0) {
                enrolledStudents--;
            }
        }
    }

    public Course getCourse(String code) {
        return courses.get(code);
    }

    public Collection<Course> getAllCourses() {
        return courses.values();
    }

    public void addCourse(Course course) {
        courses.put(course.getCode(), course);
    }
}
