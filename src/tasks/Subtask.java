package tasks;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, TaskStatuses status) {
        super(name, description, status);
    }

    public Subtask(String name, String description, TaskStatuses status, int id) {
        super(name, description, status, id);
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
                + ", status: " + status.name()
                + ", epicId: " + epicId + "}";
    }
}
