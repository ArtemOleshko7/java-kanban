import main.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    Managers managers = new Managers();
    TaskManager taskManager = managers.getDefault();

    @Test
    void testAddTask() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Task task = new Task(null, "task1", "Description1", Status.NEW, TaskType.TASK);
        taskManager.addTask(task);

        Task addedTask = taskManager.getTask(task.getId()); // предположим, что есть метод для получения задачи по ID

        assertNotNull(addedTask); // Проверка, что задача добавлена
        assertEquals("task1", addedTask.getName());
        assertEquals("Description1", addedTask.getDescription());
        assertEquals(Status.NEW, addedTask.getStatus());
    }

    @Test
    void shouldBeChangedId() {
        Task task = new Task(null, "task1", "Description1", Status.NEW, TaskType.TASK);
        taskManager.addTask(task);
        Task task1 = taskManager.getTask(task.getId());
        assertNotEquals(50, task1.getId());
    }

    @Test
    void shouldBeEqualsAllArgumentsTaskAfterAddInManager() {
        Task task = new Task(null, "Уборка", "Помыть посуду", Status.NEW, TaskType.TASK);
        taskManager.addTask(task);
        Task task1 = taskManager.getTask(task.getId());
        assertEquals("Уборка", task1.getName());
        assertEquals("Помыть посуду", task1.getDescription());
    }
}
