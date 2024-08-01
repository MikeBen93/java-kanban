import java.util.Objects;

public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected TaskStatuses status;

    // конструктор для первичного создания
    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.id = TaskManager.getNewGlobalId();
        this.status = TaskStatuses.NEW;
    }

    // конструктор для обновления, через создание нового объекта
    public Task(Task task, TaskStatuses status) {
        this.name = task.name;
        this.description = task.description;
        this.id = task.id;
        this.status = status;
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

    public int getId() {
        return id;
    }

}
