package manager;

public class Managers {
    // Приватный конструктор
    private Managers() {
        // Запретить создание экземпляров этого класса
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}