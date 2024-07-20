package main;

public class Managers {
    private static final HistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
    private static final TaskManager defaultTaskManager = new InMemoryTaskManager();

    public static TaskManager getDefault() {
        return defaultTaskManager;
    }

    public static HistoryManager getDefaultHistory() {
        return inMemoryHistoryManager;
    }
}