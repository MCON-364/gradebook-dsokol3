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
        boolean result = gradesByStudent.put(name, new ArrayList<Integer>()) == null;
        if (result) {
            activityLog.add("Added student: " + name);
        }
        return result;
    }

    public boolean addGrade(String name, int grade) {
        List<Integer> list = gradesByStudent.get(name);
        if (list == null) {
            return false;
        }
        boolean result = list.add(grade);
        if (result) {
            activityLog.add("Added grade " + grade + " for student: " + name);
            undoStack.push(gradebook -> {
                List<Integer> grades = gradebook.gradesByStudent.get(name);
                if (grades != null && !grades.isEmpty()) {
                    grades.remove(grades.size() - 1);
                }
            });
        }
        return result;
    }

    public boolean removeStudent(String name) {
        List<Integer> removedGrades = gradesByStudent.get(name);
        boolean result = gradesByStudent.remove(name) != null;
        if (result) {
            activityLog.add("Removed student: " + name);
            final List<Integer> gradesToRestore = new ArrayList<>(removedGrades);
            undoStack.push(gradebook -> {
                gradebook.gradesByStudent.put(name, gradesToRestore);
            });
        }
        return result;
    }

    public Optional<Double> averageFor(String name) {
        List<Integer> grades = gradesByStudent.get(name);
        if (grades == null || grades.isEmpty()) {
            return Optional.empty();
        }
        int sum = 0;
        for (int grade : grades) {
            sum += grade;
        }
        double average = sum / (double) grades.size();
        return Optional.of(average);
    }

    public Optional<String> letterGradeFor(String name) {
        List<Integer> grades = gradesByStudent.get(name);
        if (grades == null || grades.isEmpty()) {
            return Optional.empty();
        }
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
        if (undoStack.isEmpty()) {
            return false;
        }
        
        UndoAction action = undoStack.pop();
        action.undo(this);
        
        activityLog.add("Undo operation performed");
        
        return true;
    }

    public List<String> recentLog(int maxItems) {
        return activityLog.subList(Math.max(activityLog.size() - maxItems, 0), activityLog.size());
    }
}
