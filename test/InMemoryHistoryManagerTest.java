import model.Task;
import service.HistoryManager;
import service.InMemoryHistoryManager;
import service.InMemoryTaskManager;
import service.Managers;
import main.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;
    private InMemoryTaskManager taskManager;

    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager()); // Инициализация taskManager
        historyManager = new InMemoryHistoryManager(); // Инициализация historyManager
    }

    @Test
    void add() {
        Task task = new Task(-1, "Task Name", "Task Description", Status.NEW); // Используем -1 для ID
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не должна быть пустой.");
        assertEquals(1, history.size(), "История должна содержать одну задачу.");
    }

    @Test
    void shouldNotEqualsTaskInHistoryAfterChange() {
        Task task1 = new Task(-1, "Уборка", "Помыть посуду", Status.DONE);
        Task task2 = new Task(-1, "Покупки", "Купить продукты", Status.NEW);

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "История должна содержать две задачи.");

        assertNotEquals(history.get(0).getName(), history.get(1).getName(), "Задачи должны иметь разные названия.");
    }
}