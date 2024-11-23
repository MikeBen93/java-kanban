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
import tasks.Epic;
import tasks.Subtask;
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
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubtaskHttpHandlerTest {
    TaskManager taskManager = Managers.getDefault();
    HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
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

    @DisplayName("POST /subtasks. Успешное создание подзадачи")
    @Test
    public void createSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("epicForTest", "epicForTest", TaskStatuses.NEW);
        taskManager.createEpic(epic);

        JsonObject subtaskObject = new JsonObject();
        subtaskObject.addProperty( "name", "subtaskFromWeb");
        subtaskObject.addProperty( "description", "subtaskFromWeb");
        subtaskObject.addProperty( "status", "NEW");
        subtaskObject.addProperty( "epicId", epic.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskObject.toString()))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Некорректный HTTP статус код");
        List<Subtask> subtasksFromManager = new ArrayList<>(taskManager.getSubtasks());
        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("subtaskFromWeb", subtasksFromManager.getFirst().getName(), "Некорректное имя подзадачи");
    }

    @DisplayName("POST /subtasks. Успешное обновление подзадачи")
    @Test
    public void updateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("epicForTest", "epicForTest", TaskStatuses.NEW);
        taskManager.createEpic(epic);

        JsonObject subtaskObject = new JsonObject();
        subtaskObject.addProperty( "name", "subtaskFromWeb");
        subtaskObject.addProperty( "description", "subtaskFromWeb");
        subtaskObject.addProperty( "status", "NEW");
        subtaskObject.addProperty( "epicId", epic.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskObject.toString()))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        subtaskObject.addProperty( "status", "IN_PROGRESS");
        subtaskObject.addProperty( "id",
                JsonParser.parseString(response.body()).getAsJsonObject().get("id").getAsInt());
        subtaskObject.addProperty( "startTime", "2024-10-04T16:00");
        subtaskObject.addProperty( "duration", 60);

        request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskObject.toString()))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject responseAsObject =  JsonParser.parseString(response.body()).getAsJsonObject();
        int idFromResponse = responseAsObject.get("id").getAsInt();
        Duration durationFromResponse = Duration.ofMinutes(responseAsObject.get("duration").getAsInt());
        LocalDateTime startTimeFromResponse = LocalDateTime.parse(responseAsObject.get("startTime").getAsString());
        TaskStatuses statusFromResponse = TaskStatuses.valueOf(responseAsObject.get("status").getAsString());


        assertEquals(201, response.statusCode(), "Некорректный HTTP статус код");
        List<Subtask> subtasksFromManager = new ArrayList<>(taskManager.getSubtasks());
        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное кол-во подзадач");
        assertEquals(idFromResponse, subtasksFromManager.getFirst().getId(), "Некорректное id подзадачи");
        assertEquals(durationFromResponse, subtasksFromManager.getFirst().getDuration(), "Некорректная длительность подзадачи");
        assertEquals(startTimeFromResponse, subtasksFromManager.getFirst().getStartTime(), "Некорректное время старта подзадачи");
        assertEquals(statusFromResponse, subtasksFromManager.getFirst().getStatus(), "Некорректный статус подзадачи");
        assertEquals(statusFromResponse, epic.getStatus(), "Некорректный статус эпика");
    }

    @DisplayName("GET /subtasks. Успешное получение всех подзадач")
    @Test
    public void getSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("epicForTest", "epicForTest", TaskStatuses.NEW);
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("subtaskForTest1", "subtaskForTest1", TaskStatuses.NEW);
        Subtask subtask2 = new Subtask("subtaskForTest2", "subtaskForTest2", TaskStatuses.NEW);
        taskManager.createSubtask(subtask1, epic.getId());
        taskManager.createSubtask(subtask2, epic.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JsonArray responseAsArray =  JsonParser.parseString(response.body()).getAsJsonArray();

        Type listType = new TypeToken<List<Subtask>>(){}.getType();
        List<Subtask> listOfSubtasks = gson.fromJson(responseAsArray, listType);

        assertEquals(200, response.statusCode(), "Некорректный HTTP статус код");
        List<Subtask> subtasksFromManager = new ArrayList<>(taskManager.getSubtasks());
        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(listOfSubtasks.size(), subtasksFromManager.size(), "Некорректное кол-во подзадач");
    }

    @DisplayName("GET /subtasks/{id}. Успешное получение одной подзадачи")
    @Test
    public void getSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("epicForTest", "epicForTest", TaskStatuses.NEW);
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("subtaskForTest1", "subtaskForTest1", TaskStatuses.NEW);
        Subtask subtask2 = new Subtask("subtaskForTest2", "subtaskForTest2", TaskStatuses.NEW);
        taskManager.createSubtask(subtask1, epic.getId());
        taskManager.createSubtask(subtask2, epic.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask2.getId());

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
        Subtask subtaskFromManager = taskManager.getSubtask(subtask2.getId());
        assertNotNull(subtaskFromManager, "Подзадача не возвращается");
        assertEquals(responseId, subtaskFromManager.getId(), "Некорректное id подзадачи");
        assertEquals(responseName, subtaskFromManager.getName(), "Некорректное название подзадачи");
    }

    @DisplayName("DELETE /subtasks/{id}. Успешное удаление одной задачи")
    @Test
    public void deleteSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("epicForTest", "epicForTest", TaskStatuses.NEW);
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("subtaskForTest1", "subtaskForTest1", TaskStatuses.NEW);
        Subtask subtask2 = new Subtask("subtaskForTest2", "subtaskForTest2", TaskStatuses.NEW);
        taskManager.createSubtask(subtask1, epic.getId());
        taskManager.createSubtask(subtask2, epic.getId());
        int deletingSubtaskId = subtask2.getId();

        Subtask subtaskFromManager = taskManager.getSubtask(deletingSubtaskId);

        assertNotNull(subtaskFromManager, "Задача не возвращается");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + deletingSubtaskId);
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThrows(NotFoundException.class, () -> taskManager.getSubtask(deletingSubtaskId), "Некорректный срабатывание исключения");

        assertEquals(200, response.statusCode(), "Некорректный HTTP статус код");
    }
}
