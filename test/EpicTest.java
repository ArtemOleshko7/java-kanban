import main.Status;
import model.Epic;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.InMemoryHistoryManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EpicTest {
    @Test
    void shouldHaveSameId() {
        // Создаем экземпляр taskManager с HistoryManager
        InMemoryTaskManager taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());

        // Обратите внимание, что вам может потребоваться изменить конструктор Epic в зависимости от его определения
        Epic epic = new Epic(5, taskManager, "Уборка", "Помыть посуду", Status.DONE);
        Epic epic1 = new Epic(5, taskManager, "Готовка", "Приготовить обед", Status.NEW);

        // Проверяем, что ID у обоих эпиков одинаковый
        assertEquals(epic.getId(), epic1.getId());
    }
}