package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasksId;
    private LocalDateTime endTime;

    public Epic(String name, String description, TaskStatuses status) {
        super(name, description, status);
        this.subtasksId = new ArrayList<>();
        this.taskType = TaskTypes.EPIC;
    }

    public Epic(String name, String description, TaskStatuses status, int id, LocalDateTime startTime, int durationInMuntes) {
        super(name, description, status);
        this.subtasksId = new ArrayList<>();
        this.taskType = TaskTypes.EPIC;
        this.id = id;
    }

    public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }

    public void addSubtask(Integer subtaskId) {
        if (id == subtaskId) {
            return;
        }
        removeSubtask(subtaskId);
        this.subtasksId.add(subtaskId);
    }

    public void removeSubtask(Integer subtaskId) {
        this.subtasksId.remove(subtaskId);
    }

    public void removeAllSubtasks() {
        this.subtasksId.clear();
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }


    @Override
    public String toString() {
        return  "{Epic - id: " + id
                + ", name: " + name
                + ", description: " + description
                + ", startTime: " + startTime
                + ", duration: " + duration.toMinutes()
                + ", status: " + status.name()
                + ", subtasksId: " + subtasksId.toString() + "}";
    }
}
