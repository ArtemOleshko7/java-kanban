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


    public void addSubtask(Subtask subtask) {
        if (!subTasks.contains(subtask)) { // Проверка на уникальность
            this.subTasks.add(subtask);
            calculateStatus(); // Обновляем статус после добавления
        } else {
            throw new IllegalArgumentException("Subtask already exists in the epic");
        }
    }

    public void removeSubtask(Subtask subtask) {
        this.getSubTasks().remove(subtask);
    }

    public void calculateStatus() {
        if (subTasks.isEmpty()) {
            setStatus(TaskStatus.NEW);
            return;
        }

        boolean hasNew = false;
        boolean hasDone = false;
        boolean hasInProgress = false;

        for (Subtask subtask : subTasks) {
            switch (subtask.getStatus()) {
                case NEW:
                    hasNew = true;
                    break;
                case DONE:
                    hasDone = true;
                    break;
                case IN_PROGRESS:
                    hasInProgress = true;
                    break;
                // Другие статусы могут быть обработаны, если необходимо
            }
        }

        if (hasDone && hasNew) {
            setStatus(TaskStatus.IN_PROGRESS);
        } else if (hasInProgress) {
            setStatus(TaskStatus.IN_PROGRESS);
        } else if (hasDone) {
            setStatus(TaskStatus.DONE);
        } else if (hasNew) {
            setStatus(TaskStatus.NEW);
        } else {
            setStatus(TaskStatus.NEW); // Статус по умолчанию, если все подзадачи отсутствуют
        }
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Epic)) return false;
        Epic epic = (Epic) o;
        return getId() == epic.getId(); // Сравниваем по ID
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(getId());
    }


}