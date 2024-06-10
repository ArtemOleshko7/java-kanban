import main.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
