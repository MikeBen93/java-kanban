package managers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatuses;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    protected InMemoryTaskManager taskManager;
    protected InMemoryHistoryManager historyManager;

    @BeforeEach
    void beforeEach() {
        taskManager = new InMemoryTaskManager();
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void checkHistoryIsEmpty() {
        Assertions.assertEquals(0, taskManager.getHistory().size());
    }

    @Test
    void checkHistoryCreation() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", TaskStatuses.NEW);
        taskManager.createTask(task);

        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description", TaskStatuses.NEW);
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description", TaskStatuses.NEW);
        taskManager.createSubtask(subtask, epic.getId());

        Assertions.assertEquals(0, taskManager.getHistory().size());
        Assertions.assertNotNull(taskManager.getTask(task.getId()));
        Assertions.assertNotNull(taskManager.getEpic(epic.getId()));
        Assertions.assertNotNull(taskManager.getSubtask(subtask.getId()));
        Assertions.assertEquals(3, taskManager.getHistory().size());
    }

    @Test
    void checkHistoryCreationAndRemoveOfDuplicatesFromIt() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", TaskStatuses.NEW);
        taskManager.createTask(task);
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description", TaskStatuses.NEW);
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description", TaskStatuses.NEW);
        taskManager.createSubtask(subtask, epic.getId());

        Assertions.assertEquals(0, taskManager.getHistory().size());
        Assertions.assertNotNull(taskManager.getTask(task.getId()));
        Assertions.assertNotNull(taskManager.getEpic(epic.getId()));
        Assertions.assertNotNull(taskManager.getSubtask(subtask.getId()));
        Assertions.assertEquals(3, taskManager.getHistory().size());
        Assertions.assertNotNull(taskManager.getTask(task.getId()));
        Assertions.assertEquals(3, taskManager.getHistory().size());
        Assertions.assertNotNull(taskManager.getEpic(epic.getId()));
        Assertions.assertEquals(3, taskManager.getHistory().size());
        Assertions.assertNotNull(taskManager.getSubtask(subtask.getId()));
        Assertions.assertEquals(3, taskManager.getHistory().size());
    }

    @Test
    void checkRemoveFromHistoryBegining() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", TaskStatuses.NEW);
        taskManager.createTask(task);
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description", TaskStatuses.NEW);
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description", TaskStatuses.NEW);
        taskManager.createSubtask(subtask, epic.getId());

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);

        Assertions.assertEquals(List.of(task, epic, subtask), historyManager.getHistory());

        historyManager.remove(task.getId());

        Assertions.assertEquals(List.of(epic, subtask), historyManager.getHistory());
    }

    @Test
    void checkRemoveFromHistoryMiddle() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", TaskStatuses.NEW);
        taskManager.createTask(task);
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description", TaskStatuses.NEW);
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description", TaskStatuses.NEW);
        taskManager.createSubtask(subtask, epic.getId());

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);

        Assertions.assertEquals(List.of(task, epic, subtask), historyManager.getHistory());

        historyManager.remove(epic.getId());

        Assertions.assertEquals(List.of(task, subtask), historyManager.getHistory());
    }

    @Test
    void checkRemoveFromHistoryEnd() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", TaskStatuses.NEW);
        taskManager.createTask(task);
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description", TaskStatuses.NEW);
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description", TaskStatuses.NEW);
        taskManager.createSubtask(subtask, epic.getId());

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);

        Assertions.assertEquals(List.of(task, epic, subtask), historyManager.getHistory());

        historyManager.remove(subtask.getId());

        Assertions.assertEquals(List.of(task, epic), historyManager.getHistory());
    }
}