package service;

import model.Task;

import java.util.*;

public interface HistoryManager {
    void add(Task task);

    void remove(int id);

    void removeAll();

    List<Task> getHistory();
}
