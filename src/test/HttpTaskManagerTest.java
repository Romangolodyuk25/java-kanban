package test;

import controller.HttpTaskServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import server.KVServer;
import service.HttpTaskManager;
import service.Managers;
import service.TaskManager;

import java.io.IOException;

public class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager>{

    TaskManager taskManager = Managers.getDefault();
    static KVServer kvServer = null;
    static HttpTaskServer httpTaskServer = null;

    public TaskManager getTaskManager() {
        return taskManager;
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
}
