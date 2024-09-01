package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Epic extends Task {
    private List<Subtask> subtaskOfEpicIDs = new ArrayList<>();
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

    public List<Integer> getSubtaskOfEpicIDs() {
        return subtaskOfEpicIDs.stream()
                .map(Subtask::getId)
                .collect(Collectors.toList());
    }


    public void addSubtask(Subtask subtask) {
        // Проверка на null
        if (subtask == null) {
            throw new IllegalArgumentException("Subtask cannot be null");
        }

        // Добавление подзадачи и обновление статуса
        this.subtaskOfEpicIDs.add(subtask);
        calculateStatus(); // Обновляем статус после добавления
    }

    public void removeSubtask(Subtask subtask) {
        // Удаляем ID подзадачи из списка подзадач эпика
        if (subtaskOfEpicIDs.remove(subtask)) {
            // Если подзадача была успешно удалена, обновляем статус после удаления
            calculateStatus();
            updateEndTime(); // Необходимо обновить endTime
        } else {
            throw new IllegalArgumentException("Subtask not found in epic");
        }
    }

    private void updateEndTime() {
        if (subtaskOfEpicIDs.isEmpty()) {
            endTime = null; // Если подзадач нет, endTime также будет null
        } else {
            endTime = subtaskOfEpicIDs.stream()
                    .map(Subtask::getEndTime) // Предполагается, что Subtask имеет метод getEndTime()
                    .max(LocalDateTime::compareTo)
                    .orElse(null);
        }
    }

    public void calculateStatus() {
        if (subtaskOfEpicIDs.isEmpty()) {
            setStatus(TaskStatus.NEW);
            return;
        }

        boolean hasNew = false;
        boolean hasDone = false;
        boolean hasInProgress = false;

        for (Subtask subtask : subtaskOfEpicIDs) {
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

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getNameTask() + '\'' +
                ", description='" + getDescriptionTask() + '\'' +
                ", status=" + getStatus() +
                ", endTime=" + endTime +
                ", subTasks=" + subtaskOfEpicIDs +
                '}';
    }


}