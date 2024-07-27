package service;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory()); // Передаем HistoryManager в конструктор
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}