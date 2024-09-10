package tasks;

import managers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    HistoryManager historyManager;
    TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault(historyManager);
    }

    @Test
    void addNewSubtask() {
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description", TaskStatuses.NEW);
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description", TaskStatuses.NEW);
        taskManager.createEpic(epic);
        final int epicId = epic.getId();
        final Epic savedEpic = taskManager.getEpic(epicId);

        taskManager.createSubtask(subtask, epicId);
        final int subtaskId = subtask.getId();
        final Subtask savedSubtask = taskManager.getSubtask(subtaskId);

        assertNotNull(savedSubtask, "сабтаск не найден.");
        assertEquals(subtask, savedSubtask, "сабтаски не совпадают.");

        final ArrayList<Subtask> subtasks = taskManager.getSubtasks();

        assertNotNull(subtasks, "сабтаски не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество сабтасков.");
        assertEquals(subtask, subtasks.get(0), "сабтаски не совпадают.");
    }

    @Test
    void cantAddToSubtaskEpicAsItself() {
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description", TaskStatuses.NEW);
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description", TaskStatuses.NEW);

        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask, subtask.getId());

        assertEquals(0, epic.getSubtasksId().size(), "Неверное количество сабтасков.");

        taskManager.createSubtask(subtask, epic.getId());

        assertEquals(1, epic.getSubtasksId().size(), "Неверное количество сабтасков.");
    }

}