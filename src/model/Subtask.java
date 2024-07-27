package model;

import main.Status;
import main.TaskType;
import service.InMemoryTaskManager;

public class Subtask extends Task {

    private final Integer epicId;

    public Subtask(InMemoryTaskManager taskManager, String name, String description, Status status, Integer epicId) {
        super(-1, name, description, status); // Используйте -1 как ID по умолчанию
        this.epicId = epicId;
    }

    public Subtask(Integer id, String name, String description, Status status, Integer epicId) {
        super(id, name, description, status); // Передайте id
        this.epicId = epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUB_TASK;
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return String.format("SubTask{\nEpic Id=%d, Status=%s, Id=%d, Name='%s', Description='%s'}\n",
                epicId, super.getStatus(), super.getId(), super.getName(), super.getDescription());
    }
}