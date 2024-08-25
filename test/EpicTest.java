import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {

    public Epic epic;
    public Subtask subtask1;
    public Subtask subtask2;
    public Subtask subtask3;
    public TaskManager manager;

    @BeforeEach
    void createEpicAndSubtasks() {
        manager = new InMemoryTaskManager();
        epic = new Epic("Epic", "Description of epic");
        manager.createEpic(epic);

        subtask1 = new Subtask("Subtask 1", "Description of subtask1", TaskStatus.IN_PROGRESS, epic);
        subtask2 = new Subtask("Subtask 2", "Description of subtask2", TaskStatus.IN_PROGRESS, epic);
        subtask3 = new Subtask("Subtask 3", "Description of subtask3", TaskStatus.NEW, epic); // пример, где одна подзадача NEW

        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);

        epic.calculateStatus(); // Вызов метода для пересчета статуса эпика
    }

    @AfterEach
    void removeEpicAndSubtasks() {
        manager.deleteAllEpic();
    }

    @Test
    void shouldReturnNewEpicStatusWithoutSubtasks() {
        manager.deleteAllSubtask();
        assertEquals(TaskStatus.NEW, epic.getStatus(), "Статус пустого эпика не NEW");
    }

    @Test
    void epicCannotBeAddedAsSubtaskToItself() {
        manager.createTask(epic);
    }

    @Test
    void shouldReturnProgressEpicStatusWithSubtasksProgressStatus() {
        // Установим статус подзадач
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        subtask2.setStatus(TaskStatus.IN_PROGRESS);

        // Обновим подзадачи в менеджере
        manager.updateSubtask(subtask1);
        manager.updateSubtask(subtask2);

        // Вызовем метод расчета статуса эпика
        epic.calculateStatus();

        // Проверим, что статус эпика установлен правильно
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус эпика с сабтасками со статусом IN_PROGRESS не IN_PROGRESS");
    }

    @Test
    void shouldReturnProgressEpicStatusWithSubtasksNewAndDoneStatuses() {
        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);
        subtask3.setStatus(TaskStatus.NEW);

        manager.updateSubtask(subtask1);
        manager.updateSubtask(subtask2);
        manager.updateSubtask(subtask3);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус эпика с сабтасками со статусами NEW и DONE не IN_PROGRESS");
    }

    @Test
    void shouldReturnDoneEpicStatusWithAllSubtasksDoneStatus() {
        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);
        subtask3.setStatus(TaskStatus.DONE);

        manager.updateSubtask(subtask1);
        manager.updateSubtask(subtask2);
        manager.updateSubtask(subtask3);

        assertEquals(TaskStatus.DONE, epic.getStatus(), "Статус эпика со всеми сабтасками в статусе DONE не DONE");
    }
}