import java.util.*;

// THE STUDENT CLASS
class Student {
    private String name;
    private String id;
    private Map<Course, Double> enrolledCourses;

    public Student(String name, String id) {
        this.name = name;
        this.id = id;
        this.enrolledCourses = new HashMap<>();
    }

    // Getters and Setters
    public String getName() { return name; }
    public String getId() { return id; }
    public Map<Course, Double> getEnrolledCourses() { return enrolledCourses; }

    public void enrollInCourse(Course course) {
        if (!enrolledCourses.containsKey(course)) {
            enrolledCourses.put(course, null);
        }
    }

    public void assignGrade(Course course, double grade) {
        if (enrolledCourses.containsKey(course)) {
            enrolledCourses.put(course, grade);
        } else {
            System.out.println("Error: Student is not enrolled in " + course.getCourseCode());
        }
    }
}

//THE COURSE CLASS
class Course {
    private String courseCode;
    private String name;
    private int maxCapacity;
    private int currentEnrollment;

    // STATIC: This shared variable lives in the "Course" blueprint, not individual courses
    private static int totalEnrolledStudents = 0;

    public Course(String courseCode, String name, int maxCapacity) {
        this.courseCode = courseCode;
        this.name = name;
        this.maxCapacity = maxCapacity;
        this.currentEnrollment = 0;
    }

    public String getCourseCode() { return courseCode; }
    public String getName() { return name; }
    public int getMaxCapacity() { return maxCapacity; }

    public boolean incrementEnrollment() {
        if (currentEnrollment < maxCapacity) {
            currentEnrollment++;
            totalEnrolledStudents++; // Increment the global counter
            return true;
        }
        return false;
    }

    public static int getTotalEnrolledStudents() {
        return totalEnrolledStudents;
    }
}

//THE MANAGEMENT CLASS (The "Logic Hub")
class CourseManagement {
    private static List<Course> courses = new ArrayList<>();

    public static void addCourse(String code, String name, int capacity) {
        courses.add(new Course(code, name, capacity));
    }

    public static void enrollStudent(Student student, Course course) {
        if (course.incrementEnrollment()) {
            student.enrollInCourse(course);
            System.out.println("Success: " + student.getName() + " enrolled.");
        } else {
            System.out.println("Error: Course " + course.getCourseCode() + " is full.");
        }
    }

    public static double calculateOverallGrade(Student student) {
        double total = 0;
        int count = 0;
        for (Double grade : student.getEnrolledCourses().values()) {
            if (grade != null) {
                total += grade;
                count++;
            }
        }
        return count == 0 ? 0.0 : total / count;
    }

    public static List<Course> getCourses() { return courses; }
}

//THE MAIN INTERFACE
public class UniversitySystem {
    private static Scanner scanner = new Scanner(System.in);
    private static Map<String, Student> studentDatabase = new HashMap<>();

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            System.out.println("\n--- UNIVERSITY ADMIN PANEL ---");
            System.out.println("1. Add New Course");
            System.out.println("2. Enroll Student");
            System.out.println("3. Assign Grade");
            System.out.println("4. Calculate GPA");
            System.out.println("5. Show Global Enrollment Count");
            System.out.println("6. Exit");
            System.out.print("Choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1 -> createCourse();
                    case 2 -> enroll();
                    case 3 -> grading();
                    case 4 -> showGPA();
                    case 5 -> System.out.println("Total across all courses: " + Course.getTotalEnrolledStudents());
                    case 6 -> running = false;
                    default -> System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                System.out.println("Input error! Please try again.");
            }
        }
    }

    private static void createCourse() {
        System.out.print("Code (e.g. CS101): "); String code = scanner.nextLine();
        System.out.print("Name: "); String name = scanner.nextLine();
        System.out.print("Capacity: "); int cap = Integer.parseInt(scanner.nextLine());
        CourseManagement.addCourse(code, name, cap);
        System.out.println("Course Created.");
    }

    private static void enroll() {
        System.out.print("Student ID: "); String id = scanner.nextLine();
        System.out.print("Student Name: "); String name = scanner.nextLine();
        Student s = studentDatabase.computeIfAbsent(id, k -> new Student(name, id));

        System.out.print("Enter Course Code: "); String code = scanner.nextLine();
        Course c = findCourse(code);
        if (c != null) CourseManagement.enrollStudent(s, c);
        else System.out.println("Course not found!");
    }

    private static void grading() {
        System.out.print("Student ID: "); String id = scanner.nextLine();
        Student s = studentDatabase.get(id);
        System.out.print("Course Code: "); String code = scanner.nextLine();
        Course c = findCourse(code);

        if (s != null && c != null) {
            System.out.print("Enter Grade (0-100): ");
            double grade = Double.parseDouble(scanner.nextLine());
            s.assignGrade(c, grade);
            System.out.println("Grade Assigned.");
        } else {
            System.out.println("Student or Course not found!");
        }
    }

    private static void showGPA() {
        System.out.print("Student ID: "); String id = scanner.nextLine();
        Student s = studentDatabase.get(id);
        if (s != null) {
            System.out.println(s.getName() + "'s GPA: " + CourseManagement.calculateOverallGrade(s));
        } else {
            System.out.println("Student not found.");
        }
    }

    private static Course findCourse(String code) {
        for (Course c : CourseManagement.getCourses()) {
            if (c.getCourseCode().equalsIgnoreCase(code)) return c;
        }
        return null;
    }
}