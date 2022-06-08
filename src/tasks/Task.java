package tasks;

import manager.TaskStatus;
import manager.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

public class Task {
    protected String title;
    protected String description;
    protected int id;

    protected TaskStatus status = TaskStatus.NEW;

    protected Duration duration;
    protected LocalDateTime startTime;
    protected DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy|HH:mm");


    public Task(String title, String description, int id, LocalDateTime startTime, Duration duration) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String title, String description, int id, TaskStatus status,
                LocalDateTime startTime, Duration duration) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String title, String description, int id) {
        this.title = title;
        this.description = description;
        this.id = id;
    }

    public Optional<LocalDateTime> getEndTime() {
        return Optional.of(startTime.plus(duration));
    }

    public Optional<Duration> getDuration() {
        return Optional.of(duration);
    }

    public Optional<LocalDateTime> getStartTime() {
            return Optional.of(startTime);
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%s,%s", id, TaskType.TASK, title, status, description,
                startTime.format(formatter), duration.toMinutes());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(title, task.title) && Objects.equals(description, task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, id, status);
    }
}
