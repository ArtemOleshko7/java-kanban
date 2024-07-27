import main.Status;
import model.Subtask;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtaskTest {
    @Test
    public void shouldHaveSameId() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        // Здесь явно проверяем, что в taskManager нет задач
        assertEquals(0, taskManager.getAllTasks().size());

        int id = taskManager.createTask("Тестовое задание", "Описание задачи", Status.NEW);
        assertEquals(0, id); // Проверяем, что ID равен 0
    }
}
