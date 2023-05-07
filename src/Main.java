import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.FileBackedTasksManager;
import service.Managers;
import service.TaskManager;

import java.nio.file.Paths;


public class Main {
    public static void main(String[] args){
        TaskManager manager = Managers.getDefaultFileBackedTasksManager();

        Task task = new Task("Переезд", "Я буду переезжать", Status.NEW);
        Epic epic1 = new Epic("Мы переезжаем", "Много задач по переезду", Status.NEW);
        SubTask subTask1 = new SubTask("Собрать вещи", "Разложить вещи в чемодан", Status.IN_PROGRESS,2);
        SubTask subTask2 = new SubTask("Съездить на вокзала за билетами", "Купить билеты на 15 число", Status.IN_PROGRESS,2);
        SubTask subTask3 = new SubTask("Подсидеть на дорожку", "Присесть на чемодан", Status.IN_PROGRESS,2);
        Epic epic2 = new Epic("Отпраздновать приезд", "Жестка набухаться до рыготины", Status.NEW);


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