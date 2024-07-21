package Model;

import main.Status;
import main.TaskType;

public class Subtask extends Task {

    private Integer epicId;

    public Subtask(String name, String description, Status status, Integer epicId) {
        super(null, name, description, status); // id будет присвоен позже
        this.epicId = epicId;
    }

    public Subtask(Integer id, String name, String description, Status status, Integer epicId) {
        super(id, name, description, status); // Добавьте тип задачи здесь
        this.epicId = epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUB_TASK;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" + "\n" +
                "EpicId =" + epicId + "," +
                "main.Status=" + super.getStatus() + "," +
                "Id=" + super.getId() + "," +
                "Name=" + super.getName() + "," +
                "Description=" + super.getDescription() + "," +
                '}' + "\n";
    }
}