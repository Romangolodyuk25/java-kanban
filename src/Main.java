import Model.Epic;
import Model.Status;
import Model.SubTask;
import Model.Task;
import Service.Managers;


public class Main {
    public static void main(String[] args) {
        Managers managers = new Managers();
        Task task;
        SubTask subTask;
        Epic epic;

        task = new Task("Переезд", "Я буду переезжать", Status.NEW);
        epic = new Epic("Мы переезжаем", "Много задач по переезду", Status.NEW);
        subTask = new SubTask("Собрать вещи", "Разложить вещи в чемодан", Status.IN_PROGRESS,1);
        SubTask subTask1 = new SubTask("Съездить на вокзала за билетами",
                "Купить билеты на 15 число", Status.IN_PROGRESS,1);

        managers.getDefault().createTask(task);
        managers.getDefault().createEpic(epic);
        managers.getDefault().createSubTask(subTask);
        managers.getDefault().createSubTask(subTask1);

        System.out.println(managers.getDefault().getAllTasks());
        System.out.println(managers.getDefault().getAllSubTask());
        System.out.println(managers.getDefault().getAllEpic());

        Task newUpdateTask = new Task("Переезд", "Я буду переезжать", Status.IN_PROGRESS);
        managers.getDefault().updateTask(newUpdateTask);

        SubTask newUpdateSubTask = new SubTask("Собрать вещи", "Разложить вещи в чемодан", Status.NEW, 1);
        managers.getDefault().updateSubTask(newUpdateSubTask);

        SubTask newUpdateSubTask1 = new SubTask("Съездить на вокзала за билетами","Купить билеты на 15 число",
                Status.NEW,2);
        managers.getDefault().updateSubTask(newUpdateSubTask1);

        System.out.println(managers.getDefault().getAllTasks());
        System.out.println(managers.getDefault().getAllSubTask());
        System.out.println(managers.getDefault().getAllEpic());

        System.out.println(managers.getDefault().getSubTaskInSpecificEpic(1));

        managers.getDefault().getTaskById(1);
        managers.getDefault().getSubTaskById(1);
        managers.getDefault().getEpicById(1);
        System.out.println("Список просмотров: " + managers.getDefault().getHistory());

        //создал новый интерфейс и новый класс
        //Заимплементил классу InMemoryTaskManager 2 интерфейс HistoryManager переопределил в нем методо Add
    }
}