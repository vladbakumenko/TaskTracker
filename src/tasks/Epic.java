package tasks;

import manager.TaskType;

import java.time.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class Epic extends Task {
    private LocalDateTime endTime;
    private Map<Integer, Subtask> subtasksInEpic = new HashMap<>();

    public Epic(String title, String description, int id) {
        super(title, description, id);
    }

    @Override
    public Optional<LocalDateTime> getStartTime() {
        LocalDateTime startTime = LocalDateTime.MAX;

        if (!subtasksInEpic.isEmpty()) {
            for (Subtask subtask : subtasksInEpic.values()) {
                if (subtask.getStartTime().get().isBefore(startTime)) {
                    startTime = subtask.getStartTime().get();
                }
            }
            super.startTime = startTime;
            return Optional.of(startTime);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Duration> getDuration() {
        Duration duration = Duration.ofMinutes(0);

        if (!subtasksInEpic.isEmpty()) {
            for (Subtask subtask : subtasksInEpic.values()) {
                duration = duration.plusMinutes(subtask.getDuration().get().toMinutes());
            }
            super.duration = duration;
        }
        return Optional.of(super.duration);
    }

    @Override
    public Optional<LocalDateTime> getEndTime() {
        LocalDateTime localDateTime = LocalDateTime.now();

        if (!subtasksInEpic.isEmpty()) {
            for (Subtask subtask : subtasksInEpic.values()) {
                if (subtask.getEndTime().get().isAfter(localDateTime)) {
                    localDateTime = subtask.getEndTime().get();
                }
            }
            endTime = localDateTime;
        }
        return Optional.of(endTime);
    }

    public Map<Integer, Subtask> getSubtasksInEpic() {
        return subtasksInEpic;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%s,%s", id, TaskType.EPIC, title, status, description,
                getStartTime().get().format(formatter), getDuration().get().toMinutes());
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
