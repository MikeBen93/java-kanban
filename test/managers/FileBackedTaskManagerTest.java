package managers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatuses;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class FileBackedTaskManagerTest {
    TaskManager taskManager;
    TaskManager taskManager2;
    File testFile;

    @BeforeEach
    void beforeEach() throws IOException {
        testFile = File.createTempFile("testFile", "csv");
        taskManager = new FileBackedTaskManager(testFile);
    }

    @Test
    void checkSaveOfTasksToFile() throws IOException {
        String testTaskName = "test_task_1";
        String testEpicName = "test_epic_1";
        String testSubtaskName = "test_subtask_1_1";
        ArrayList<String> lines = new ArrayList<>();
        taskManager.createTask(new Task(testTaskName, "TEST_1", TaskStatuses.NEW));
        taskManager.createEpic(new Epic(testEpicName, "EPIC_1", TaskStatuses.NEW));
        taskManager.createSubtask(
                new Subtask(testSubtaskName, "SUBTASK_1_OF_EPIC_1", TaskStatuses.NEW),
                1);

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(testFile, StandardCharsets.UTF_8))) {
            while (bufferedReader.ready()) {
                lines.add(bufferedReader.readLine());
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось считать данные из файла.");
        }

        Assertions.assertEquals(testTaskName, lines.get(1).split(",")[2]);
        Assertions.assertEquals(testEpicName, lines.get(2).split(",")[2]);
        Assertions.assertEquals(testSubtaskName, lines.get(3).split(",")[2]);
    }

    @Test
    void checkLoadOfTasksFromFile() throws IOException {
        String testTaskName = "test_task_1";
        String testEpicName = "test_epic_1";
        String testSubtaskName = "test_subtask_1_1";
        ArrayList<String> lines = new ArrayList<>();
        taskManager.createTask(new Task(testTaskName, "TEST_1", TaskStatuses.NEW));
        taskManager.createEpic(new Epic(testEpicName, "EPIC_1", TaskStatuses.NEW));
        taskManager.createSubtask(
                new Subtask(testSubtaskName, "SUBTASK_1_OF_EPIC_1", TaskStatuses.NEW),
                1);

        taskManager2 = FileBackedTaskManager.loadFromFile(testFile);

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(testFile, StandardCharsets.UTF_8))) {
            while (bufferedReader.ready()) {
                lines.add(bufferedReader.readLine());
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось считать данные из файла.");
        }

        Assertions.assertEquals(taskManager2.getTasks().get(0), taskManager.getTasks().get(0));
        Assertions.assertEquals(taskManager2.getEpics().get(0), taskManager.getEpics().get(0));
        Assertions.assertEquals(taskManager2.getSubtasks().get(0), taskManager.getSubtasks().get(0));
    }

    @Test
    void checkReadException() throws IOException {
        Files.delete(testFile.toPath());

        assertThrows(ManagerSaveException.class, () -> {
            FileBackedTaskManager.loadFromFile(testFile);
        });
    }
}
