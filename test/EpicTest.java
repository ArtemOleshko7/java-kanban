import model.Epic;
import main.Status;
import model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EpicTest {
    @Test
    void shouldHaveSameId() {
        Epic epic = new Epic(5, "Уборка", "Помыть посуду", Status.DONE);
        Epic epic1 = new Epic(5, "Готовка", "Приготовить обед", Status.NEW);

        assertEquals(epic.getId(), epic1.getId());
    }
}
