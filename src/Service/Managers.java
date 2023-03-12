package Service;

import java.util.ArrayList;
import java.util.List;

public class Managers {

    public static List<TaskManager> taskManagers;// можно присвоить Task, Epic, SubTask
    public static List<InMemoryHistoryManager> historyManagers;

//    TaskManager managerTask = new InMemoryTaskManager();
//    TaskManager managerTask1 = new ......(Новая реализация интерфейса TaskManager, добавить с список реализаций)
//    TaskManager managerTask2 = new ......(Новая реализация интерфейса TaskManager, добавить с список реализаций)

    public Managers() {
        this.taskManagers = new ArrayList<>();
        taskManagers.add(new InMemoryTaskManager());
        historyManagers.add(new InMemoryHistoryManager());
        //taskManagers.add(new ДругойКлассРеализации())

    }

    public static TaskManager getDefault() {
        return taskManagers.get(0);
    }

    public static InMemoryHistoryManager getDefaultHistory() {
        return historyManagers.get(0);
    }
}
