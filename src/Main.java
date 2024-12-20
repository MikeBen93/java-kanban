import api.HttpTaskServer;
import managers.*;
import tasks.*;

import java.io.File;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) throws ManagerSaveException {
        File file = new File("tasksFile.csv");
        TaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

        addTasks(fileBackedTaskManager);
        updateTasks(fileBackedTaskManager);
        printAllTasks(fileBackedTaskManager);

        HttpTaskServer taskServer = new HttpTaskServer(fileBackedTaskManager);

        try {
            taskServer.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    private static void printAllTasks(TaskManager manager) {
        System.out.println("Все задачи:");
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getEpics()) {
            System.out.println(epic);

            for (Task task : manager.getEpicSubtasks(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("==============");
        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
        System.out.println("==============");
    }

    private static void addTasks(TaskManager manager) {
        manager.createTask(new Task("test_1", "TEST_1", TaskStatuses.NEW));
        manager.createTask(new Task("test_2", "TEST_2", TaskStatuses.NEW));
        manager.createEpic(new Epic("epic_1", "EPIC_1", TaskStatuses.NEW));
        manager.createEpic(new Epic("epic_2", "EPIC_2", TaskStatuses.NEW));
        manager.createSubtask(
                new Subtask("subtask_1_1", "SUBTASK_1_OF_EPIC_1", TaskStatuses.NEW),
                2);
        manager.createSubtask(
                new Subtask("subtask_1_2", "SUBTASK_2_OF_EPIC_1", TaskStatuses.NEW),
                2);
        manager.createSubtask(
                new Subtask("subtask_1_3", "SUBTASK_3_OF_EPIC_1", TaskStatuses.NEW),
                2);
        manager.createSubtask(
                new Subtask("subtask_2_1", "SUBTASK_1_OF_EPIC_2", TaskStatuses.NEW),
                3);
        manager.createSubtask(
                new Subtask("subtask_2_2", "SUBTASK_2_OF_EPIC_2", TaskStatuses.NEW),
                3);
        manager.createEpic(new Epic("epic_3", "EPIC_3", TaskStatuses.NEW));
    }

    private static void updateTasks(TaskManager manager) {
        manager.updateTask(new Task(
                "test_1",
                "TEST_1",
                TaskStatuses.IN_PROGRESS,
                0,
                LocalDateTime.of(2024, 10, 4, 12, 0),
                60));
        manager.updateSubtask(new Subtask(
                "subtask_1_1",
                "SUBTASK_1_OF_EPIC_1",
                TaskStatuses.IN_PROGRESS,
                4,
                LocalDateTime.of(2024, 10, 4, 13, 0),
                60));
        manager.updateSubtask(new Subtask(
                "subtask_2_1",
                "SUBTASK_1_OF_EPIC_2",
                TaskStatuses.DONE,
                6,
                LocalDateTime.of(2024, 10, 4, 14, 0),
                60));
        manager.updateSubtask(new Subtask(
                "subtask_2_2",
                "SUBTASK_2_OF_EPIC_2",
                TaskStatuses.DONE,
                7,
                LocalDateTime.of(2024, 10, 4, 15, 0),
                60));
    }

    private static void removeTasks(TaskManager manager) {
        manager.removeTask(0);
        manager.removeSubtask(4);
        manager.removeEpic(3);
    }

    private static void removeAllTasks(TaskManager manager) {
        manager.removeAllTasks();
        manager.removeAllSubtasks();
        manager.removeAllEpics();
    }

    private static void printTasksById(TaskManager manager) {
        System.out.println("Отбор задач по id:");
        System.out.println("Задачи:");
        System.out.println(manager.getTask(0));
        System.out.println("Эпики:");
        System.out.println(manager.getEpic(2));
        System.out.println("Подзадачи:");
        System.out.println(manager.getSubtask(6));

        System.out.println("==============");
        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
        System.out.println("==============");
    }

    private static void printTasksByIdSecond(TaskManager manager) {
        System.out.println("Отбор задач по id второй варинт:");
        System.out.println("Задачи:");
        System.out.println(manager.getTask(0));
        System.out.println("Задачи:");
        System.out.println(manager.getTask(1));
        System.out.println("Задачи:");
        System.out.println(manager.getTask(0));
        System.out.println("Подзадачи:");
        System.out.println(manager.getSubtask(6));
        System.out.println("Эпики:");
        System.out.println(manager.getEpic(2));

        System.out.println("==============");
        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
        System.out.println("==============");
    }
}

