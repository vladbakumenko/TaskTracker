package tasks;

import manager.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Epic extends Task {
    private LocalDateTime endTime;
    public void setSubtasksInEpic(Map<Integer, Subtask> subtasksInEpic) {
        this.subtasksInEpic = subtasksInEpic;
    }

    private Map<Integer, Subtask> subtasksInEpic = new HashMap<>();

    public Epic() {
        taskType = TaskType.EPIC;
    }
    public Epic(String title, String description) {
        super(title, description);
        taskType = TaskType.EPIC;
    }

    public Epic(String title, String description, int id) {
        super(title, description, id);
        taskType = TaskType.EPIC;
    }

    @Override
    public LocalDateTime getStartTime() {
        LocalDateTime startTime = null;

        if (subtasksInEpic != null && !subtasksInEpic.isEmpty()) {
            for (Subtask subtask : subtasksInEpic.values()) {
                if (startTime == null) {
                    startTime = subtask.getStartTime();
                    continue;
                }
                if (subtask.getStartTime() != null && subtask.getStartTime().isBefore(startTime)) {
                    startTime = subtask.getStartTime();
                }
            }
        }
        super.startTime = startTime;
        return super.startTime;
    }

    @Override
    public Duration getDuration() {
        Duration duration = Duration.ofMinutes(0);

        if (subtasksInEpic != null && !subtasksInEpic.isEmpty()) {
            for (Subtask subtask : subtasksInEpic.values()) {
                if (subtask.getDuration() == null) {
                    continue;
                }
                duration = duration.plusMinutes(subtask.getDuration().toMinutes());
            }
        }
        super.duration = duration;
        return super.duration;
    }

    @Override
    public LocalDateTime getEndTime() {
        LocalDateTime subtasksEndTime = null;

        if (subtasksInEpic != null && !subtasksInEpic.isEmpty()) {
            for (Subtask subtask : subtasksInEpic.values()) {
                if (subtask.getEndTime() != null) {
                    if (subtasksEndTime == null) {
                        subtasksEndTime = subtask.getEndTime();
                        continue;
                    }
                    if (subtask.getEndTime().isAfter(subtasksEndTime)) {
                        subtasksEndTime = subtask.getEndTime();
                    }
                }
            }
        }
        endTime = subtasksEndTime;
        return endTime;
    }

    public Map<Integer, Subtask> getSubtasksInEpic() {
        return subtasksInEpic;
    }

    @Override
    public String toString() {

        return String.format("%d,%s,%s,%s,%s,%s,%s", id, TaskType.EPIC, title, status, description,
                (startTime != null) ? startTime.format(formatter) : "null",
                (duration != null) ? duration.toMinutes() : "null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasksInEpic, epic.subtasksInEpic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasksInEpic);
    }
}
