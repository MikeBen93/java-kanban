package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import managers.*;

public class Task /*implements Comparable<Task>*/ {
    protected String name;
    protected String description;
    protected int id;
    protected TaskStatuses status;
    protected TaskTypes taskType;
    protected Duration duration;
    protected LocalDateTime startTime;

    // конструктор для первичного создания
    public Task(String name, String description, TaskStatuses status) {
        this.name = name;
        this.description = description;
        this.id = InMemoryTaskManager.getNewGlobalId();
        this.status = status;
        this.taskType = TaskTypes.TASK;
        this.duration = Duration.ZERO;
    }

    // конструктор для обновления, через создание нового объекта
    public Task(String name, String description, TaskStatuses status, int id, LocalDateTime startTime, int durationInMuntes) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.taskType = TaskTypes.TASK;
        this.startTime = startTime;
        this.duration = Duration.ofMinutes(durationInMuntes);
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public TaskTypes getType() {
        return taskType;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatuses getStatus() {
        return status;
    }

    public void setStatus(TaskStatuses status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setDuration(int durationInMuntes) {
        this.duration = Duration.ofMinutes(durationInMuntes);
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        if (startTime != null)
            return startTime.plus(duration);

        return null;
    }

    public void endTask() {
        this.status = TaskStatuses.DONE;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task otherTask = (Task) obj;

        return otherTask.id == id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "{Task - id: " + id
                + ", name: " + name
                + ", description: " + description
                + ", startTime: " + startTime
                + ", duration: " + duration.toMinutes()
                + ", status: " + status.name() + "}";
    }
}
