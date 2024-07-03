import main.*;
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
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task("Уборка", "Помыть посуду", Status.DONE, 5);
        Task task2 = new Task("Покупки", "Купить продукты", Status.NEW, 3);

        historyManager.add(task1);
        historyManager.add(task2);

        assertNotEquals(historyManager.getTasks().get(0).getName(),
                historyManager.getTasks().get(1).getName(),
                "Не сохранялись предыдущие данные");
    }

}
