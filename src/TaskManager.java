import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> tasks;
    private static int taskGlobalId;

    public TaskManager() {
        taskGlobalId = 0;
        tasks = new HashMap<>();
    }

    // метод для работы с индентификатором задач
    public static int getNewId() {
        int currentTaskGlobalId = taskGlobalId;
        taskGlobalId++;

        return currentTaskGlobalId;
    }

    // метод для вывода все задач
    public void getTasks() {
        for (Task task: tasks.values()) {
            System.out.println(task);
        }
    }

    // метод для добавления новых задач
    public void createTask(Task task) {
        tasks.put(task.getId(), task);
    }

    // метод для обновления существующих задач
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);

        if (task.type == TasksTypes.SUBTASK) {
            Subtask subtask = (Subtask) task;
            checkEpicForStatus(subtask.getEpic());
        }
    }

    // методя для удаления всех задач
    public void removeAllTasks() {
        tasks.clear();
        taskGlobalId = 0;
    }

    // методя для получения объекта задачи по id
    public Task getTask(int id) {
        return tasks.get(id);
    }

    // метод удаления задачи по id
    public void removeTask(int id) {
        Task taskToRemove = getTask(id);

        tasks.remove(id);
        if (taskToRemove.type == TasksTypes.SUBTASK) {
            Subtask subtaskToRemove = (Subtask) taskToRemove;
            subtaskToRemove.getEpic().removeSubtask(subtaskToRemove);
        }
    }

    // метод для получения подзадач из эпика
    public void getTasksFromEpic(int id) {
        Epic epic = (Epic) tasks.get(id);

        for (Integer tasksId: epic.getSubtasksId()) {
            System.out.println(tasks.get(tasksId));
        }
    }

    // методя для обновления статуса эпика исходя из статуса подзадач
    private void checkEpicForStatus(Epic epic) {
        boolean allSubtaksNew = true;
        boolean allSubtaksDone = true;

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
