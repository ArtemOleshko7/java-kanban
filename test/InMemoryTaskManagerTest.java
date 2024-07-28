import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    TaskManager taskManager = Managers.getDefault();

    @Test
    void shouldHaveDifferentIdAfterTaskCreation() {
        Task task = new Task("Уборка", "Помыть посуду", TaskStatus.DONE, 50);
        taskManager.createTask(task);
        Task task1 = taskManager.getTask(task.getId());
        assertNotEquals(50, task1.getId());
    }

    @Test
    void shouldMatchAllTaskAttributesAfterCreation() {
        Task task = new Task("Уборка", "Помыть посуду", TaskStatus.DONE);
        taskManager.createTask(task);
        Task task1 = taskManager.getTask(task.getId());
        assertEquals("Уборка", task1.getNameTask());
        assertEquals("Помыть посуду", task1.getDescriptionTask());
    }

    @Test
    void shouldSuccessfullyAddAndFetchTasksAndEpics() {
        Task task = new Task("Уборка", "Помыть посуду", TaskStatus.DONE);
        taskManager.createTask(task);
        final List<Task> tasks = taskManager.getAllTasks();
        Epic epic = new Epic("Переезд", "Собрать все вещи");
        taskManager.createEpic(epic);
        final List<Task> epics = taskManager.getAllEpic();
        Subtask subtask = new Subtask("Отдых", "Ничего не делать", TaskStatus.NEW, epic);
        taskManager.createSubtask(subtask);
        final List<Task> subtasks = taskManager.getAllSubtasks();
        assertNotNull(subtasks, "История не пустая.");
        assertNotNull(epics, "История не пустая.");
        assertNotNull(tasks, "История не пустая.");
        assertEquals(task, taskManager.getTask(task.getId()));
        assertEquals(epic, taskManager.getEpic(epic.getId()));
        assertEquals(subtask, taskManager.getSubtask(subtask.getId()));
    }

    @Test
    void shouldNotContainDeletedSubtaskIdInEpic() {
        Epic epic = new Epic("Переезд", "Собрать все вещи");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Собрать вещи", "Разложить вещи по коробкам", TaskStatus.NEW, epic);
        taskManager.createSubtask(subtask);
        Subtask subtask1 = new Subtask("Убрать квартиру", "Убрать", TaskStatus.NEW, epic);
        taskManager.createSubtask(subtask1);
        taskManager.deleteSubtaskById(subtask1.getId());
        taskManager.updateEpic(epic);
        List<Subtask> subtaskList = epic.getSubTasks();
        for (Subtask sub : subtaskList) {
            assertNotEquals(sub.getId(), subtask1.getId(), "Эпик содержит не актуальный id подзадачи.");
        }
    }


}