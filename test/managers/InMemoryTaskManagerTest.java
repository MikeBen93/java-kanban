package managers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatuses;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    HistoryManager historyManager;
    TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault(historyManager);
    }

    @Test
    void checkIdCreation() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", TaskStatuses.NEW);
        taskManager.createTask(task);

        Assertions.assertEquals(0, task.getId());

        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description", TaskStatuses.NEW);
        taskManager.createEpic(epic);

        Assertions.assertEquals(1, epic.getId());

        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description", TaskStatuses.NEW);
        taskManager.createSubtask(subtask, epic.getId());

        Assertions.assertEquals(2, subtask.getId());
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
}