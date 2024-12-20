package managers;

import tasks.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private static final String HEADER_CSV_FILE = "id,type,name,status,description,startTime,duration,epicId";
    File tasksFile;

    public FileBackedTaskManager(File tasksFile) {
        super();
        this.tasksFile = tasksFile;
    }

    public static FileBackedTaskManager loadFromFile(File existingTasksFile) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(existingTasksFile);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(existingTasksFile, StandardCharsets.UTF_8))) {

            String line;
            while (bufferedReader.ready()) {
                line = bufferedReader.readLine();
                if (line.equals("")) {
                    break;
                } else if (line.equals(HEADER_CSV_FILE)) {
                    continue;
                }

                Task task = taskManager.fromString(line);

                if (task instanceof Epic epic) {
                    taskManager.addEpic(epic);
                } else if (task instanceof Subtask subtask) {
                    taskManager.addSubtask(subtask);
                } else {
                    taskManager.addTask(task);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось считать данные из файла.");
        }

        return taskManager;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask, int epicId) {
        super.createSubtask(subtask, epicId);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    private void save() {
        try {
            if (Files.exists(tasksFile.toPath())) {
                Files.delete(tasksFile.toPath());
            }
            Files.createFile(tasksFile.toPath());
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось найти файл для записи данных");
        }

        try (FileWriter fileWriter = new FileWriter(tasksFile, StandardCharsets.UTF_8)) {
            fileWriter.write(HEADER_CSV_FILE + "\n");

            for (Task t : super.getTasks()) {
                fileWriter.write(toStringForFile(t) + "\n");
            }
            for (Epic e : super.getEpics()) {
                fileWriter.write(toStringForFile(e) + "\n");
            }
            for (Subtask s : super.getSubtasks()) {
                fileWriter.write(toStringForFile(s) + "\n");
            }

            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Не удалось записать в файл задачи");
        }
    }

    private String toStringForFile(Task task) {
        String epicId = "";
        if (task.getType().equals(TaskTypes.SUBTASK)) {
            Subtask subtask = (Subtask)task;
            epicId = Integer.toString(subtask.getEpicId());
        }

        return String.format("%s,%s,%s,%s,%s,%s,%s,%s",
                task.getId(),
                task.getType(),
                task.getName(),
                task.getStatus(),
                task.getDescription(),
                task.getStartTime(),
                task.getDuration().toMinutes(),
                epicId);
    }

    private Task fromString(String value) {
        String[] taskValues = value.split(",");
        LocalDateTime taskDT;
        if (taskValues[5].equals("null"))
            taskDT = null;
        else
            taskDT = LocalDateTime.parse(taskValues[5]);

        switch (TaskTypes.valueOf(taskValues[1])) {
            case TASK:
                return new Task(
                        taskValues[2],
                        taskValues[4],
                        TaskStatuses.valueOf(taskValues[3]),
                        Integer.parseInt(taskValues[0]),
                        taskDT,
                        Integer.parseInt(taskValues[6])
                );
            case EPIC:
                return new Epic(
                        taskValues[2],
                        taskValues[4],
                        TaskStatuses.valueOf(taskValues[3]),
                        Integer.parseInt(taskValues[0]),
                        taskDT,
                        Integer.parseInt(taskValues[6])
                );
            case SUBTASK:
                Subtask subtask = new Subtask(
                        taskValues[2],
                        taskValues[4],
                        TaskStatuses.valueOf(taskValues[3]),
                        Integer.parseInt(taskValues[0]),
                        taskDT,
                        Integer.parseInt(taskValues[6])
                );
                subtask.setEpicId(Integer.parseInt(taskValues[7]));
                return subtask;
        }
        return null;
    }

    private void addTask(Task task) {
        super.createTask(task);
    }

    private void addEpic(Epic epic) {
        super.createEpic(epic);
    }

    private void addSubtask(Subtask subtask) {
        super.createSubtask(subtask, subtask.getEpicId());
    }
}
