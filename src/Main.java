public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        System.out.println("Поехали!");
        // доблавяем задачи
        taskManager.createTask(new Task("test_1", "TEST_TEST_TEST_1"));
        taskManager.createTask(new Task("test_2", "TEST_TEST_TEST_2"));
        taskManager.createTask(new Epic("epic_1", "EPIC_EPIC_EPIC_1"));
        System.out.println("--------- getAllTasks");
        // выводим задачи
        taskManager.getTasks();
        // доблавяем и выводим подзадачи
        taskManager.createTask(new Subtask("subtask_1",
                "EPIC_EPIC_EPIC_1",
                (Epic) taskManager.getTask(2)));
        taskManager.createTask(new Subtask("subtask_2",
                "EPIC_EPIC_EPIC_1",
                (Epic) taskManager.getTask(2)));
        System.out.println("--------- getAllTasks");
        taskManager.getTasks();
        // удаляем задачу по id
        System.out.println("--------- removeTask 1");
        taskManager.removeTask(1);
        taskManager.getTasks();
        // достаём задачу по id
        System.out.println("--------- getTask 0");
        System.out.println(taskManager.getTask(0));
        // обновляем задачу 0
        System.out.println("--------- updateTask 0");
        taskManager.updateTask(new Task(taskManager.getTask(0), TaskStatuses.IN_PROGRESS));
        taskManager.getTasks();
        // получаем подзадачи из эпика 2
        System.out.println("--------- getTasksFromEpic 3");
        taskManager.getTasksFromEpic(2);
        // обновляем подзадачу 3
        System.out.println("--------- updateTask 3");
        taskManager.updateTask(new Subtask((Subtask) taskManager.getTask(3), TaskStatuses.IN_PROGRESS));
        taskManager.getTasks();
        // обновляем подзадачу 3 и 4
        System.out.println("--------- updateTask 3 and 4");
        taskManager.updateTask(new Subtask((Subtask) taskManager.getTask(3), TaskStatuses.DONE));
        taskManager.updateTask(new Subtask((Subtask) taskManager.getTask(4), TaskStatuses.DONE));
        taskManager.getTasks();
        // удаляем подзадачу 3
        System.out.println("--------- removeTask 3");
        taskManager.removeTask(3);
        taskManager.getTasks();
        // удаляем все задачи
        System.out.println("--------- removeAllTasks");
        taskManager.removeAllTasks();
        taskManager.getTasks();
    }
}
