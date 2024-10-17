package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, TaskStatuses status) {
        super(name, description, status);
        this.taskType = TaskTypes.SUBTASK;
    }

    public Subtask(String name, String description, TaskStatuses status, int id, LocalDateTime startTime, int durationInMuntes) {
        super(name, description, status, id, startTime, durationInMuntes);
        this.taskType = TaskTypes.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        if (epicId == id) {
            return;
        }
        this.epicId = epicId;
    }



    @Override
    public String toString() {
        return  "{Subtask - id: " + id
                + ", name: " + name
                + ", description: " + description
                + ", startTime: " + startTime
                + ", duration: " + duration.toMinutes()
                + ", status: " + status.name()
                + ", epicId: " + epicId + "}";
    }
}
