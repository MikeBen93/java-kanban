package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    // методы для работы с задачами типа tasks.Task
    void createTask(Task task);

    void updateTask(Task task);

    ArrayList<Task> getTasks();

    void removeAllTasks();

    Task getTask(int id);

    void removeTask(int id);

    // методы для работы с задачами типа tasks.Epic
    void createEpic(Epic epic);

    ArrayList<Epic> getEpics();

    void removeAllEpics();

    Epic getEpic(int id);

    void removeEpic(int id);

    ArrayList<Subtask> getEpicSubtasks(int epicId);

    // методы для работы с задачами типа tasks.Subtask
    void createSubtask(Subtask subtask, int epicId);

    void updateSubtask(Subtask subtask);

    // метод для вывода задач типа tasks.Subtask
    ArrayList<Subtask> getSubtasks();

    void removeAllSubtasks();

    Subtask getSubtask(int id);

    void removeSubtask(int id);

    List<Task> getHistory();

}
