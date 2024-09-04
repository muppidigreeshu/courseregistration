import java.io.*;
import java.util.*;

public class StudentDatabaseStorage {
    private static final String FILE_NAME = "students.txt";

    public static void saveStudents(Collection<StudentDatabase.Student> students) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (StudentDatabase.Student student : students) {
                writer.write(String.join(",",
                    student.getId(),
                    student.getName(),
                    String.join(";", student.getRegisteredCourses())
                ));
                writer.newLine();
            }
        }
    }

    public static Map<String, StudentDatabase.Student> loadStudents() throws IOException {
        Map<String, StudentDatabase.Student> students = new HashMap<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String id = parts[0];
                String name = parts[1];
                List<String> registeredCourses = Arrays.asList(parts[2].split(";"));

                StudentDatabase.Student student = new StudentDatabase.Student(id, name);
                student.getRegisteredCourses().addAll(registeredCourses);
                students.put(id, student);
            }
        }
        return students;
    }
}
