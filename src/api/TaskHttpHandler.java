package api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.ManagerValidateException;
import managers.NotFoundException;
import managers.TaskManager;
import tasks.Task;
import tasks.TaskStatuses;

import java.io.IOException;
import java.time.LocalDateTime;

public class TaskHttpHandler extends BaseHttpHandler implements HttpHandler {

    public TaskHttpHandler(TaskManager taskManager) {
        super.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_TASKS:
                handleGetTasks(exchange);
                break;
            case GET_TASK_BY_ID:
                handleGetTaskById(exchange);
                break;
            case POST_TASK:
                handlePostTask(exchange);
                break;
            case DELETE_TASK:
                handleDeleteTask(exchange);
                break;
            case DELETE_ALL_TASKS:
                handleDeleteAllTask(exchange);
                break;
            default:
                sendNotFound(exchange);
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        System.out.println(LocalDateTime.now() + " ---> Получен запрос " + exchange.getRequestMethod() + " "
                + exchange.getRequestURI());
        try {
            String json = gson.toJson(taskManager.getTasks());
            System.out.println(LocalDateTime.now() + " <--- Сформирован ответ:");

            sendText(exchange, json, 200);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            sendError(exchange);
        }
    }

    private void handleGetTaskById(HttpExchange exchange) throws IOException {
        System.out.println(LocalDateTime.now() + " ---> Получен запрос " + exchange.getRequestMethod() + " "
                + exchange.getRequestURI());
        try {
            String json = gson.toJson(taskManager.getTask(
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

    private void handlePostTask(HttpExchange exchange) throws IOException {
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
                json = updateTask(jsonObject, jsonElement);
            } else {
                json = createTask(jsonObject);
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

    private String createTask(JsonObject taskAsJsonObject) {
        String name = taskAsJsonObject.get("name").getAsString();
        String description = taskAsJsonObject.get("description").getAsString();
        TaskStatuses taskStatus = gson.fromJson(taskAsJsonObject.get("status"), TaskStatuses.class);

        Task newTask = new Task(name, description, taskStatus);
        taskManager.createTask(newTask);
        System.out.println(LocalDateTime.now() + " Задача с id= " + newTask.getId() + " создана");

        return gson.toJson(newTask);
    }

    private String updateTask(JsonObject taskAsJsonObject, JsonElement taskAsJsonElement) {
        String name = taskAsJsonObject.get("name").getAsString();
        String description = taskAsJsonObject.get("description").getAsString();
        TaskStatuses taskStatus = gson.fromJson(taskAsJsonObject.get("status"), TaskStatuses.class);
        int id = taskAsJsonObject.get("id").getAsInt();
        LocalDateTime startTime = LocalDateTime.parse(taskAsJsonObject.get("startTime").getAsString());
        int duration = taskAsJsonObject.get("duration").getAsInt();

        Task updatedTask = new Task(name, description, taskStatus, id, startTime, duration);
        taskManager.updateTask(updatedTask);
        System.out.println(LocalDateTime.now() + " Задача с id=" + updatedTask.getId() + " обновлена");
        return gson.toJson(updatedTask);



    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        System.out.println(LocalDateTime.now() + " ---> Получен запрос " + exchange.getRequestMethod() + " "
                + exchange.getRequestURI());
        try {
            taskManager.removeTask(Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]));

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

    private void handleDeleteAllTask(HttpExchange exchange) throws IOException {
        try {
            taskManager.removeAllTasks();

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
