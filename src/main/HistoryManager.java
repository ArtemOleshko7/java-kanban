package main;

import java.util.*;

public interface HistoryManager {
    void add(Task task);

    List<Task> getHistory();
}
