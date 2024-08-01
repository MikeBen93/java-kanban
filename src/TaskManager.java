import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subtasks;
    private static int taskGlobalId;

    public TaskManager() {
        taskGlobalId = 0;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    // метод для работы с индентификатором задач
    public static int getNewGlobalId() {
        int currentTaskGlobalId = taskGlobalId;
        taskGlobalId++;

        return currentTaskGlobalId;
    }

    // методы для работы с задачами типа Task
    public void createTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void getTasks() {
        if (tasks.isEmpty()) {
            System.out.println("Задач типа Task нет");
            return;
        }

        for (Task task: tasks.values()) {
            System.out.println(task);
        }
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public void removeTask(int id) {
        tasks.remove(id);
    }

    // методы для работы с задачами типа Epic
    public void createEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void getEpics() {
        if (epics.isEmpty()) {
            System.out.println("Задач типа Epic нет");
            return;
        }

        for (Epic epic: epics.values()) {
            System.out.println(epic);
        }
    }

    public void removeAllEpics() {
        for (Epic epic: epics.values()) {
            for (Subtask subtask : epic.getSubtasks()) {
                subtasks.remove(subtask.getId());
            }
        }

        epics.clear();
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public void removeEpic(int id) {
        for (Integer subtaskId : epics.get(id).getSubtasksId()) {
            subtasks.remove(subtaskId);
        }

        epics.remove(id);
    }

    public void getTasksFromEpic(int epicId) {
        if (epics.get(epicId).getSubtasks().isEmpty()) {
            System.out.println("Подзадач нет");
            return;
        }

        for (Subtask subtask: epics.get(epicId).getSubtasks()) {
            System.out.println(subtask);
        }
    }

    // методы для работы с задачами типа Subtask
    public void createSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);

        checkEpicForStatus(subtask.getEpic());
    }

    // метод для вывода задач типа Subtask
    public void getSubtasks() {
        if (subtasks.isEmpty()) {
            System.out.println("Задач типа Subtask нет");
            return;
        }

        for (Subtask subtask: subtasks.values()) {
            System.out.println(subtask);
        }
    }

    public void removeAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.removeAllSubtasks();
            epic.status = TaskStatuses.NEW;
        }

        subtasks.clear();
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    public void removeSubtask(int id) {
        Subtask subtaskToRemove = subtasks.get(id);
        Epic subtaskEpic = subtaskToRemove.getEpic();

        subtaskEpic.removeSubtask(subtaskToRemove);
        checkEpicForStatus(subtaskEpic);
        subtasks.remove(id);
    }

    // методя для обновления статуса эпика исходя из статуса подзадач
    private void checkEpicForStatus(Epic epic) {
        boolean allSubtaksNew = true;
        boolean allSubtaksDone = true;

        if (epic.getSubtasks().isEmpty()) {
            epic.status = TaskStatuses.NEW;
            return;
        }

        for (Subtask subtask : epic.getSubtasks()) {
            if (subtask.status == TaskStatuses.DONE)
                allSubtaksNew = false;
            else if (subtask.status == TaskStatuses.NEW)
                allSubtaksDone = false;
            else {
                allSubtaksDone = false;
                allSubtaksNew = false;
            }
        }

        if (allSubtaksNew)
            epic.status = TaskStatuses.NEW;
        else if (allSubtaksDone)
            epic.status = TaskStatuses.DONE;
        else
            epic.status = TaskStatuses.IN_PROGRESS;
    }
}
