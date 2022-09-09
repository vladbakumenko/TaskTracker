package tasks;

import manager.TaskStatus;
import manager.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {

    protected int idOfEpic;

    public Subtask() {
        taskType = TaskType.SUBTASK;
    }
    public Subtask(String title, String description, int idOfEpic,
                   LocalDateTime startTime, Duration duration) {
        super(title, description, startTime, duration);
        this.idOfEpic = idOfEpic;
        taskType = TaskType.SUBTASK;
    }

    public Subtask(String title, String description, int id, int idOfEpic,
                   LocalDateTime startTime, Duration duration) {
        super(title, description, id, startTime, duration);
        this.idOfEpic = idOfEpic;
        taskType = TaskType.SUBTASK;
    }

    public Subtask(String title, String description, int idOfEpic, TaskStatus status,
                   LocalDateTime startTime, Duration duration) {
        super(title, description, status, startTime, duration);
        this.idOfEpic = idOfEpic;
        taskType = TaskType.SUBTASK;
    }

    public int getIdOfEpic() {
        return idOfEpic;
    }

    public void setIdOfEpic(int idOfEpic) {
        this.idOfEpic = idOfEpic;
    }

    @Override
    public String toString() {

        return String.format("%d,%s,%s,%s,%s,%s,%s,%d", id, TaskType.SUBTASK, title, status, description,
                (startTime != null) ? startTime.format(formatter) : "null",
                (duration != null) ? duration.toMinutes() : "null", idOfEpic);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return idOfEpic == subtask.idOfEpic;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), idOfEpic);
    }
}
