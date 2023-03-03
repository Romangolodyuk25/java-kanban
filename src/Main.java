import Model.Epic;
import Model.SubTask;
import Model.Task;
import Service.ManagerTask;


public class Main {
    public static void main(String[] args) {
        ManagerTask managerTask = new ManagerTask();
        Task task;
        SubTask subTask;
        Epic epic;

        task = new Task("Переезд", "Я буду переезжать", Task.STATUS_NEW);
        epic = new Epic("Мы переезжаем", "Много задач по переезду", Task.STATUS_NEW);
        subTask = new SubTask("Собрать вещи", "Разложить вещи в чемодан", Task.STATUS_IN_PROGRESS,1);
        SubTask subTask1 = new SubTask("Съездить на вокзала за билетами",
                "Купить билеты на 15 число", Task.STATUS_IN_PROGRESS,1);

        managerTask.createTask(task);
        managerTask.createEpic(epic);
        managerTask.createSubTask(subTask);
        managerTask.createSubTask(subTask1);

        System.out.println(managerTask.getAllTasks());
        System.out.println(managerTask.getAllSubTask());
        System.out.println(managerTask.getAllEpic());

        Task newUpdateTask = new Task("Переезд", "Я буду переезжать", Task.STATUS_IN_PROGRESS);
        managerTask.updateTask(newUpdateTask);

        SubTask newUpdateSubTask = new SubTask("Собрать вещи", "Разложить вещи в чемодан", Task.STATUS_DONE, 1);
        managerTask.updateSubTask(newUpdateSubTask);

        SubTask newUpdateSubTask1 = new SubTask("Съездить на вокзала за билетами","Купить билеты на 15 число",
                Task.STATUS_DONE,2);
        managerTask.updateSubTask(newUpdateSubTask1);

        System.out.println(managerTask.getAllTasks());
        System.out.println(managerTask.getAllSubTask());
        System.out.println(managerTask.getAllEpic());

        System.out.println(managerTask.getSubTaskInSpecificEpic(1));



    }
}