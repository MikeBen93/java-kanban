package tasks;

import java.util.Objects;
import managers.*;

public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected TaskStatuses status;
    protected TaskTypes taskType;

    // конструктор для первичного создания
    public Task(String name, String description, TaskStatuses status) {
        this.name = name;
        this.description = description;
        this.id = InMemoryTaskManager.getNewGlobalId();
        this.status = status;
        this.taskType = TaskTypes.TASK;
    }

    // конструктор для обновления, через создание нового объекта
    public Task(String name, String description, TaskStatuses status, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
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
                + ", status: " + status.name() + "}";
    }
}
