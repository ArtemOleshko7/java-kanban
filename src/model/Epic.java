package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Subtask> subTasks = new ArrayList<>();


    public Epic(String nameTask, String descriptionTask) {
        super(nameTask, descriptionTask, TaskStatus.NEW); // Устанавливаем начальный статус как TaskStatus.NEW
    }

    public Epic(String nameTask, String descriptionTask, TaskStatus status, int id) {
        super(nameTask, descriptionTask, status, id);
    }

    public List<Subtask> getSubTasks() {
        return subTasks;
    }

    public void changeSubTask(Subtask subtask) {
        List<Subtask> subTasks = this.getSubTasks(); // Получаем список подзадач

        for (int i = 0; i < subTasks.size(); i++) {
            if (subTasks.get(i).getId() == subtask.getId()) {
                subTasks.set(i, subtask); // Заменяем подзадачу
                return;
            }
        }
    }

    public void addSubtask(Subtask subtask) {
        this.getSubTasks().add(subtask);
    }

    public void removeSubtask(Subtask subtask) {
        this.getSubTasks().remove(subtask);
    }

    public void calculateStatus() {
        int sizeSubtask = this.getSubTasks().size();
        int numberOfStatusDone = 0;
        int numberOfStatusProgress = 0;

        for (Subtask element : this.getSubTasks()) {
            if (element.getStatus().equals(TaskStatus.NEW)) {
                this.setStatus(TaskStatus.NEW);
                return; // Прекращаем исполнение, если есть подзадача со статусом NEW
            } else if (element.getStatus().equals(TaskStatus.DONE)) {
                numberOfStatusDone++;
            } else if (element.getStatus().equals(TaskStatus.IN_PROGRESS)) {
                numberOfStatusProgress++;
            }
        }

        TaskStatus result = TaskStatus.NEW; // По умолчанию

        if (numberOfStatusDone == sizeSubtask) {
            result = TaskStatus.DONE;
        } else if (numberOfStatusProgress > 0 || numberOfStatusDone > 0) {
            result = TaskStatus.IN_PROGRESS;
        }

        this.setStatus(result);
    }


}