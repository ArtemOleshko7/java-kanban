import main.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    Managers managers = new Managers();
    TaskManager taskManager = managers.getDefault();

    @Test
    void testAddTask() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Task task = new Task("Test Task", "Description", Status.NEW);
        Task addedTask = taskManager.addTask(task);
        assertNotNull(addedTask.getId());
        assertEquals("Test Task", addedTask.getName());
        assertEquals("Description", addedTask.getDescription());
        assertEquals(Status.NEW, addedTask.getStatus());
    }

    @Test
    void shouldBeChangedId() {
        Task task = new Task("Уборка", "Помыть посуду", Status.DONE, 50);
        taskManager.addTask(task);
        Task task1 = taskManager.getTask(task.getId());
        assertNotEquals(50, task1.getId());
    }

    @Test
    void shouldBeEqualsAllArgumentsTaskAfterAddInManager() {
        Task task = new Task("Уборка", "Помыть посуду", Status.DONE);
        taskManager.addTask(task);
        Task task1 = taskManager.getTask(task.getId());
        assertEquals("Уборка", task1.getName());
        assertEquals("Помыть посуду", task1.getDescription());
    }
}
