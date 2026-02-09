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
        return gradesByStudent.put(name, new ArrayList<Integer>()) != null;
    }

    public boolean addGrade(String name, int grade) {
        List<Integer> list = gradesByStudent.get(name);
        list.add(grade);
        return gradesByStudent.put(name, list) != null;
    }

    public boolean removeStudent(String name) {
        return gradesByStudent.remove(name)!= null;
    }

    public Optional<Double> averageFor(String name) {
        if (!gradesByStudent.containsKey(name)) return Optional.empty();
        int sum = 0;
        for(int grade : gradesByStudent.get(name)) {
            sum += grade;
        }
        double average = sum / (double) gradesByStudent.get(name).size();
        return Optional.of(average);
    }

    public Optional<String> letterGradeFor(String name) {
        List<Integer> grade =gradesByStudent.get(name);
        Optional<Double> avg = averageFor(grade.toString());
        int avgInt = avg.get().intValue();
        return switch (avgInt) {
            case 100, 90 -> Optional.of("A");
            case 80, 89 -> Optional.of("B");
            case 70, 79 -> Optional.of("C");
            case 60, 69 -> Optional.of("D");
            default -> Optional.of("F");
        };
    }

    public Optional<Double> classAverage() {
        int sum = 0;
        for (int i = 0; i < gradesByStudent.size(); i++){
            sum += gradesByStudent.get(i).get(i);
        }
        double average = sum / (double) gradesByStudent.size();
        return Optional.of(average);
    }

    public boolean undo() {
        return undoStack.pop() != null;
    }

    public List<String> recentLog(int maxItems) {
        return activityLog.subList(Math.max(activityLog.size() - maxItems, 0), activityLog.size());
    }
}
