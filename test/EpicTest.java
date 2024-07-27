import main.Status;
import model.Epic;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EpicTest {
    @Test
    void shouldHaveSameId() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager(); // Создаем экземпляр taskManager
        Epic epic = new Epic(5, taskManager, "Уборка", "Помыть посуду", Status.DONE);
        Epic epic1 = new Epic(5, taskManager, "Готовка", "Приготовить обед", Status.NEW);

        assertEquals(epic.getId(), epic1.getId());
    }
}
