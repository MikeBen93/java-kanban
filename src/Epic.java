import java.util.ArrayList;

public class Epic extends Task{
    private ArrayList<Subtask> subtasks;

    public Epic(String name, String description) {
        super(name, description);
        this.subtasks = new ArrayList<>();
        this.type = TasksTypes.EPIC;
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    protected void addSubtask(Subtask subtask) {
        removeSubtask(subtask);
        this.subtasks.add(subtask);
    }

    protected void removeSubtask(Subtask subtask) {
        this.subtasks.remove(subtask);
    }

    public ArrayList<Integer> getSubtasksId() {
        ArrayList<Integer> subtasksId = new ArrayList<>();

        for (Subtask subtask : subtasks) {
            subtasksId.add(subtask.getId());
        }

        return subtasksId;
    }

    @Override
    public String toString() {
        return  "{Epic - id: " + id
                + ", name: " + name
                + ", description: " + description
                + ", status: " + status.name()
                + ", subtasksId: " + getSubtasksId().toString() + "}";
    }
}
