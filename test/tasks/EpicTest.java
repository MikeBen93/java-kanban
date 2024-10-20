package tasks;

import managers.HistoryManager;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    void addNewEpic() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description", TaskStatuses.NEW);
        taskManager.createEpic(epic);

        final int epicId = epic.getId();
        final Epic savedEpic = taskManager.getEpic(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final ArrayList<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void cantAddEpicAsSubtaskToItself() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description", TaskStatuses.NEW);
        taskManager.createEpic(epic);
        int epicId = epic.getId();

        epic.addSubtask(epicId);

        assertEquals(0, epic.getSubtasksId().size(), "Неверное количество сабтасков.");
    }

    @Test
    void epicWithAllSubtasksInNewStatus() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description", TaskStatuses.NEW);
        Subtask firstSubtask =  new Subtask("subtask_1_1", "SUBTASK_1_OF_EPIC_1", TaskStatuses.NEW);
        Subtask secondSubtask =  new Subtask("subtask_1_2", "SUBTASK_1_OF_EPIC_1", TaskStatuses.NEW);
        taskManager.createEpic(epic);
        taskManager.createSubtask(firstSubtask,0);
        taskManager.createSubtask(secondSubtask,0);

        assertEquals(epic.getSubtasksId().size(), 2);
        assertEquals(epic.getStatus(), TaskStatuses.NEW);
    }

    @Test
    void epicWithAllSubtasksInDoneStatus() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description", TaskStatuses.NEW);
        Subtask firstSubtask =  new Subtask("subtask_1_1", "SUBTASK_1_OF_EPIC_1", TaskStatuses.NEW);
        Subtask secondSubtask =  new Subtask("subtask_1_2", "SUBTASK_1_OF_EPIC_1", TaskStatuses.NEW);

        taskManager.createEpic(epic);
        taskManager.createSubtask(firstSubtask,0);
        taskManager.createSubtask(secondSubtask,0);

        firstSubtask = new Subtask(
                "subtask_1_1",
                "SUBTASK_1_OF_EPIC_1",
                TaskStatuses.DONE,
                1,
                LocalDateTime.of(2024, 10, 4, 13, 0),
                60);
        secondSubtask = new Subtask(
                "subtask_1_2",
                "SUBTASK_2_OF_EPIC_1",
                TaskStatuses.DONE,
                2,
                LocalDateTime.of(2024, 10, 4, 14, 0),
                60);

        taskManager.updateSubtask(firstSubtask);
        taskManager.updateSubtask(secondSubtask);

        assertEquals(epic.getSubtasksId().size(), 2);
        assertEquals(epic.getStatus(), TaskStatuses.DONE);
    }

    @Test
    void epicWithAllSubtasksInNewAndDoneStatus() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description", TaskStatuses.NEW);
        Subtask firstSubtask =  new Subtask("subtask_1_1", "SUBTASK_1_OF_EPIC_1", TaskStatuses.NEW);
        Subtask secondSubtask =  new Subtask("subtask_1_2", "SUBTASK_1_OF_EPIC_1", TaskStatuses.NEW);

        taskManager.createEpic(epic);
        taskManager.createSubtask(firstSubtask,0);
        taskManager.createSubtask(secondSubtask,0);

        firstSubtask = new Subtask(
                "subtask_1_1",
                "SUBTASK_1_OF_EPIC_1",
                TaskStatuses.DONE,
                1,
                LocalDateTime.of(2024, 10, 4, 13, 0),
                60);

        taskManager.updateSubtask(firstSubtask);

        assertEquals(epic.getSubtasksId().size(), 2);
        assertEquals(epic.getStatus(), TaskStatuses.IN_PROGRESS);
    }

    @Test
    void epicWithAllSubtasksInInProgressStatus() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description", TaskStatuses.NEW);
        Subtask firstSubtask =  new Subtask("subtask_1_1", "SUBTASK_1_OF_EPIC_1", TaskStatuses.NEW);
        Subtask secondSubtask =  new Subtask("subtask_1_2", "SUBTASK_1_OF_EPIC_1", TaskStatuses.NEW);

        taskManager.createEpic(epic);
        taskManager.createSubtask(firstSubtask,0);
        taskManager.createSubtask(secondSubtask,0);

        firstSubtask = new Subtask(
                "subtask_1_1",
                "SUBTASK_1_OF_EPIC_1",
                TaskStatuses.IN_PROGRESS,
                1,
                LocalDateTime.of(2024, 10, 4, 13, 0),
                60);
        secondSubtask = new Subtask(
                "subtask_1_2",
                "SUBTASK_2_OF_EPIC_1",
                TaskStatuses.IN_PROGRESS,
                2,
                LocalDateTime.of(2024, 10, 4, 14, 0),
                60);

        taskManager.updateSubtask(firstSubtask);
        taskManager.updateSubtask(secondSubtask);

        assertEquals(epic.getSubtasksId().size(), 2);
        assertEquals(epic.getStatus(), TaskStatuses.IN_PROGRESS);
    }
}