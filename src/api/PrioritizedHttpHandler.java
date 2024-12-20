package api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;

import java.io.IOException;
import java.time.LocalDateTime;

public class PrioritizedHttpHandler extends BaseHttpHandler implements HttpHandler {

    public PrioritizedHttpHandler(TaskManager taskManager) {
        super.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        if (endpoint.equals(Endpoint.GET_PRIORITIZED)) {
            handleGetPrioritized(exchange);
        } else {
            sendNotFound(exchange);
        }
    }

    private void handleGetPrioritized(HttpExchange exchange) throws IOException {
        System.out.println(LocalDateTime.now() + " ---> Получен запрос " + exchange.getRequestMethod() + " "
                + exchange.getRequestURI());

        String json = gson.toJson(taskManager.getPrioritizedTasks());
        System.out.println(LocalDateTime.now() + " <--- Сформирован ответ:");
        sendText(exchange, json, 200);
        System.out.println("Body: " + json);
    }
}
