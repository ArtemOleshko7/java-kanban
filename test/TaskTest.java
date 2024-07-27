import main.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.InMemoryHistoryManager; // Импортируйте класс HistoryManager

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TaskTest {
    private InMemoryTaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager()); // Инициализируем taskManager с HistoryManager
    }

    @Test
    void shouldHaveUniqueIds() {
        int id1 = taskManager.createTask("Task 1", "Description 1", Status.NEW);
        int id2 = taskManager.createTask("Task 2", "Description 2", Status.NEW);

        assertNotEquals(id1, id2, "ID задач должен быть уникальным"); // Проверяем, что ID разные
    }

    @Test
    void shouldHaveFirstIdAsZero() {
        int firstId = taskManager.createTask("Task 1", "Description 1", Status.NEW);
        assertEquals(0, firstId, "Первый ID задачи должен быть 0"); // Проверка, что первый ID равен 0
    }
}