package controller;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import model.Epic;
import model.SubTask;
import model.Task;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class HttpTaskServer {
    private final static int PORT = 8080;
    private final TaskManager manager;
    private final HttpServer httpServer;
    private final Gson gson;

    public HttpTaskServer() throws IOException {
        manager = Managers.getDefaultFileBackedTasksManager();
        gson = Managers.getGson();
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks/task", (exchange -> {
            if("GET".equals(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                String response = null;
                if(query!=null && query.contains("id")) {//не заходит в условие
                    int id = Integer.parseInt(query.split("id=")[1]);
                    response = gson.toJson(manager.getTaskById(id));
                } else {
                    response = gson.toJson(manager.getAllTasks());
                }
                exchange.sendResponseHeaders(200, 0);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else if("POST".equals(exchange.getRequestMethod())){
                int contentLength = Integer.parseInt(exchange.getRequestHeaders().getFirst("Content-Length"));
                InputStream is = exchange.getRequestBody();
                byte[] data = new byte[contentLength];
                int length = is.read(data);
                Task newTask = gson.fromJson(new String(data, StandardCharsets.UTF_8),Task.class);
                manager.createTask(newTask);
                exchange.sendResponseHeaders(200, 0);
                OutputStream os = exchange.getResponseBody();
                os.write("Задача Создана".getBytes());
                os.close();
            }else if("DELETE".equals(exchange.getRequestMethod())){
                String query = exchange.getRequestURI().getQuery();
                String response = null;
                if(query!=null && query.contains("id")){
                    int id = Integer.parseInt(query.split("id=")[1]);
                    response = "Задача удалена по id";
                    manager.deleteTaskById(id);
                } else {
                    manager.deleteAllTasks();
                    response = "Все задачи удалены";
                }
                exchange.sendResponseHeaders(200, 0);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }));
        httpServer.createContext("/tasks/subtask", (exchange -> {
            if ("GET".equals(exchange.getRequestMethod())){
                String query = exchange.getRequestURI().getQuery();
                String response = null;
                if(query!=null && query.contains("id")){
                    int id = Integer.parseInt(query.split("id=")[1]);
                    response = gson.toJson(manager.getSubTaskById(id));
                } else {
                    response = gson.toJson(manager.getAllSubTask());
                }
                exchange.sendResponseHeaders(200,0);
                OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
            } else if("POST".equals(exchange.getRequestMethod())){
                int contentLength = Integer.parseInt(exchange.getRequestHeaders().getFirst("Content-Length"));
                InputStream is = exchange.getRequestBody();
                byte[] data = new byte[contentLength];
                int length = is.read(data);
                SubTask newSubTask = gson.fromJson(new String(data, StandardCharsets.UTF_8),SubTask.class);
                if (manager.getEpicById(newSubTask.getIdEpic()) == null){
                    exchange.sendResponseHeaders(400,0);
                    OutputStream os = exchange.getResponseBody();
                    os.write("Подзадача не может существовать без Epic".getBytes());
                    os.close();
                } else {
                    manager.createSubTask(newSubTask);
                    exchange.sendResponseHeaders(200, 0);
                    OutputStream os = exchange.getResponseBody();
                    os.write("Подзадача Создана".getBytes());
                    os.close();
                }
            }else if("DELETE".equals(exchange.getRequestMethod())){
                String query = exchange.getRequestURI().getQuery();
                String response = null;
                if(query!=null && query.contains("id")){
                    int id = Integer.parseInt(query.split("id=")[1]);
                    response = "Подзадача удалена по id";
                    manager.deleteSubTaskById(id);
                } else {
                    manager.deleteAllSubTasks();
                    response = "Все Подзадачи удалены";
                }
                exchange.sendResponseHeaders(200, 0);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }));
        httpServer.createContext("/tasks/epic", (exchange -> {
            if ("GET".equals(exchange.getRequestMethod())){
                String query = exchange.getRequestURI().getQuery();
                String response = null;
                if(query!=null && query.contains("id")){
                    int id = Integer.parseInt(query.split("id=")[1]);
                    response = gson.toJson(manager.getEpicById(id));
                } else {
                    response = gson.toJson(manager.getAllEpic());
                }
                exchange.sendResponseHeaders(200,0);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else if ("POST".equals(exchange.getRequestMethod())) {
                int contentLength = Integer.parseInt(exchange.getRequestHeaders().getFirst("Content-Length"));
                InputStream is = exchange.getRequestBody();
                byte[] data = new byte[contentLength];
                int length = is.read(data);
                Epic newEpic = gson.fromJson(new String(data, StandardCharsets.UTF_8), Epic.class);
                newEpic.setSubTaskListId(new ArrayList<Integer>());
                manager.createEpic(newEpic);
                exchange.sendResponseHeaders(200, 0);
                OutputStream os = exchange.getResponseBody();
                os.write("Эпик создан".getBytes());
                os.close();
            } else if ("DELETE".equals(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                String response = null;
                if(query!=null && query.contains("id")){
                    int id = Integer.parseInt(query.split("id=")[1]);
                    response = "Эпик удален по id";
                    manager.deleteEpicById(id);
                } else {
                    manager.deleteAllEpic();
                    response = "Все Эпики удалены";
                }
                exchange.sendResponseHeaders(200, 0);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }));
        httpServer.createContext("/tasks/history", (exchange -> {
            if ("GET".equals(exchange.getRequestMethod())){
                String response = gson.toJson(manager.getHistory());
                exchange.sendResponseHeaders(200, 0);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                exchange.sendResponseHeaders(400, 0);
                OutputStream os = exchange.getResponseBody();
                os.write("Историю можно только получить".getBytes());
                os.close();
            }
        }));
        httpServer.createContext("/tasks", (exchange -> {
            if ("GET".equals(exchange.getRequestMethod())){
                String response = gson.toJson(manager.getPrioritizedTasks());
                exchange.sendResponseHeaders(200, 0);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                exchange.sendResponseHeaders(400, 0);
                OutputStream os = exchange.getResponseBody();
                os.write("Задачи по приоритету можно только получить".getBytes());
                os.close();
            }
        }));
        httpServer.start();
    }
}
