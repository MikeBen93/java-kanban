package managers;

import tasks.Task;

import java.util.List;

public interface HistoryManager {

    public void add(Task task);

    List<Task> getHistory();

    void remove(int id); // метод для удаления задачи из просмотра
}
