package controller;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import model.Epic;
import model.SubTask;
import model.Task;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
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
        httpServer.createContext("/tasks/task", (this::handleTask));
        httpServer.createContext("/tasks/subtask", (this::handleSubtask));
        httpServer.createContext("/tasks/epic", (this::handleEpic));
        httpServer.createContext("/tasks/history", (this::handlerHistory));
        httpServer.createContext("/tasks", (this::handlerPrioritized));
        httpServer.start();
    }

    private void handleTask(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET":
                getTasks(exchange);
                break;
            case "POST":
                createTask(exchange);
                break;
            case "DELETE":
                deleteTask(exchange);
                break;
            default:
                nonExistentMethod(exchange);
                break;
        }
    }

    public void handleSubtask(HttpExchange exchange) throws IOException{
        switch (exchange.getRequestMethod()) {
            case "GET":
                getSubTask(exchange);
                break;
            case "POST":
                createSubTask(exchange);
                break;
            case "DELETE":
                deleteSubTask(exchange);
                break;
            default:
                nonExistentMethod(exchange);
                break;
        }
    }

    public void handleEpic(HttpExchange exchange) throws IOException{
        switch (exchange.getRequestMethod()) {
            case "GET":
                getEpic(exchange);
                break;
            case "POST":
                createEpic(exchange);
                break;
            case "DELETE":
                deleteEpic(exchange);
                break;
            default:
                nonExistentMethod(exchange);
                break;
        }
    }

    public void handlerHistory(HttpExchange exchange) throws IOException{
        switch (exchange.getRequestMethod()){
            case "GET":
                getHistory(exchange);
                break;
            default: badRequest(exchange, "Историю можно только получить");
        }
    }

    public void handlerPrioritized(HttpExchange exchange) throws IOException{
        if ("GET".equals(exchange.getRequestMethod())){
            String response = gson.toJson(manager.getPrioritizedTasks());
            sendText(exchange, response);
        } else {
            nonExistentMethod(exchange);
        }
    }

    private void getHistory(HttpExchange exchange) throws IOException{
        String response = gson.toJson(manager.getHistory());
        sendText(exchange,response);
    }

    private void getEpic(HttpExchange exchange) throws IOException{
        String query = exchange.getRequestURI().getQuery();
        String response = null;
        if(query!=null && query.contains("id")){
            int id = Integer.parseInt(query.split("id=")[1]);
            response = gson.toJson(manager.getEpicById(id));
        } else {
            response = gson.toJson(manager.getAllEpic());
        }
        sendText(exchange,response);
    }

    private void createEpic(HttpExchange exchange) throws  IOException{
        String body = readText(exchange);
        if(body.isEmpty()){
            badRequest(exchange, "Тело не может быть пустым");
        } else {
            Epic newEpic = gson.fromJson(body, Epic.class);
            newEpic.setSubTaskListId(new ArrayList<Integer>());
            manager.createEpic(newEpic);
            sendText(exchange,"Епик создан");
        }
    }

    private void deleteEpic(HttpExchange exchange) throws IOException{
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
        sendText(exchange,response);
    }

    private void getSubTask(HttpExchange exchange) throws IOException{
        String query = exchange.getRequestURI().getQuery();
        String response = null;
        if(query!=null && query.contains("id")){
            int id = Integer.parseInt(query.split("id=")[1]);
            response = gson.toJson(manager.getSubTaskById(id));
        } else {
            response = gson.toJson(manager.getAllSubTask());
        }
        sendText(exchange,response);
    }

    private void createSubTask(HttpExchange exchange) throws IOException{
        String body = readText(exchange);
        if(body.isEmpty()){
            badRequest(exchange,"Тело не может быть пустым");
        }else {
            SubTask newSubTask = gson.fromJson(body, SubTask.class);
            if (manager.getEpicById(newSubTask.getIdEpic()) == null) {
                badRequest(exchange, "Поздадача не может существовать без Эпика");
            } else {
                manager.createSubTask(newSubTask);
                sendText(exchange,"Подзадача создана");
            }
        }
    }

    private void deleteSubTask(HttpExchange exchange) throws IOException{
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
        sendText(exchange,response);
    }

    private void getTasks(HttpExchange exchange) throws IOException{
        String query = exchange.getRequestURI().getQuery();
        String response = null;
        if(query!=null && query.contains("id")) {//не заходит в условие
            int id = Integer.parseInt(query.split("id=")[1]);
            response = gson.toJson(manager.getTaskById(id));
        } else {
            response = gson.toJson(manager.getAllTasks());
        }
        sendText(exchange, response);
    }
    private void createTask(HttpExchange exchange) throws IOException{
        String body = readText(exchange);
        if(body.isEmpty()){
            badRequest(exchange, "Неправильный запрос");
        } else {
            Task newTask = gson.fromJson(body, Task.class);
            manager.createTask(newTask);
            sendText(exchange, "Задача создана");
        }
    }

    private void deleteTask(HttpExchange exchange) throws IOException {
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
        sendText(exchange, response);
    }
    private void nonExistentMethod(HttpExchange exchange) throws IOException {
        System.out.println("Не существующмй запрос");
        exchange.sendResponseHeaders(405, 0);
        exchange.close();
    }

    private String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    private void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    private void badRequest(HttpExchange exchange, String body) throws IOException {
        byte[] resp = body.getBytes(StandardCharsets.UTF_8);
        System.out.println(body);
        exchange.sendResponseHeaders(400,0);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    public void close() {
        httpServer.stop(0);
    }
}
