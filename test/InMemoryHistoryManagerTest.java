import Model.Task;
import Service.HistoryManager;
import Service.InMemoryHistoryManager;
import Service.Managers;
import main.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {
    Managers managers = new Managers();
    HistoryManager historyManager = managers.getDefaultHistory();

    @Test
    void add() {
        Task task = new Task(1, "Task Name", "Task Description", Status.NEW);
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void shouldNotEqualsTaskInHistoryAfterChange() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task(1, "Уборка", "Помыть посуду", Status.DONE);
        Task task2 = new Task(2, "Покупки", "Купить продукты", Status.NEW);

        historyManager.add(task1);
        historyManager.add(task2);

        assertNotEquals(historyManager.getTasks().get(0).getName(),
                historyManager.getTasks().get(1).getName(),
                "Не сохранялись предыдущие данные");
    }

}
