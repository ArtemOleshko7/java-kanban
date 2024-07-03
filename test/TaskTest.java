import main.Status;
import main.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {


    @Test
    void shouldHaveSameId() {
        Task task1 = new Task("Получить почту", "Забрать письма", Status.NEW);
        Task task2 = new Task("Отправить открытку", "Поздравить друга", Status.IN_PROGRESS);

        task1.setId(15);
        task2.setId(15);

        assertEquals(task1.getId(), task2.getId());
    }

}