package test;

import org.junit.jupiter.api.BeforeEach;
import service.InMemoryTaskManager;
import service.Managers;
import service.TaskManager;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager>{

    TaskManager taskManager = Managers.getDefault();

    public TaskManager getTaskManager() {
        return  taskManager;
    }
}
