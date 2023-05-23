package test;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import javax.imageio.IIOException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class HttpTaskServerTest {
    HttpClient client;
    Gson gson;
    TaskManager manager;

    public Task createTask(){
        return new Task("Переезд", "Я буду переезжать", Status.NEW, 1, LocalDateTime.of(2023, 1, 1, 10, 0), 100);
    }

    public Task createRequestForCreateTask() throws IOException, InterruptedException{
        URI urlAddTask = URI.create("http://localhost:8080/tasks/task/");
        Task newTask = createTask();
        String json = gson.toJson(newTask);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest requestAddTask = HttpRequest.newBuilder().uri(urlAddTask).POST(body).build();
        HttpResponse<String> responseAddTask = client.send(requestAddTask, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseAddTask.statusCode());
        assertEquals("Задача Создана", responseAddTask.body());
        return newTask;
    }

    @BeforeEach
    public void beforeEach(){
        client = HttpClient.newHttpClient();
        gson = Managers.getGson();
        manager = Managers.getDefaultFileBackedTasksManager();
    }

//    @Test
//    public void shouldGetEmptyJsonForAllTasks() throws IOException, InterruptedException {
//        URI url = URI.create("http://localhost:8080/tasks/task/");
//        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//        assertEquals(200, response.statusCode());
//        assertEquals(0,gson.fromJson(response.body(), HashMap.class).size());
//    }

    @Test
    public void shouldGetTaskById() throws IOException, InterruptedException {
        Task newTask = createRequestForCreateTask();
        //положил задачу

        //получаю ее по айди и проверяю с той то положил(newTask)
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task receivedTask = gson.fromJson(response.body(), Task.class);
        assertEquals(200, response.statusCode());
        assertEquals(newTask, receivedTask);
        assertEquals(newTask.getId(), receivedTask.getId());
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
        assertEquals("Задача Создана", response.body());

       //нужно получить с сервера либо задачу либо список всех задач и сверить их
    }
}
