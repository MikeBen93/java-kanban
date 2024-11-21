package api;

import com.sun.net.httpserver.HttpServer;
import managers.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final TaskManager taskManager;
    HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public void start() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHttpHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtaskHttpHandler(taskManager));
        httpServer.createContext("/epics", new EpicHttpHandler(taskManager));
        httpServer.createContext("/history", new HistoryHttpHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHttpHandler(taskManager));

        httpServer.start();

        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stop() {
        httpServer.stop(0);
    }
}
