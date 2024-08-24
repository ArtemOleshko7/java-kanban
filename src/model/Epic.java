package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Subtask> subTasks = new ArrayList<>();
    private LocalDateTime endTime;


    public Epic(String nameTask, String descriptionTask) {
        super(nameTask, descriptionTask, TaskStatus.NEW); // Устанавливаем начальный статус как TaskStatus.NEW
    }

    public Epic(String nameTask, String descriptionTask, TaskStatus status, int id) {
        super(nameTask, descriptionTask, status, id);
    }

    public Epic(String nameTask, String descriptionTask, LocalDateTime endTime) {
        super(nameTask, descriptionTask, TaskStatus.NEW); // Устанавливаем начальный статус как TaskStatus.NEW
        this.endTime = endTime; // Инициализируем endTime
    }

    public Epic(String nameTask, String descriptionTask, TaskStatus status, int id, LocalDateTime endTime) {
        super(nameTask, descriptionTask, status, id);
        this.endTime = endTime; // Инициализируем endTime
    }

    public Epic(String nameTask, String descriptionTask, TaskStatus status, int id, Duration duration, LocalDateTime startTime) {
        super(nameTask, descriptionTask, status, id);
        this.duration = duration;
        this.startTime = startTime;
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
        this.subTasks.add(subtask);
        calculateStatus(); // Обновляем статус после добавления
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

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }


}