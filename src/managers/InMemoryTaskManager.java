package managers;

import tasks.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subtasks;
    private static int taskGlobalId;
    private HistoryManager historyManager;

    public InMemoryTaskManager() {
        taskGlobalId = 0;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
    }

    // метод для работы с индентификатором задач
    public static int getNewGlobalId() {
        int currentTaskGlobalId = InMemoryTaskManager.taskGlobalId;
        InMemoryTaskManager.taskGlobalId++;

        return currentTaskGlobalId;
    }

    // методы для работы с задачами типа tasks.Task
    @Override
    public void createTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public Task getTask(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public void removeTask(int id) {
        tasks.remove(id);
    }

    // методы для работы с задачами типа tasks.Epic
    @Override
    public void createEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void removeAllEpics() {
        for (Epic epic: epics.values()) {
            for (Integer subtaskId : epic.getSubtasksId()) {
                subtasks.remove(subtaskId);
            }
        }

        epics.clear();
    }

    @Override
    public Epic getEpic(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public void removeEpic(int id) {
        for (Integer subtaskId : epics.get(id).getSubtasksId()) {
            subtasks.remove(subtaskId);
        }

        epics.remove(id);
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        ArrayList<Subtask> subtasksList = new ArrayList<>();
        for (Integer subtaskId : epics.get(epicId).getSubtasksId())
            subtasksList.add(subtasks.get(subtaskId));

        return subtasksList;
    }

    // методы для работы с задачами типа tasks.Subtask
    @Override
    public void createSubtask(Subtask subtask, int epicId) {
        Epic epic = epics.get(epicId);
        int subtaskId = subtask.getId();
        if (epicId == subtaskId) {
            return;
        }

        subtask.setEpicId(epic.getId());
        epic.addSubtask(subtask.getId());
        subtasks.put(subtask.getId(), subtask);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtask.setEpicId(subtasks.get(subtask.getId()).getEpicId());
        subtasks.put(subtask.getId(), subtask);
        checkEpicForStatus(epics.get(subtask.getEpicId()));
    }

    // метод для вывода задач типа tasks.Subtask
    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void removeAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.removeAllSubtasks();
            epic.setStatus(TaskStatuses.NEW);
        }

        subtasks.clear();
    }

    @Override
    public Subtask getSubtask(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public void removeSubtask(int id) {
        Subtask subtaskToRemove = subtasks.get(id);
        Epic subtaskEpic = epics.get(subtaskToRemove.getEpicId());

        subtaskEpic.removeSubtask(id);
        checkEpicForStatus(subtaskEpic);
        subtasks.remove(id);
    }

    // методя для обновления статуса эпика исходя из статуса подзадач
    private void checkEpicForStatus(Epic epic) {
        boolean allSubtaksNew = true;
        boolean allSubtaksDone = true;

        if (epic.getSubtasksId().isEmpty()) {
            epic.setStatus(TaskStatuses.NEW);
            return;
        }

        for (Integer subtaskId : epic.getSubtasksId()) {
            if (subtasks.get(subtaskId).getStatus() == TaskStatuses.DONE)
                allSubtaksNew = false;
            else if (subtasks.get(subtaskId).getStatus() == TaskStatuses.NEW)
                allSubtaksDone = false;
            else {
                allSubtaksDone = false;
                allSubtaksNew = false;
            }
        }

        if (allSubtaksNew)
            epic.setStatus(TaskStatuses.NEW);
        else if (allSubtaksDone)
            epic.setStatus(TaskStatuses.DONE);
        else
            epic.setStatus(TaskStatuses.IN_PROGRESS);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
