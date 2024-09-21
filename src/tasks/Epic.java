package tasks;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasksId;

    public Epic(String name, String description, TaskStatuses status) {
        super(name, description, status);
        this.subtasksId = new ArrayList<>();
        this.taskType = TaskTypes.EPIC;
    }
    //конструктор для загрузки из файла
    public Epic(String name, String description, TaskStatuses status, int id) {
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
    public String toString() {
        return  "{Epic - id: " + id
                + ", name: " + name
                + ", description: " + description
                + ", status: " + status.name()
                + ", subtasksId: " + subtasksId.toString() + "}";
    }
}
