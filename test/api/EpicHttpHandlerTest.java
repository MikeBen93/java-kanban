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

public class EpicHttpHandlerTest {
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

    @DisplayName("POST /epics. Успешное создание эпика")
    @Test
    public void createEpic() throws IOException, InterruptedException {
        JsonObject epicObject = new JsonObject();
        epicObject.addProperty( "name", "epicFromWeb");
        epicObject.addProperty( "description", "epicFromWeb");
        epicObject.addProperty( "status", "NEW");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicObject.toString()))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Некорректный HTTP статус код");
        List<Epic> epicsFromManager = new ArrayList<>(taskManager.getEpics());
        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("epicFromWeb", epicsFromManager.getFirst().getName(), "Некорректное имя эпика");
    }

    @DisplayName("GET /epics. Успешное получение всех эпиков")
    @Test
    public void getEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("epicForTest1", "epicForTest1", TaskStatuses.NEW);
        Epic epic2 = new Epic("epicForTest1", "epicForTest1", TaskStatuses.NEW);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JsonArray responseAsArray =  JsonParser.parseString(response.body()).getAsJsonArray();

        Type listType = new TypeToken<List<Epic>>(){}.getType();
        List<Epic> listOfEpics = gson.fromJson(responseAsArray, listType);

        assertEquals(200, response.statusCode(), "Некорректный HTTP статус код");
        List<Epic> epicsFromManager = new ArrayList<>(taskManager.getEpics());
        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(listOfEpics.size(), epicsFromManager.size(), "Некорректное кол-во эпиков");
    }

    @DisplayName("GET /epics/{id}. Успешное получение одного эпика")
    @Test
    public void getEpicById() throws IOException, InterruptedException {
        Epic epic1 = new Epic("epicForTest1", "epicForTest1", TaskStatuses.NEW);
        taskManager.createEpic(epic1);
        Epic epic2 = new Epic("epicForTest2", "epicForTest2", TaskStatuses.NEW);
        taskManager.createEpic(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic2.getId());

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
        Epic epicFromManager = taskManager.getEpic(epic2.getId());
        assertNotNull(epicFromManager, "Эпик не возвращается");
        assertEquals(responseId, epicFromManager.getId(), "Некорректное id эпика");
        assertEquals(responseName, epicFromManager.getName(), "Некорректное название эпика");
    }


}
