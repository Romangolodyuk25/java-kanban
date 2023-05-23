package test;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.HistoryManager;
import service.InMemoryTaskManager;
import service.Managers;
import service.TaskManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {

    private TaskManager inMemoryTaskManager;
    private HistoryManager historyManager;

    @BeforeEach
    public void beforeEach(){
        historyManager = Managers.getDefaultHistory();
        inMemoryTaskManager = Managers.getInMemoryTaskManager();
    }

    public Task createNewTask(){
        return new Task("Переезд", "Я буду переезжать", Status.NEW);
    }

    public Epic createEpic(){
        return new Epic("Мы переезжаем", "Много задач по переезду", Status.NEW,1);
    }

    public SubTask createSubTask(){
        return new SubTask("Собрать вещи", "Разложить вещи в чемодан", Status.NEW,1);
    }

    @Test
    public void shouldReturnHistoryIfHistoryListEmpty(){
        Task task = createNewTask();
        List<Task> history = historyManager.getHistory();

        assertEquals(0, history.size(), "История не пустая");
    }

    @Test
    public void shouldReturnHistory() {
        Task task = createNewTask();

        historyManager.add(task);
        List<Task> historyList = historyManager.getHistory();

        assertEquals(1, historyList.size(), "Список задач пустой");
        assertEquals(task, historyList.get(0), "Задачи не существует");
    }

    @Test
    public void shouldAddInHistory(){
        Task task = createNewTask();
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");

    }

    @Test
    public void shouldNotAddIfTasksEquals(){
        Task task = createNewTask();
        Epic epic = createEpic();
        historyManager.add(task);
        historyManager.add(task);
        historyManager.add(epic);

        assertEquals(2, historyManager.getHistory().size());
        assertNotEquals(task, historyManager.getHistory().get(1));
    }

    @Test
    public void shouldDeleteTaskInHistory(){
        Task task = createNewTask();
        Epic epic = createEpic();
        historyManager.add(task);
        historyManager.add(epic);

        assertEquals(2,historyManager.getHistory().size());
        assertEquals(task, historyManager.getHistory().get(0));

        historyManager.remove(task.getId());
        assertEquals(1,historyManager.getHistory().size());
        assertNotEquals(task, historyManager.getHistory().get(0));

    }

    @Test
    public void shouldDeleteFirstElement(){
        Task task = createNewTask();
        Epic epic = createEpic();
        historyManager.add(task);
        historyManager.add(epic);

        assertEquals(2,historyManager.getHistory().size());
        assertEquals(task, historyManager.getHistory().get(0));

        historyManager.remove(0);
        assertEquals(1,historyManager.getHistory().size());

        assertNotEquals(task, historyManager.getHistory().get(0));
        assertEquals(epic, historyManager.getHistory().get(0));

    }

    @Test
    public void shouldDeleteLastElement() {
        Task task = createNewTask();
        Epic epic = createEpic();
        SubTask subTask = createSubTask();
        subTask.setId(2);
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subTask);

        List<Task> historyList = historyManager.getHistory();

        assertEquals(3,historyList.size());
        assertEquals(subTask, historyList.get(historyList.size()-1));

        historyManager.remove(subTask.getId());
        historyList = historyManager.getHistory();
        assertEquals(2,historyList.size());
        assertEquals(epic, historyList.get(historyList.size()-1));
    }
    @Test
    public void shouldDeleteMiddleElement() {
        Task task = createNewTask();
        Epic epic = createEpic();
        SubTask subTask = createSubTask();
        subTask.setId(2);
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subTask);

        List<Task> historyList = historyManager.getHistory();

        assertEquals(3,historyList.size());
        assertEquals(epic, historyList.get(1));

        historyManager.remove(epic.getId());
        historyList = historyManager.getHistory();

        assertNotEquals(epic, historyList.get(1));
        assertEquals(subTask, historyList.get(1));
    }
}
