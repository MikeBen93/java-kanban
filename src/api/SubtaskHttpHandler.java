package api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.ManagerValidateException;
import managers.NotFoundException;
import managers.TaskManager;
import tasks.Subtask;
import tasks.TaskStatuses;

import java.io.IOException;
import java.time.LocalDateTime;

public class SubtaskHttpHandler extends BaseHttpHandler implements HttpHandler {

    public SubtaskHttpHandler(TaskManager taskManager) {
        super.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_SUBTASKS:
                handleGetSubtaks(exchange);
                break;
            case GET_SUBTASK_BY_ID:
                handleGetSubtaskById(exchange);
                break;
            case POST_SUBTASK:
                handlePostSubtask(exchange);
                break;
            case DELETE_SUBTASK:
                handleDeleteSubtask(exchange);
                break;
            case DELETE_ALL_SUBTASKS:
                handleDeleteAllSubtasks(exchange);
                break;
            default:
                sendNotFound(exchange);
        }
    }

    private void handleGetSubtaks(HttpExchange exchange) throws IOException {
        System.out.println(LocalDateTime.now() + " ---> Получен запрос " + exchange.getRequestMethod() + " "
                + exchange.getRequestURI());
        try {
            String json = gson.toJson(taskManager.getSubtasks());
            System.out.println(LocalDateTime.now() + " <--- Сформирован ответ:");

            sendText(exchange, json, 200);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            sendError(exchange);
        }
    }

    private void handleGetSubtaskById(HttpExchange exchange) throws IOException {
        System.out.println(LocalDateTime.now() + " ---> Получен запрос " + exchange.getRequestMethod() + " "
                + exchange.getRequestURI());
        try {
            String json = gson.toJson(taskManager.getSubtask(
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

    private void handlePostSubtask(HttpExchange exchange) throws IOException {
        String body = readBodyRequest(exchange);
        String json;

        try {
            JsonElement jsonElement = JsonParser.parseString(body);
            if (!jsonElement.isJsonObject()) {
                sendBadRequest(exchange);
                return;
            }

            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject.has("id")) {
                int id = jsonObject.get("id").getAsInt();
                json = updateSubtask(jsonObject, jsonElement);
                System.out.println(id);
            } else {
                json = createSubtask(jsonObject);
            }

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

    private String createSubtask(JsonObject taskAsJsonObject) {
        String name = taskAsJsonObject.get("name").getAsString();
        String description = taskAsJsonObject.get("description").getAsString();
        TaskStatuses taskStatus = gson.fromJson(taskAsJsonObject.get("status"), TaskStatuses.class);
        int epicId = taskAsJsonObject.get("epicId").getAsInt();

        Subtask newSubtask = new Subtask(name, description, taskStatus);
        taskManager.createSubtask(newSubtask, epicId);
        System.out.println(LocalDateTime.now() + " Подзадача с id= " + newSubtask.getId() + " создана");

        return gson.toJson(newSubtask);
    }

    private String updateSubtask(JsonObject taskAsJsonObject, JsonElement taskAsJsonElement) {
        String name = taskAsJsonObject.get("name").getAsString();
        String description = taskAsJsonObject.get("description").getAsString();
        TaskStatuses taskStatus = gson.fromJson(taskAsJsonObject.get("status"), TaskStatuses.class);
        int id = taskAsJsonObject.get("id").getAsInt();
        LocalDateTime startTime = LocalDateTime.parse(taskAsJsonObject.get("startTime").getAsString());
        int duration = taskAsJsonObject.get("duration").getAsInt();

        Subtask updatedSubtask = new Subtask(name, description, taskStatus, id, startTime, duration);
        taskManager.updateSubtask(updatedSubtask);
        System.out.println(LocalDateTime.now() + " Подзадача с id=" + updatedSubtask.getId() + " обновлена");
        return gson.toJson(updatedSubtask);



    }

    private void handleDeleteSubtask(HttpExchange exchange) throws IOException {
        System.out.println(LocalDateTime.now() + " ---> Получен запрос " + exchange.getRequestMethod() + " "
                + exchange.getRequestURI());
        try {
            taskManager.removeSubtask(Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]));

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

    private void handleDeleteAllSubtasks(HttpExchange exchange) throws IOException {
        try {
            taskManager.removeAllSubtasks();

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
