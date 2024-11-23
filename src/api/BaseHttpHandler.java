package api;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import api.adapter.DurationAdapter;
import api.adapter.LocalDateTimeAdapter;
import com.sun.net.httpserver.HttpExchange;
import managers.*;

import java.io.OutputStream;

import java.io.IOException;

public class BaseHttpHandler {

    protected TaskManager taskManager;
    protected Gson gson;

    BaseHttpHandler() {
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    protected Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        switch (pathParts[1]) {
            case "tasks":
                switch (requestMethod) {
                    case "GET":
                        if (pathParts.length == 3)
                            return Endpoint.GET_TASK_BY_ID;
                        return Endpoint.GET_TASKS;
                    case "POST":
                        return Endpoint.POST_TASK;
                    case "DELETE":
                        if (pathParts.length == 3)
                            return Endpoint.DELETE_TASK;
                        return Endpoint.DELETE_ALL_TASKS;
                    default:
                        return Endpoint.UNKNOWN;
                }
            case "subtasks":
                switch (requestMethod) {
                    case "GET":
                        if (pathParts.length == 3)
                            return Endpoint.GET_SUBTASK_BY_ID;
                        return Endpoint.GET_SUBTASKS;
                    case "POST":
                        return Endpoint.POST_SUBTASK;
                    case "DELETE":
                        if (pathParts.length == 3)
                            return Endpoint.DELETE_SUBTASK;
                        return Endpoint.DELETE_ALL_SUBTASKS;
                    default:
                        return Endpoint.UNKNOWN;
                }
            case "epics":
                switch (requestMethod) {
                    case "GET":
                        if (pathParts.length == 3)
                            return Endpoint.GET_EPIC_BY_ID;
                        else if (pathParts.length == 4)
                            return Endpoint.GET_EPIC_SUBTASKS;
                        return Endpoint.GET_EPICS;
                    case "POST":
                        return Endpoint.POST_EPIC;
                    case "DELETE":
                        if (pathParts.length == 3)
                            return Endpoint.DELETE_EPIC;
                        return Endpoint.DELETE_ALL_EPICS;
                    default:
                        return Endpoint.UNKNOWN;
                }
            case "history":
                return Endpoint.GET_HISTORY;
            case "prioritized":
                return Endpoint.GET_PRIORITIZED;
            default:
                return Endpoint.UNKNOWN;
        }
    }



    protected void sendText(HttpExchange exchange,
                            String responseString,
                            int responseCode) throws IOException {

        exchange.sendResponseHeaders(responseCode, 0);
        if (responseString != null && !responseString.isEmpty())
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseString.getBytes());
            }

        exchange.close();
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(404, 0);
        exchange.close();
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(406, 0);
        exchange.close();
    }

    protected void sendError(HttpExchange exchange) throws IOException {
        int statusCode = 500;
        exchange.sendResponseHeaders(statusCode, 0);
        System.out.println(LocalDateTime.now() + " Отправлен  statusCode: " + statusCode);
        System.out.println(exchange.getResponseHeaders());
        exchange.close();
    }

    protected void sendBadRequest(HttpExchange exchange) throws IOException {
        System.out.println(LocalDateTime.now() + " Некорректный запрос");
        exchange.sendResponseHeaders(400, 0);
        System.out.println(exchange.getResponseHeaders());
        exchange.close();
    }

    protected String readBodyRequest(HttpExchange exchange) throws IOException {
        String body;
        try (InputStream inputStream = exchange.getRequestBody()) {
            body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
        System.out.println(LocalDateTime.now() + " ---> Получен запрос " + exchange.getRequestMethod() + " "
                + exchange.getRequestURI() + "\nBody: " + body);

        if (body.isEmpty()) {
            sendBadRequest(exchange);
            return "";
        }
        return body;
    }
}
