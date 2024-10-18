package managers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatuses;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    protected Task createTask() {
        return new Task("TaskName", "TaskDescription", TaskStatuses.NEW);
    }

    protected Epic createEpic() {

        return new Epic("EpicName", "EpicDescription", TaskStatuses.NEW);
    }

    protected Subtask createSubtask() {
        return new Subtask("SubtaskName", "SubtaskDescription", TaskStatuses.NEW);
    }

    protected Task updateTask(Task task, TaskStatuses newTaskStatus, int durationInMuntes, LocalDateTime startDate) {
        return new Task(
                task.getName(),
                task.getDescription(),
                newTaskStatus,
                task.getId(),
                startDate,
                durationInMuntes
        );
    }


    protected Subtask updateSubtask(Subtask subtask, TaskStatuses newTaskStatus, int durationInMuntes, LocalDateTime startDate) {
        return new Subtask(
                subtask.getName(),
                subtask.getDescription(),
                newTaskStatus,
                subtask.getId(),
                startDate,
                durationInMuntes
        );
    }

    @Test
    void checkIdCreation() {
        Task task = createTask();
        taskManager.createTask(task);

        Assertions.assertEquals(0, task.getId());

        Epic epic = createEpic();
        taskManager.createEpic(epic);

        Assertions.assertEquals(1, epic.getId());

        Subtask subtask = createSubtask();
        taskManager.createSubtask(subtask, epic.getId());

        Assertions.assertEquals(2, subtask.getId());
    }

    @Test
    void checkTaskUpdate() {

        Task task = createTask();
        taskManager.createTask(task);
        task = updateTask(task, TaskStatuses.IN_PROGRESS, 60, LocalDateTime.now());
        taskManager.updateTask(task);

        Assertions.assertEquals(TaskStatuses.IN_PROGRESS, taskManager.getTask(task.getId()).getStatus());
    }

    @Test
    void checkSubtaskInEpicAndEpicUpdateToInProgress() {

        Subtask subtask1 = createSubtask();
        Subtask subtask2 = createSubtask();
        Epic epic = createEpic();
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1, epic.getId());

        Assertions.assertEquals(1, epic.getSubtasksId().size());

        taskManager.createSubtask(subtask2, epic.getId());

        Assertions.assertEquals(2, epic.getSubtasksId().size());

        subtask1 = updateSubtask(subtask1, TaskStatuses.IN_PROGRESS, 60, LocalDateTime.now());
        taskManager.updateSubtask(subtask1);

        Assertions.assertEquals(TaskStatuses.IN_PROGRESS, taskManager.getEpic(epic.getId()).getStatus());
        Assertions.assertEquals(2, epic.getSubtasksId().size());
    }

    @Test
    void checkSubtaskInEpicAndEpicUpdateToDone() {

        Subtask subtask1 = createSubtask();
        Subtask subtask2 = createSubtask();
        Epic epic = createEpic();
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1, epic.getId());
        taskManager.createSubtask(subtask2, epic.getId());
        subtask1 = updateSubtask(subtask1, TaskStatuses.IN_PROGRESS, 60, LocalDateTime.now());
        taskManager.updateSubtask(subtask1);

        Assertions.assertEquals(TaskStatuses.IN_PROGRESS, taskManager.getEpic(epic.getId()).getStatus());

        subtask1.endTask();
        subtask2 = updateSubtask(subtask2, TaskStatuses.IN_PROGRESS, 60
                , LocalDateTime.now().plus(Duration.ofMinutes(60)));
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);

        Assertions.assertEquals(TaskStatuses.IN_PROGRESS, taskManager.getEpic(epic.getId()).getStatus());

        subtask2.endTask();
        taskManager.updateSubtask(subtask2);

        Assertions.assertEquals(TaskStatuses.DONE, taskManager.getEpic(epic.getId()).getStatus());
    }

    @Test
    void testManagerValidateException() {
        Task task1 = createTask();
        Task task2 = createTask();
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        task1 = updateTask(task1, TaskStatuses.IN_PROGRESS, 60, LocalDateTime.now());
        task2 = updateTask(task2, TaskStatuses.IN_PROGRESS, 60, LocalDateTime.now());
        taskManager.updateTask(task1);
        Task finalTask = task2;
        assertThrows(ManagerValidateException.class, () -> {
            taskManager.updateTask(finalTask);
        });
    }
}
