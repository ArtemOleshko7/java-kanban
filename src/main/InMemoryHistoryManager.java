package main;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    static final int maxNumberOfHistory = 10;
    List<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (history.size() < maxNumberOfHistory) {
            history.add(new Task(task));
        } else {
            history.remove(0);
            history.add(new Task(task));
        }
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
