public class Subtask extends Task {
    private Epic epic;

    public Subtask(String name, String description, Epic epic) {
        super(name, description);
        this.epic = epic;
        this.type = TasksTypes.SUBTASK;

        epic.addSubtask(this);
    }

    public Subtask(Subtask subtask, TaskStatuses status) {
        super(subtask.name, subtask.description);

        this.epic = subtask.epic;
        this.id = subtask.id;
        this.status = status;
        this.type = TasksTypes.SUBTASK;

        epic.addSubtask(this);
    }

    public Epic getEpic() {
        return epic;
    }


    @Override
    public String toString() {
        String result = "{Subtask - id: " + id
                + ", name: " + name
                + ", description: " + description
                + ", status: " + status.name();

        if (epic != null)
            return result + ", epicId: " + epic.getId() + "}";
        else
            return result + ", epicId: null}";
    }
}
