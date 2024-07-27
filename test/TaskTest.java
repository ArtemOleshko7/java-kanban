import main.Status;
import model.Task;
import service.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {
    private InMemoryTaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager(); // Инициализируем taskManager перед каждым тестом
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