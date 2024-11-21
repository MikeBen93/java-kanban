package api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.ManagerValidateException;
import managers.NotFoundException;
import managers.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.TaskStatuses;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class EpicHttpHandler extends BaseHttpHandler implements HttpHandler {
    public EpicHttpHandler(TaskManager taskManager) {
        super.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_EPICS:
                handleGetEpics(exchange);
                break;
            case GET_EPIC_BY_ID:
                handleGetEpicById(exchange);
                break;
            case POST_EPIC:
                handlePostEpic(exchange);
                break;
            case GET_EPIC_SUBTASKS:
                handleGetEpicSubtasks(exchange);
            case DELETE_EPIC:
                handleDeleteEpic(exchange);
                break;
            case DELETE_ALL_EPICS:
                handleDeleteAllEpics(exchange);
                break;
            default:
                sendNotFound(exchange);
        }
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        System.out.println(LocalDateTime.now() + " ---> Получен запрос " + exchange.getRequestMethod() + " "
                + exchange.getRequestURI());
        try {
            String json = gson.toJson(taskManager.getEpics());
            System.out.println(LocalDateTime.now() + " <--- Сформирован ответ:");

            sendText(exchange, json, 200);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            sendError(exchange);
        }
    }

    private void handleGetEpicById(HttpExchange exchange) throws IOException {
        System.out.println(LocalDateTime.now() + " ---> Получен запрос " + exchange.getRequestMethod() + " "
                + exchange.getRequestURI());
        try {
            String json = gson.toJson(taskManager.getEpic(
                    Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2])
            ));
            System.out.println(LocalDateTime.now() + " <--- Сформирован ответ:");
            sendText(exchange, json, 200);
        } catch (NotFoundException e) {
            System.err.println(e.getMessage());
            sendNotFound(exchange);
        }  catch (Exception e) {
            System.err.println(e.getMessage());
            sendError(exchange);
        }
    }

    private void handlePostEpic(HttpExchange exchange) throws IOException {
        String body = readBodyRequest(exchange);
        String json;

        try {
            JsonElement jsonElement = JsonParser.parseString(body);
            if (!jsonElement.isJsonObject()) {
                sendBadRequest(exchange);
                return;
            }

            JsonObject jsonObject = jsonElement.getAsJsonObject();
            json = createEpic(jsonObject);

            System.out.println(LocalDateTime.now() + " <--- Сформирован ответ:");
            sendText(exchange, json, 201);
            System.out.println("Body: " + json);
        } catch (ManagerValidateException e) {
            System.err.println(e.getMessage());
            sendHasInteractions(exchange);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            sendError(exchange);
        }
    }

    private String createEpic(JsonObject epicAsJsonObject) {
        String name = epicAsJsonObject.get("name").getAsString();
        String description = epicAsJsonObject.get("description").getAsString();
        TaskStatuses epicStatus = gson.fromJson(epicAsJsonObject.get("status"), TaskStatuses.class);

        Epic newEpic = new Epic(name, description, epicStatus);
        taskManager.createEpic(newEpic);
        System.out.println(LocalDateTime.now() + " Эпик с id= " + newEpic.getId() + " создана");

        return gson.toJson(newEpic);
    }


    private void handleGetEpicSubtasks(HttpExchange exchange) throws IOException {
        System.out.println(LocalDateTime.now() + " ---> Получен запрос " + exchange.getRequestMethod() + " "
                + exchange.getRequestURI());

        try {
            int id = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
            if (taskManager.getEpic(id) == null) {
                sendNotFound(exchange);
                return;
            }
            ArrayList<Subtask> subtasks = taskManager.getEpicSubtasks(id);
            String json = gson.toJson(subtasks);
            System.out.println(LocalDateTime.now() + " <--- Сформирован ответ:");
            sendText(exchange, json, 200);
            System.out.println("Body: " + json);

        } catch (NotFoundException e) {
            System.err.println(e.getMessage());
            sendNotFound(exchange);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            sendError(exchange);
        }
    }

    private void handleDeleteEpic(HttpExchange exchange) throws IOException {
        System.out.println(LocalDateTime.now() + " ---> Получен запрос " + exchange.getRequestMethod() + " "
                + exchange.getRequestURI());
        try {
            taskManager.removeEpic(Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]));

            System.out.println(LocalDateTime.now() + " <--- Сформирован ответ:");
            sendText(exchange, "", 200);
        } catch (NotFoundException e) {
            System.err.println(e.getMessage());
            sendNotFound(exchange);
        }  catch (Exception e) {
            System.err.println(e.getMessage());
            sendError(exchange);
        }
    }

    private void handleDeleteAllEpics(HttpExchange exchange) throws IOException {
        try {
            taskManager.removeAllEpics();

            System.out.println(LocalDateTime.now() + " <--- Сформирован ответ:");
            sendText(exchange, "", 200);
        } catch (NotFoundException e) {
            System.err.println(e.getMessage());
            sendNotFound(exchange);
        }  catch (Exception e) {
            System.err.println(e.getMessage());
            sendError(exchange);
        }
    }
}
