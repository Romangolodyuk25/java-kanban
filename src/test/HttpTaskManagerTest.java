package test;

import service.HttpTaskManager;
import service.Managers;
import service.TaskManager;

public class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager>{

    TaskManager taskManager = Managers.getDefault();

    public TaskManager getTaskManager() {
        return taskManager;
    }
}
