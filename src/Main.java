import Model.Epic;
import Model.Status;
import Model.SubTask;
import Model.Task;
import Service.Managers;
import Service.TaskManager;


public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
                
        Task task;
        SubTask subTask;
        Epic epic;

        task = new Task("Переезд", "Я буду переезжать", Status.NEW);
        epic = new Epic("Мы переезжаем", "Много задач по переезду", Status.NEW);
        subTask = new SubTask("Собрать вещи", "Разложить вещи в чемодан", Status.IN_PROGRESS,1);
        SubTask subTask1 = new SubTask("Съездить на вокзала за билетами",
                "Купить билеты на 15 число", Status.IN_PROGRESS,1);

        manager.createTask(task);
        manager.createEpic(epic);
        manager.createSubTask(subTask);
        manager.createSubTask(subTask1);


        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllSubTask());
        System.out.println(manager.getAllEpic());

        Task newUpdateTask = new Task("Переезд", "Я буду переезжать", Status.IN_PROGRESS);
        manager.updateTask(newUpdateTask);

        SubTask newUpdateSubTask = new SubTask("Собрать вещи", "Разложить вещи в чемодан", Status.NEW, 1);
        manager.updateSubTask(newUpdateSubTask);

        SubTask newUpdateSubTask1 = new SubTask("Съездить на вокзала за билетами","Купить билеты на 15 число",
                Status.NEW,2);
        manager.updateSubTask(newUpdateSubTask1);

        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllSubTask());
        System.out.println(manager.getAllEpic());

        System.out.println(manager.getSubTaskInSpecificEpic(1));

        manager.getTaskById(3);
        manager.getSubTaskById(1);
        manager.getEpicById(1);
        System.out.println("Список просмотров: " + manager.getHistory());

        //создал новый интерфейс и новый класс
        //Заимплементил классу InMemoryTaskManager 2 интерфейс HistoryManager переопределил в нем методо Add
    }
}