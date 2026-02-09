package edu.course.gradebook;

import java.util.*;

public class Gradebook {

    private final Map<String, List<Integer>> gradesByStudent = new HashMap<>();
    private final Deque<UndoAction> undoStack = new ArrayDeque<>();
    private final LinkedList<String> activityLog = new LinkedList<>();

    public Optional<List<Integer>> findStudentGrades(String name) {
        return Optional.ofNullable(gradesByStudent.get(name));
    }

    public boolean addStudent(String name) {
        return gradesByStudent.put(name, new ArrayList<Integer>()) == null;
    }

    public boolean addGrade(String name, int grade) {
        List<Integer> list = gradesByStudent.get(name);
        if (list == null) {
            return false;
        }
        return list.add(grade);
    }

    public boolean removeStudent(String name) {
        return gradesByStudent.remove(name) != null;
    }

    public Optional<Double> averageFor(String name) {
        if (!gradesByStudent.containsKey(name))
            return Optional.empty();
        int sum = 0;
        for (int grade : gradesByStudent.get(name)) {
            sum += grade;
        }
        double average = sum / (double) gradesByStudent.get(name).size();
        return Optional.of(average);
    }

    public Optional<String> letterGradeFor(String name) {
        List<Integer> grade = gradesByStudent.get(name);
        if (grade == null || grade.isEmpty())
            return Optional.empty();
        Optional<Double> avg = averageFor(name);
        int avgInt = avg.get().intValue();
        return switch (avgInt >= 90 ? 'A' : avgInt >= 80 ? 'B' : avgInt >= 70 ? 'C' : avgInt >= 60 ? 'D' : 'F') {
            case 'A' -> {
                yield Optional.of("A");
            }
            case 'B' -> {
                yield Optional.of("B");
            }
            case 'C' -> {
                yield Optional.of("C");
            }
            case 'D' -> {
                yield Optional.of("D");
            }
            default -> {
                yield Optional.of("F");
            }
        };
    }

    public Optional<Double> classAverage() {
        int sum = 0;
        int count = 0;
        for (List<Integer> grades : gradesByStudent.values()) {
            for (int grade : grades) {
                sum += grade;
                count++;
            }
        }
        return count == 0 ? Optional.empty() : Optional.of(sum / (double) count);
    }

    public boolean undo() {
        boolean bl = !undoStack.isEmpty() && undoStack.pop()  != null;
        activityLog.removeLast();
        return bl;
    }

    public List<String> recentLog(int maxItems) {
        return activityLog.subList(Math.max(activityLog.size() - maxItems, 0), activityLog.size());
    }
}
