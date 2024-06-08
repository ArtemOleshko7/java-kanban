import main.Status;
import main.Subtask;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtaskTest {
    @Test
    void shouldHaveSameId() {
        Subtask subtask1 = new Subtask("Уборка в комнате", "Пылесосить и мыть пол", Status.IN_PROGRESS,
                10);
        Subtask subtask2 = new Subtask("Покупки", "Купить продукты", Status.NEW, 10);

        subtask1.setId(8);
        subtask2.setId(8);

        assertEquals(subtask1.getId(), subtask2.getId());
    }

}
