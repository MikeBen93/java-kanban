package tasks;

import managers.HistoryManager;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    HistoryManager historyManager;
    TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault(historyManager);
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
}