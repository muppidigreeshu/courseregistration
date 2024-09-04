import java.util.*;

public class StudentDatabase {
    private final Map<String, Student> students = new HashMap<>();

    public static class Student {
        private final String id;
        private final String name;
        private final List<String> registeredCourses = new ArrayList<>();

        public Student(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public List<String> getRegisteredCourses() {
            return registeredCourses;
        }

        public void registerCourse(String courseCode) {
            registeredCourses.add(courseCode);
        }
    }

    public Student getStudent(String id) {
        return students.get(id);
    }

    public Collection<Student> getAllStudents() {
        return students.values();
    }

    public void addStudent(Student student) {
        students.put(student.getId(), student);
    }
}
