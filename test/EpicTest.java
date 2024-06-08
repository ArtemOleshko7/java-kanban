import main.Epic;
import main.Status;
import main.Task;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EpicTest {
    @Test
    void shouldHaveSameId() {
        Epic epic = new Epic("Уборка", "Помыть посуду", Status.DONE);
        Task epic1 = new Epic("Готовка", "Приготвоить обед", Status.NEW);

        epic.setId(5);
        epic1.setId(5);

        assertEquals(epic.getId(), epic1.getId());
    }
}
