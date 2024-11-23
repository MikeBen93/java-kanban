package api;

import api.adapter.DurationAdapter;
import api.adapter.LocalDateTimeAdapter;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import managers.Managers;
import managers.NotFoundException;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.TaskStatuses;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskHttpHandlerTest {
    TaskManager taskManager = Managers.getDefault();
    HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration .class, new DurationAdapter())
            .create();

    @BeforeEach
    public void setUp() throws IOException {
        taskManager.removeAllTasks();
        taskManager.removeAllSubtasks();
        taskManager.removeAllEpics();

        httpTaskServer.start();
    }

    @AfterEach
    public void shutDown() {
        httpTaskServer.stop();
    }

    @DisplayName("POST /tasks. Успешное создание задачи")
    @Test
    public void createTask() throws IOException, InterruptedException {
        JsonObject taskObject = new JsonObject();
        taskObject.addProperty( "name", "taskFromWeb");
        taskObject.addProperty( "description", "taskFromWeb");
        taskObject.addProperty( "status", "NEW");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskObject.toString()))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Некорректный HTTP статус код");
        List<Task> tasksFromManager = new ArrayList<>(taskManager.getTasks());
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("taskFromWeb", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @DisplayName("POST /tasks. Успешное обновление задачи")
    @Test
    public void updateTask() throws IOException, InterruptedException {
        JsonObject taskObject = new JsonObject();
        taskObject.addProperty( "name", "taskFromWeb");
        taskObject.addProperty( "description", "taskFromWeb");
        taskObject.addProperty( "status", "NEW");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskObject.toString()))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        taskObject.addProperty( "status", "IN_PROGRESS");
        taskObject.addProperty( "id",
                JsonParser.parseString(response.body()).getAsJsonObject().get("id").getAsInt());
        taskObject.addProperty( "startTime", "2024-10-04T16:00");
        taskObject.addProperty( "duration", 60);

        request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskObject.toString()))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject responseAsObject =  JsonParser.parseString(response.body()).getAsJsonObject();
        int idFromResponse = responseAsObject.get("id").getAsInt();
        Duration durationFromResponse = Duration.ofMinutes(responseAsObject.get("duration").getAsInt());
        LocalDateTime startTimeFromResponse = LocalDateTime.parse(responseAsObject.get("startTime").getAsString());
        TaskStatuses statusFromResponse = TaskStatuses.valueOf(responseAsObject.get("status").getAsString());


        assertEquals(201, response.statusCode(), "Некорректный HTTP статус код");
        List<Task> tasksFromManager = new ArrayList<>(taskManager.getTasks());
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное кол-во задач");
        assertEquals(idFromResponse, tasksFromManager.getFirst().getId(), "Некорректное id задачи");
        assertEquals(durationFromResponse, tasksFromManager.getFirst().getDuration(), "Некорректная длительность задачи");
        assertEquals(startTimeFromResponse, tasksFromManager.getFirst().getStartTime(), "Некорректное время старта задачи");
        assertEquals(statusFromResponse, tasksFromManager.getFirst().getStatus(), "Некорректный статус задачи");
    }

    @DisplayName("GET /tasks. Успешное получение всех задач")
    @Test
    public void getTasks() throws IOException, InterruptedException {
        JsonObject taskObject1 = new JsonObject();
        JsonObject taskObject2 = new JsonObject();
        taskObject1.addProperty( "name", "taskFromWeb_1");
        taskObject1.addProperty( "description", "taskFromWeb_1");
        taskObject1.addProperty( "status", "NEW");
        taskObject2.addProperty( "name", "taskFromWeb_2");
        taskObject2.addProperty( "description", "taskFromWeb_2");
        taskObject2.addProperty( "status", "NEW");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskObject1.toString()))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskObject2.toString()))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JsonArray responseAsArray =  JsonParser.parseString(response.body()).getAsJsonArray();

        Type listType = new TypeToken<List<Task>>(){}.getType();
        List<Task> listOfTasks = gson.fromJson(responseAsArray, listType);



        assertEquals(200, response.statusCode(), "Некорректный HTTP статус код");
        List<Task> tasksFromManager = new ArrayList<>(taskManager.getTasks());
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(listOfTasks.size(), tasksFromManager.size(), "Некорректное кол-во задач");
    }

    @DisplayName("GET /tasks/{id}. Успешное получение одной задачи")
    @Test
    public void getTaskById() throws IOException, InterruptedException {
        Task newTask1 = new Task("testTask1", "testTask1", TaskStatuses.NEW);
        Task newTask2 = new Task("testTask2", "testTask2", TaskStatuses.NEW);

        taskManager.createTask(newTask1);
        taskManager.createTask(newTask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + newTask2.getId());

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JsonObject responseAsObject =  JsonParser.parseString(response.body()).getAsJsonObject();
        int responseId = responseAsObject.get("id").getAsInt();
        String responseName = responseAsObject.get("name").getAsString();

        assertEquals(200, response.statusCode(), "Некорректный HTTP статус код");
        Task taskFromManager = taskManager.getTask(newTask2.getId());
        assertNotNull(taskFromManager, "Задача не возвращается");
        assertEquals(responseId, taskFromManager.getId(), "Некорректное id задачи");
        assertEquals(responseName, taskFromManager.getName(), "Некорректное название задачи");
    }

    @DisplayName("DELETE /tasks/{id}. Успешное удаление одной задачи")
    @Test
    public void deleteTaskById() throws IOException, InterruptedException {
        Task newTask1 = new Task("testTask1", "testTask1", TaskStatuses.NEW);
        Task newTask2 = new Task("testTask2", "testTask2", TaskStatuses.NEW);
        int deletingTaskId = newTask2.getId();

        taskManager.createTask(newTask1);
        taskManager.createTask(newTask2);

        Task taskFromManager = taskManager.getTask(deletingTaskId);

        assertNotNull(taskFromManager, "Задача не возвращается");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + deletingTaskId);
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThrows(NotFoundException.class, () -> taskManager.getTask(deletingTaskId), "Некорректный срабатывание исключения");

        assertEquals(200, response.statusCode(), "Некорректный HTTP статус код");
    }

}
