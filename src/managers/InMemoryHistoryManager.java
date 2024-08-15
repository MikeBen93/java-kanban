package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{

    private ArrayList<Task> historyTasks;
    private final int HISTORY_LENGTH = 10;

    InMemoryHistoryManager() {
        historyTasks = new ArrayList<>();
    }

    public void add(Task task) {
        if (historyTasks.size() == HISTORY_LENGTH)
            historyTasks.remove(0);

        historyTasks.add(task);
    }

    public List<Task> getHistory() {
        return historyTasks;
    }
}
