import controller.HttpTaskServer;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import server.KVClient;
import server.KVServer;
import service.FileBackedTasksManager;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.LocalDateTime;


public class Main {
    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = null;
        try {
            httpTaskServer = new HttpTaskServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        TaskManager manager = Managers.getDefaultFileBackedTasksManager();
        new KVServer().start();
        try {
            KVClient client = new KVClient(new URI("http://localhost:8078"));
        } catch (URISyntaxException e){
            System.out.println("Неверный адресс URI");
            return;
        }

        Task task = new Task("Переезд", "Я буду переезжать", Status.NEW, 0, LocalDateTime.of(2023, 1, 1, 10, 00), 100);
        Epic epic1 = new Epic("Мы переезжаем", "Много задач по переезду", Status.NEW, 0, LocalDateTime.of(2023, 1, 1, 12, 0));
        SubTask subTask1 = new SubTask("Собрать вещи", "Разложить вещи в чемодан", Status.IN_PROGRESS,0, 2, LocalDateTime.of(2023, 1, 1, 15, 0),60);
        SubTask subTask2 = new SubTask("Съездить на вокзала за билетами", "Купить билеты на 15 число", Status.IN_PROGRESS,0, 2, LocalDateTime.of(2023, 2, 1, 18, 0),60);
        SubTask subTask3 = new SubTask("Подсидеть на дорожку", "Присесть на чемодан", Status.IN_PROGRESS,0, 2, LocalDateTime.of(2023, 3, 1, 20, 0),100);
        Epic epic2 = new Epic("Отпраздновать приезд", "Жестка набухаться до рыготины", Status.NEW,0, LocalDateTime.of(2023, 4, 2, 12, 0), 50);


        int idTask = manager.createTask(task);
        int idEpic1 = manager.createEpic(epic1);
        int idSubTask1 = manager.createSubTask(subTask1);
        int idSubTask2 = manager.createSubTask(subTask2);
        int idSubTask3 = manager.createSubTask(subTask3);
        int idEpic2 = manager.createEpic(epic2);

        manager.getTaskById(idTask);
        manager.getSubTaskById(idSubTask1);
        manager.getSubTaskById(idSubTask2);
        manager.getSubTaskById(idSubTask3);
        manager.getEpicById(idEpic1);
        manager.getEpicById(idEpic2);

//        manager.deleteTaskById(10);
//
//        manager.deleteEpicById(1);


        System.out.println("Список просмотров: " + manager.getHistory());
        System.out.println("Приоритет задач" + manager.getPrioritizedTasks());

        FileBackedTasksManager manager2 = FileBackedTasksManager.loadFromFile(Paths.get("testFile.csv").toFile());

        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllSubTask());
        System.out.println(manager.getAllEpic());

        System.out.println();

        System.out.println(manager2.getAllTasks());
        System.out.println(manager2.getAllSubTask());
        System.out.println(manager2.getAllEpic());

        System.out.println("Список просмотров во 2 менеджере " + manager2.getHistory());
        System.out.println("Приоритет задач" + manager2.getPrioritizedTasks());
    }
}