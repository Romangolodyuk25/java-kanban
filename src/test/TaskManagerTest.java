package test;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.TaskManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    
    
    abstract public TaskManager getTaskManager();

    @BeforeEach
    public void beforeEach(){
        getTaskManager().deleteAllTasks();
        getTaskManager().deleteAllEpic();
        getTaskManager().deleteAllSubTasks();
    }

    public Task createNewTask(){
        return new Task("Переезд", "Я буду переезжать", Status.NEW, 1, LocalDateTime.of(2023, 1, 1, 10, 00), 100);
    }

    public Epic createEpic(){
        return new Epic("Мы переезжаем", "Много задач по переезду", Status.NEW,1, LocalDateTime.of(2023, 1, 1, 12, 0));
    }

    public SubTask createSubTask(){
        return new SubTask("Собрать вещи", "Разложить вещи в чемодан", Status.NEW,1, 1, LocalDateTime.of(2023, 1, 1, 15, 0),60);
    }


    @Test
    public void shouldReturnTasks(){
        Task task1 = createNewTask();
        int task1Id = getTaskManager().createTask(task1);

        List<Task> tasks = getTaskManager().getAllTasks();

        assertNotNull(tasks, "Список задач пустой");
        assertEquals(1, tasks.size(), "Список задачи не соответствует ожидаемому");
        assertEquals(task1Id, tasks.get(0).getId(), "Неверный Идентификатор задачи");
    }

    @Test
    public void shouldReturnEmptyList(){
        List<Task> tasks = getTaskManager().getAllTasks();

        assertEquals(0, tasks.size());
    }

    @Test
    public void shouldReturnEmptyListSubTask(){
        Epic epic1 = createEpic();
        getTaskManager().createEpic(epic1);

        Epic receivedEpic = getTaskManager().getEpicById(epic1.getId());
        assertNotNull(receivedEpic);
        assertEquals(epic1, receivedEpic);

        List<SubTask> subTasks = getTaskManager().getAllSubTask();
        assertEquals(0, subTasks.size());
        assertEquals(Status.NEW, receivedEpic.getStatus());
    }

    @Test
    public void shouldReturnSubTaskListAndCheckEpicIdAndEpicStatusNew(){
        Epic epic = createEpic();
        SubTask subTask1 = createSubTask();
        getTaskManager().createEpic(epic);
        getTaskManager().createSubTask(subTask1);

        List<SubTask> subTaskList = getTaskManager().getAllSubTask();
        assertEquals(1, subTaskList.size());
        assertEquals(epic.getId(), subTask1.getIdEpic());
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void shouldReturnSubTaskListAndCheckEpicIdAndEpicStatusInProgress(){
        Epic epic = createEpic();
        SubTask subTask1 = createSubTask();
        SubTask subTask2 = createSubTask();

        getTaskManager().createEpic(epic);
        getTaskManager().createSubTask(subTask1);
        getTaskManager().createSubTask(subTask2);
        Epic receivedEpic = getTaskManager().getEpicById(epic.getId());
        assertEquals(Status.NEW, receivedEpic.getStatus());

        List<SubTask> subTaskList = getTaskManager().getAllSubTask();
        assertEquals(2, subTaskList.size());

        subTask2.setStatus(Status.IN_PROGRESS);
        getTaskManager().updateSubTask(subTask2);

        subTaskList = getTaskManager().getAllSubTask();
        assertEquals(2, subTaskList.size());
        assertEquals(epic.getId(), subTask1.getIdEpic());
        assertEquals(epic.getId(), subTask2.getIdEpic());
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }


    @Test
    public void shouldDeleteAllTasks(){
        Task task1 = createNewTask();
        int task1Id = getTaskManager().createTask(task1);

        List<Task> tasks = getTaskManager().getAllTasks();

        assertNotNull(tasks, "Список пустой");
        assertEquals(1, tasks.size(), "Список задачи не соответствует ожидаемому");

        getTaskManager().deleteAllTasks();

        List<Task> clearTasks = getTaskManager().getAllTasks();
        assertEquals(0, clearTasks.size(), "В списке лежат задачи");
    }

    @Test
    public void shouldDeleteAllSubTasksIfEpicDelete(){
        Epic epic = createEpic();
        SubTask subTask1 = createSubTask();
        getTaskManager().createEpic(epic);
        getTaskManager().createSubTask(subTask1);

        getTaskManager().deleteEpicById(epic.getId());
        assertNull(getTaskManager().getEpicById(epic.getId()), "В хранилище есть задача с такой сигнатурой");

        List<SubTask> subTasks = getTaskManager().getAllSubTask();
        assertEquals(0, subTasks.size(), "В списке лежат задачи");
    }

    @Test
    public void shouldReturnTaskById(){
        Task task1 = createNewTask();
        int task1Id = getTaskManager().createTask(task1);

        Task receivedTask = getTaskManager().getTaskById(task1Id);
        assertNotNull(receivedTask, "Задача не найдена");
        assertEquals(task1Id, receivedTask.getId(), "Айди задачи не совпадает");
    }


    @Test
    public void shouldReturnTaskByIdForNonExistentId(){
        Task task1 = createNewTask();
        Task receivedTask = getTaskManager().getTaskById(task1.getId());

        assertNull(receivedTask, "В значении лежит не null");
    }

    @Test
    public void shouldCreateTask(){
        Task task = createNewTask();

        int taskId = getTaskManager().createTask(task);
        Task savedTask = getTaskManager().getTaskById(taskId);

        assertNotNull(savedTask, "Данная задача не найдена");
        assertEquals(task, savedTask, "Разные задачи");

        List<Task> tasksList = getTaskManager().getAllTasks();

        assertNotNull(tasksList, "Список пустой");
        assertEquals(1, tasksList.size(), "Неверное количество задач");
        assertEquals(task, tasksList.get(0), "Задачи не совпадают");
    }


    @Test
    public void shouldNotDeleteTaskIfIdNotExistent(){ //сделать тест на удаление где айди 0, айди -1
        Task task = createNewTask();
        getTaskManager().createTask(task);

        List<Task> historyList = getTaskManager().getAllTasks();

        assertEquals(1, historyList.size());

        Task receivedTask = getTaskManager().getTaskById(500);
        assertNull(receivedTask);

        getTaskManager().deleteTaskById(500);
        historyList = getTaskManager().getAllTasks();
        assertEquals(1, historyList.size());
        assertEquals(task, historyList.get(0));
    }


    @Test
    public void shouldCreateSubTaskAndCheckEpicStatus(){
        Epic epic = createEpic();
        SubTask subTask1 = createSubTask();
        subTask1.setStatus(Status.DONE);

        int idEpic = getTaskManager().createEpic(epic);
        int idSubTask1 = getTaskManager().createSubTask(subTask1);

        SubTask receivedSubTask = getTaskManager().getSubTaskById(idSubTask1);
        assertNotNull(receivedSubTask);
        assertEquals(subTask1, receivedSubTask);

        List<SubTask> subTasks = getTaskManager().getAllSubTask();
        assertEquals(1, subTasks.size());
        assertEquals(idEpic, receivedSubTask.getIdEpic());
        assertEquals(Status.DONE, receivedSubTask.getStatus());
    }

    @Test
    public void shouldUpdateTask(){
        Task task1 = createNewTask();
        int task1Id = getTaskManager().createTask(task1);

        Task receivedTask = getTaskManager().getTaskById(task1Id);
        assertNotNull(receivedTask, "Задача не найдена");
        assertEquals(task1Id, receivedTask.getId(), "Айди задачи не совпадает");

        Task newTask = new Task("Приезд", "Я приехал", Status.NEW, 1, LocalDateTime.of(2023, 1, 1, 8, 00), 10);
        getTaskManager().updateTask(newTask);

        List<Task> tasks = getTaskManager().getAllTasks();
        assertEquals(1, tasks.size());

        Task receivedUpdateTask = getTaskManager().getTaskById(newTask.getId());
        assertNotNull(receivedUpdateTask, "Задача не найдена");
        assertEquals(task1Id, receivedTask.getId(), "Айди задачи не совпадает");
    }

    @Test
    public void shouldUpdateSubTask(){
        Epic epic = createEpic();
        SubTask subTask1 = createSubTask();
        subTask1.setStatus(Status.DONE);
        int idEpic = getTaskManager().createEpic(epic);
        int idSubTask1 = getTaskManager().createSubTask(subTask1);
        assertEquals(idEpic, getTaskManager().getSubTaskById(idSubTask1).getIdEpic());
        assertEquals(Status.DONE, getTaskManager().getEpicById(idEpic).getStatus());

        SubTask subTask2 = new SubTask("Разобрать вещи", "Разложить вещи", Status.IN_PROGRESS, idSubTask1, idEpic, LocalDateTime.of(2023, 1, 1, 15, 0),60);
        getTaskManager().updateSubTask(subTask2);

        assertEquals(idEpic, getTaskManager().getSubTaskById(idSubTask1).getIdEpic());
        assertEquals(Status.IN_PROGRESS, getTaskManager().getEpicById(idEpic).getStatus());
    }

    @Test
    public void shouldDeleteTaskById(){
        Task task1 = createNewTask();
        int task1Id = getTaskManager().createTask(task1);

        Task receivedTask = getTaskManager().getTaskById(task1Id);
        assertNotNull(receivedTask);
        assertEquals(task1Id, receivedTask.getId());

        getTaskManager().deleteTaskById(receivedTask.getId());

        List<Task> tasks = getTaskManager().getAllTasks();
        assertEquals(0, tasks.size());
    }

    @Test
    public void shouldNotDeleteTaskForNotExistentById(){
        Task task = createNewTask();
        getTaskManager().createTask(task);
        assertEquals(task, getTaskManager().getTaskById(task.getId()));

        assertNull(getTaskManager().getTaskById(1000));
        getTaskManager().deleteTaskById(1000);//проверка что метод выполняется без эксепшена
        assertEquals(task, getTaskManager().getTaskById(task.getId()));//приверил что таска не удалилась

    }

    @Test
    public void shouldNotDeleteSubTaskForNotExistentById(){
        Epic epic = createEpic();
        SubTask subTask = createSubTask();
        getTaskManager().createEpic(epic);
        getTaskManager().createSubTask(subTask);

        assertEquals(subTask, getTaskManager().getSubTaskById(subTask.getId()));
        assertEquals(subTask.getId(), epic.getAllListSubTaskId().get(0));

        assertNull(getTaskManager().getSubTaskById(-1));
        assertEquals(subTask, getTaskManager().getSubTaskById(subTask.getId()));

    }

    @Test
    public void shouldReturnSubTaskInSpecificEpic(){
        Epic epic = createEpic();
        SubTask subTask1 = createSubTask();
        int idEpic = getTaskManager().createEpic(epic);
        int idSubTask1 = getTaskManager().createSubTask(subTask1);

        ArrayList<SubTask> listId = getTaskManager().getSubTaskInSpecificEpic(idEpic);
        assertNotNull(listId, "Список сабтасков пустой");
        assertEquals(1, listId.size(), "Размер Сабтасков не совпадает");
        assertEquals(subTask1.getId(), listId.get(0).getId(), "Идентификатор задачи не совпадает");
        assertEquals(subTask1.getIdEpic(), listId.get(0).getIdEpic(), "Идентификатор Epic в подзадаче не совпадает");
    }

    @Test
    public void shouldNotReturnSubTaskInSpecificEpicIfSubTaskListEmpty(){
        Epic epic = new Epic("Мы переезжаем", "Много задач по переезду", Status.NEW,1);
        int idEpic = getTaskManager().createEpic(epic);

        assertEquals(0, epic.getAllListSubTaskId().size());
        assertNull(getTaskManager().getSubTaskById(1));
    }

    @Test
    public void shouldReturnHistoryMethodGet(){
        Task task1 = createNewTask();
        getTaskManager().createTask(task1);
        Task newTask =  getTaskManager().getTaskById(task1.getId());
        assertNotNull(newTask, "Данной задачи не существет");

        List<Task> tasksHistory =  getTaskManager().getHistory();
        assertEquals(1, tasksHistory.size(), "Количество историй не совпадает");

        Task receivedTask = tasksHistory.get(0);
        assertNotNull(receivedTask, "Полученное значени пустое");
        assertEquals(task1, receivedTask, "Это разные задачи");
    }

    @Test
    public void shouldAddSubTaskInEpicAndCheckEpicTime(){
        Epic epic = createEpic();
        SubTask subTask1 = createSubTask();
        SubTask subTask2 = new SubTask("Съездить на вокзала за билетами", "Купить билеты на 15 число", Status.IN_PROGRESS,0, 1, LocalDateTime.of(2023, 2, 1, 18, 0),60);
        int idEpic = getTaskManager().createEpic(epic);
        int idSubTask1 = getTaskManager().createSubTask(subTask1);
        int idSubTask2 = getTaskManager().createSubTask(subTask2);

        ArrayList<SubTask> listId = getTaskManager().getSubTaskInSpecificEpic(idEpic);
        assertNotNull(listId, "Список сабтасков пустой");
        assertEquals(2, listId.size(), "Размер Сабтасков не совпадает");
        assertEquals(subTask1.getId(), listId.get(0).getId(), "Идентификатор задачи не совпадает");
        assertEquals(subTask1.getIdEpic(), listId.get(0).getIdEpic(), "Идентификатор Epic в подзадаче не совпадает");

        Epic receivedEpic = getTaskManager().getEpicById(idEpic);
        assertEquals(receivedEpic.getStartTime(), LocalDateTime.of(2023, 1, 1, 15, 0));
        assertEquals(receivedEpic.getEndTime(), LocalDateTime.of(2023, 2, 1, 19, 0));
    }
    @Test
    public void shouldCheckEpicTimeIfEpicNotHaveSubTask(){
        Epic epic = new Epic("Мы переезжаем", "Много задач по переезду", Status.NEW,1, null);
        int idEpic = getTaskManager().createEpic(epic);
        assertEquals(1, getTaskManager().getAllEpic().size());
        Epic receivedEpic = getTaskManager().getEpicById(idEpic);
        assertEquals(0, receivedEpic.getAllListSubTaskId().size());
        assertEquals(null, receivedEpic.getStartTime());
        assertNull(receivedEpic.getEndTime());
    }

    @Test
    public void shouldCheckEpicTimeForUpdateSubTask(){
        Epic epic = createEpic();
        SubTask subTask1 = createSubTask();
        subTask1.setStartTime(LocalDateTime.of(2023, 2, 1, 15, 10));
        subTask1.setDuration(120);

        int idEpic = getTaskManager().createEpic(epic);
        int idSubTask1 = getTaskManager().createSubTask(subTask1);

        Epic receivedEpic = getTaskManager().getEpicById(idEpic);

        assertEquals(idEpic, getTaskManager().getSubTaskById(idSubTask1).getIdEpic());
        assertEquals(subTask1.getStartTime(), receivedEpic.getStartTime());
        assertEquals(LocalDateTime.of(2023, 2, 1, 17, 10), receivedEpic.getEndTime());

        SubTask updateSubtask = new SubTask("Поужинать", "Купить продукты", Status.IN_PROGRESS,2, 1, LocalDateTime.of(2023, 2, 1, 22, 0),60);
        getTaskManager().updateSubTask(updateSubtask);

        receivedEpic = getTaskManager().getEpicById(idEpic);
        assertEquals(idEpic, getTaskManager().getSubTaskById(idSubTask1).getIdEpic());
        assertEquals(updateSubtask.getStartTime(), receivedEpic.getStartTime());
        assertEquals(LocalDateTime.of(2023, 2, 1, 23, 0), receivedEpic.getEndTime());
    }

    @Test
    public void shouldNotAddIfTaskIntersect(){
        Task task = new Task("Переезд", "Я буду переезжать", Status.NEW, 1, LocalDateTime.of(2023, 1, 1, 10, 0), 100);
        SubTask subTask = new SubTask("Собрать вещи", "Разложить вещи в чемодан", Status.NEW,1, 2, LocalDateTime.of(2023, 1, 1, 10, 0),60);
        Epic epic = new Epic("Мы переезжаем", "Много задач по переезду", Status.NEW,1, LocalDateTime.of(2023, 1, 1, 12, 0));

        int idTask = getTaskManager().createTask(task);
        Task receivedTask = getTaskManager().getTaskById(idTask);
        assertEquals(1, getTaskManager().getAllTasks().size());
        assertEquals(task.getStartTime(), receivedTask.getStartTime());
        assertEquals(1, getTaskManager().getPrioritizedTasks().size());

        getTaskManager().createEpic(epic);
        int idSubTask = getTaskManager().createSubTask(subTask);
        SubTask receivedSubTask = getTaskManager().getSubTaskById(idSubTask);
        assertEquals(0, getTaskManager().getAllSubTask().size());
        assertEquals(1, getTaskManager().getPrioritizedTasks().size());
    }

    @Test
    public void shouldAddIfTaskNotIntersect(){
        Task task = new Task("Переезд", "Я буду переезжать", Status.NEW, 1, LocalDateTime.of(2023, 1, 1, 10, 0), 100);
        SubTask subTask = new SubTask("Собрать вещи", "Разложить вещи в чемодан", Status.NEW,1, 2, LocalDateTime.of(2023, 1, 1, 15, 0),60);
        Epic epic = new Epic("Мы переезжаем", "Много задач по переезду", Status.NEW,1, LocalDateTime.of(2023, 1, 1, 12, 0));

        int idTask = getTaskManager().createTask(task);
        Task receivedTask = getTaskManager().getTaskById(idTask);
        assertEquals(1, getTaskManager().getAllTasks().size());
        assertEquals(task.getStartTime(), receivedTask.getStartTime());
        assertEquals(1, getTaskManager().getPrioritizedTasks().size());

        getTaskManager().createEpic(epic);
        int idSubTask = getTaskManager().createSubTask(subTask);
        SubTask receivedSubTask = getTaskManager().getSubTaskById(idSubTask);
        assertEquals(1, getTaskManager().getAllSubTask().size());
        assertEquals(2, getTaskManager().getPrioritizedTasks().size());
    }

    @Test
    public void shouldNotUpdateIfTimeIntersect(){
        Task task = new Task("Переезд", "Я буду переезжать", Status.NEW, 1, LocalDateTime.of(2023, 1, 1, 10, 0), 100);
        SubTask subTask = new SubTask("Собрать вещи", "Разложить вещи в чемодан", Status.NEW,1, 2, LocalDateTime.of(2023, 1, 1, 15, 0),60);
        Epic epic = new Epic("Мы переезжаем", "Много задач по переезду", Status.NEW,1, LocalDateTime.of(2023, 1, 1, 12, 0));

        int idTask = getTaskManager().createTask(task);
        Task receivedTask = getTaskManager().getTaskById(idTask);
        assertEquals(1, getTaskManager().getAllTasks().size());
        assertEquals(task.getStartTime(), receivedTask.getStartTime());
        assertEquals(1, getTaskManager().getPrioritizedTasks().size());

        getTaskManager().createEpic(epic);
        int idSubTask = getTaskManager().createSubTask(subTask);

        SubTask newSubTask = new SubTask("Сесть поужинать", "Купить продукты", Status.NEW,3, 2, LocalDateTime.of(2023, 1, 1, 10, 0),60);
        getTaskManager().updateSubTask(newSubTask);

        assertEquals(subTask, getTaskManager().getSubTaskById(idSubTask));
        assertEquals(2, getTaskManager().getPrioritizedTasks().size());
        assertEquals(subTask, getTaskManager().getPrioritizedTasks().get(1));

    }

    @Test
    public void shouldUpdateIfTimeNotIntersect(){
        Task task = new Task("Переезд", "Я буду переезжать", Status.NEW, 1, LocalDateTime.of(2023, 1, 1, 10, 0), 100);
        SubTask subTask = new SubTask("Собрать вещи", "Разложить вещи в чемодан", Status.NEW,1, 2, LocalDateTime.of(2023, 1, 1, 15, 0),60);
        Epic epic = new Epic("Мы переезжаем", "Много задач по переезду", Status.NEW,1, LocalDateTime.of(2023, 1, 1, 12, 0));

        int idTask = getTaskManager().createTask(task);
        Task receivedTask = getTaskManager().getTaskById(idTask);
        assertEquals(1, getTaskManager().getAllTasks().size());
        assertEquals(task.getStartTime(), receivedTask.getStartTime());
        assertEquals(1, getTaskManager().getPrioritizedTasks().size());

        getTaskManager().createEpic(epic);
        int idSubTask = getTaskManager().createSubTask(subTask);
        assertEquals(LocalDateTime.of(2023, 1, 1, 15, 0), epic.getStartTime());
        assertEquals(LocalDateTime.of(2023, 1, 1, 16, 0), epic.getEndTime());
        SubTask receivedSubTask = getTaskManager().getSubTaskById(idSubTask);

        SubTask newSubTask = new SubTask("Сесть поужинать", "Купить продукты", Status.NEW,3, 2, LocalDateTime.of(2023, 1, 1, 20, 0),60);
        getTaskManager().updateSubTask(newSubTask);

        assertEquals(LocalDateTime.of(2023, 1, 1, 20, 0), epic.getStartTime());
        assertEquals(LocalDateTime.of(2023, 1, 1, 21, 0), epic.getEndTime());
        assertNotNull(getTaskManager().getSubTaskById(newSubTask.getId()));
        assertEquals(newSubTask, getTaskManager().getSubTaskById(idSubTask));
        assertEquals(2, getTaskManager().getPrioritizedTasks().size());
        assertEquals(newSubTask, getTaskManager().getPrioritizedTasks().get(1));
        assertEquals(LocalDateTime.of(2023, 1, 1, 20, 0), newSubTask.getStartTime());
        assertEquals(LocalDateTime.of(2023, 1, 1, 21, 0), newSubTask.getEndTime());

    }
}
