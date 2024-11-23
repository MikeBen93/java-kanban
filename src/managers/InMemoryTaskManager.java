package managers;

import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subtasks;
    private static int taskGlobalId;
    private HistoryManager historyManager;
    private final Comparator<Task> taskComparator = this::compare;
    protected Set<Task> prioritizedTasks = new TreeSet<Task>(taskComparator);

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
        addNewPrioritizedTask(task);
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateTask(Task task) {
        addNewPrioritizedTask(task);
        tasks.put(task.getId(), task);
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void removeAllTasks() {
        getPrioritizedTasks().stream()
                .filter(task -> task.getType().equals(TaskTypes.TASK))
                .map(task -> prioritizedTasks.remove(task));
        tasks.clear();
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);

        if (task == null)
            throw new NotFoundException(LocalDateTime.now() + " Задача с id=" + id + " не найдена");

        historyManager.add(task);
        return task;
    }

    @Override
    public void removeTask(int id) {
        prioritizedTasks.removeIf(task -> task.getId() == id);
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
        Epic epic = epics.get(id);

        if (epic == null)
            throw new NotFoundException(LocalDateTime.now() + " Эпик с id=" + id + " не найдена");

        historyManager.add(epic);
        return epic;
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
        addNewPrioritizedTask(subtask);
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
        addNewPrioritizedTask(subtask);
        subtasks.put(subtask.getId(), subtask);
        checkEpicForStatus(epics.get(subtask.getEpicId()));
        checkEpicTime(epics.get(subtask.getEpicId()));
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
        Subtask subtask = subtasks.get(id);

        if (subtask == null)
            throw new NotFoundException(LocalDateTime.now() + " Сабтаска с id=" + id + " не найдена");

        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public void removeSubtask(int id) {
        Subtask subtaskToRemove = subtasks.get(id);
        Epic subtaskEpic = epics.get(subtaskToRemove.getEpicId());

        subtaskEpic.removeSubtask(id);
        checkEpicForStatus(subtaskEpic);
        checkEpicTime(subtaskEpic);
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

    private void checkEpicTime(Epic epic) {
        Duration duraionsSum = Duration.ZERO;
        List<Integer> epicSubtasks = epic.getSubtasksId();
        LocalDateTime earliestStartTime = subtasks.get(epicSubtasks.getFirst()).getStartTime();

        for (Integer id : epicSubtasks) {
            LocalDateTime startTime = subtasks.get(id).getStartTime();

            if (startTime != null && earliestStartTime != null && startTime.isBefore(earliestStartTime))
                earliestStartTime = startTime;

            duraionsSum = duraionsSum.plus(subtasks.get(id).getDuration());
        }

        epic.setStartTime(earliestStartTime);
        epic.setDuration(duraionsSum);
        if (earliestStartTime == null)
            epic.setEndTime(null);
        else
            epic.setEndTime(earliestStartTime.plus(duraionsSum));
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private Task hasIntersectionWith(Task task) {
        List<Task> tasks = List.copyOf(prioritizedTasks);

        for (Task checkTask : tasks) {
            if (task.equals(checkTask))
                continue;
            else if ((task.getStartTime() == null && task.getEndTime() == null) ||
            (checkTask.getStartTime() == null && checkTask.getEndTime() == null))
                continue;
            else if ((task.getStartTime().isAfter(checkTask.getStartTime())
                    && task.getStartTime().isBefore(checkTask.getEndTime()))
                || (task.getEndTime().isAfter(checkTask.getStartTime())
                    && task.getEndTime().isBefore(checkTask.getEndTime()))
                    || (task.getStartTime().isEqual(checkTask.getStartTime())
                    && task.getEndTime().isEqual(checkTask.getEndTime()))
            ) {
                return checkTask;
            }
        }

        return null;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().toList();
    }

    private void addNewPrioritizedTask(Task task) {
        Task crossedTask = hasIntersectionWith(task);

        if (crossedTask != null)
            throw new ManagerValidateException(
                    "Задачи #" + task + " и #" + crossedTask + "пересекаются");
        if (prioritizedTasks.contains(task))
            prioritizedTasks.remove(task);

        prioritizedTasks.add(task);
    }

    private int compare(Task firstTask, Task secondTask) {
        if (firstTask.getId() == secondTask.getId()) {
            if (firstTask.getStartTime() == null || secondTask.getStartTime() == null)
                return 0;
        }

        return firstTask.getId() - secondTask.getId();
    }
}
