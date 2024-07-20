import main.Status;
import main.Task;
import main.TaskType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {


    @Test
    void shouldHaveSameId() {
        Task task1 = new Task(null, "Получить почту", "Забрать письма", Status.NEW, TaskType.TASK);
        Task task2 = new Task(null, "Отправить открытку", "Поздравить друга", Status.IN_PROGRESS, TaskType.TASK);

        int commonId = 15;
        task1.setId(commonId);
        task2.setId(commonId);

        assertEquals(task1.getId(), task2.getId()); // Проверка, что ID одинаковые
    }

}