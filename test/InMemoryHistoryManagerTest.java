import main.HistoryManager;
import main.Managers;
import main.Status;
import main.Task;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {
    Managers managers = new Managers();
    HistoryManager historyManager = managers.getDefaultHistory();

    @Test
    void add() {
        Task task = new Task("Уборка", "Помыть посуду", Status.DONE, 5);
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void shouldNotEqualsTaskInHistoryAfterChange() {
        Task task1 = new Task("Уборка", "Помыть посуду", Status.DONE, 5);
        Task task2 = new Task(task1); // Создаем копию task1
        historyManager.add(task1);

        task2.setName("Убрать кухню");
        historyManager.add(task2);

        final List<Task> history = historyManager.getHistory();
        assertNotEquals(history.get(0).getName(), history.get(1).getName(), "Не сохранялись предыдущие данные");
    }
}
