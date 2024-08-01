public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        System.out.println("Поехали!");
        // Создание Task и Epic
        taskManager.createTask(new Task("test_1", "TEST_1"));
        taskManager.createTask(new Task("test_2", "TEST_2"));
        taskManager.createEpic(new Epic("epic_1", "EPIC_1"));
        taskManager.createEpic(new Epic("epic_2", "EPIC_2"));
        System.out.println("======= Получение списка всех задач Task и Epic =======");
        // Получение списка всех задач Task и Epic
        taskManager.getTasks();
        taskManager.getEpics();
        // Создание Subtask
        taskManager.createSubtask(new Subtask("subtask_1_1",
                "SUBTASK_1_OF_EPIC_1",
                taskManager.getEpic(2)));
        taskManager.createSubtask(new Subtask("subtask_1_2",
                "SUBTASK_2_OF_EPIC_1",
                taskManager.getEpic(2)));
        taskManager.createSubtask(new Subtask("subtask_2_1",
                "SUBTASK_1_OF_EPIC_2",
                taskManager.getEpic(3)));
        taskManager.createSubtask(new Subtask("subtask_2_2",
                "SUBTASK_2_OF_EPIC_2",
                taskManager.getEpic(3)));
        // Получение списка всех задач Epic и Subtask
        System.out.println("======= Получение списка всех задач Epic и Subtask после создания Subtask =======");
        taskManager.getEpics();
        taskManager.getSubtasks();
        // Получение по идентификатору Task, Epic и Subtask
        System.out.println("======= Получение по идентификатору Task, Epic и Subtask =======");
        System.out.println(taskManager.getTask(0));
        System.out.println(taskManager.getEpic(2));
        System.out.println(taskManager.getSubtask(6));
        // Обновляем задачу Task - 0
        System.out.println("======= Обновляем задачу Task - 0 =======");
        taskManager.updateTask(new Task(taskManager.getTask(0), TaskStatuses.IN_PROGRESS));
        taskManager.getTasks();
        // Обновляем задачу Subtask - 6
        System.out.println("======= Обновляем задачу Subtask - 6 =======");
        taskManager.updateSubtask(new Subtask(taskManager.getSubtask(6), TaskStatuses.IN_PROGRESS));
        System.out.println(taskManager.getEpic(3));
        System.out.println(taskManager.getSubtask(6));
        // Обновляем задачу Subtask - 6 и 7 и получаем задачи из эпика 3
        System.out.println("======= Обновляем задачу Subtask - 6 и 7 и получаем задачи из эпика 3 =======");
        taskManager.updateSubtask(new Subtask(taskManager.getSubtask(6), TaskStatuses.DONE));
        taskManager.updateSubtask(new Subtask(taskManager.getSubtask(7), TaskStatuses.DONE));
        System.out.println(taskManager.getEpic(3));
        taskManager.getTasksFromEpic(3);
        // Удаляем задачи Subtask - 6 и 7 и получаем эпик 3
        System.out.println("======= Удаляем задачи Subtask - 6 и 7 и получаем эпик 3 =======");
        taskManager.removeSubtask(6);
        taskManager.removeSubtask(7);
        System.out.println(taskManager.getEpic(3));
        taskManager.getTasksFromEpic(3);

        // Удаляем задачи Task и Epic - 0 и 3
        System.out.println("======= Удаляем задачи Task и Epic - 0 и 3 =======");
        taskManager.removeTask(0);
        taskManager.removeEpic(3);
        taskManager.getTasks();
        taskManager.getEpics();

        // Удаление всех задач Task, Epic и Subtask
        System.out.println("======= Удаление всех задач Task, Epic и Subtask =======");
        taskManager.removeAllTasks();
        taskManager.removeAllEpics();
        taskManager.removeAllSubtasks();

        taskManager.getTasks();
        taskManager.getEpics();
        taskManager.getSubtasks();

    }
}
