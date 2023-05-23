package test;

import com.google.gson.Gson;
import controller.HttpTaskServer;
import model.Status;
import model.Task;
import org.junit.jupiter.api.*;
import server.KVServer;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HttpTaskServerTest {
    HttpClient client;
    Gson gson;
    TaskManager manager;
    static HttpTaskServer httpTaskServer = null;
    static KVServer kvServer = null;

    public Task createTask(){
        return new Task("Переезд", "Я буду переезжать", Status.NEW, 1, LocalDateTime.of(2023, 1, 1, 10, 0), 100);
    }
    public void deleteAll() throws IOException, InterruptedException {
        shouldDeleteAllTask();
    }

    @BeforeEach
    public void beforeEach() throws IOException, InterruptedException {
        client = HttpClient.newHttpClient();
        gson = Managers.getGson();
        manager = Managers.getDefaultFileBackedTasksManager();
        TaskManager manager = Managers.getDefaultFileBackedTasksManager();
        deleteAll();
    }
    @BeforeAll
    public static void beforeAll() throws IOException {
        try {
            httpTaskServer = new HttpTaskServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        kvServer = new KVServer();
        kvServer.start();
    }

    @AfterAll
    public static void afterAll(){
        httpTaskServer.close();
        kvServer.close();
    }


    public Task createRequestForCreateTask() throws IOException, InterruptedException{
        URI urlAddTask = URI.create("http://localhost:8080/tasks/task/");
        Task newTask = createTask();
        String json = gson.toJson(newTask);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest requestAddTask = HttpRequest.newBuilder().uri(urlAddTask).POST(body).build();
        HttpResponse<String> responseAddTask = client.send(requestAddTask, HttpResponse.BodyHandlers.ofString());
        return newTask;
    }

    @Test
    @Order(1)
    public void shouldGetTaskById() throws IOException, InterruptedException {
        Task newTask = createRequestForCreateTask();
        //положил задачу

        //получаю ее по айди и проверяю с той то положил(newTask)
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task receivedTask = gson.fromJson(response.body(), Task.class);
        assertEquals(200, response.statusCode());
        assertEquals(newTask, receivedTask);
        assertEquals(newTask.getId(), receivedTask.getId());
    }

    @Test
    public void shouldGetAllTasks() throws IOException, InterruptedException {
        createRequestForCreateTask();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        ArrayList<Task> list = gson.fromJson(response.body(), ArrayList.class);
        assertEquals(1, list.size());
    }


    @Test
    public void shouldCreateTask() throws IOException, InterruptedException{
        URI url = URI.create("http://localhost:8080/tasks/task/");
        Task newTask = createTask();
        String json = gson.toJson(newTask);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("Задача создана", response.body());

       //нужно получить с сервера либо задачу либо список всех задач и сверить их
    }

    @Test
    public void shouldDeleteAllTask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        Task newTask = createTask();
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("Все задачи удалены", response.body());
    }
    @Test
    public void shouldDeleteTaskById() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/?id=2");
        Task newTask = createTask();
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("Задача удалена по id", response.body());
    }
}
