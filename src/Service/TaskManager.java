package Service;

import Model.Epic;
import Model.SubTask;
import Model.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public interface TaskManager {// сделать интерфейсом перенести все в InMemory

    // ПОЛУЧЕНИЕ ВСЕХ ОБЪЕКТОВ
    ArrayList<Task> getAllTasks();

    ArrayList<SubTask> getAllSubTask();

    ArrayList<Epic> getAllEpic();

    // УДАЛЕНИЕ ВСЕХ ОБЪЕКТОВ
    void deleteAllTasks();

    void deleteAllSubTasks();

    void deleteAllEpic();

    // ПОЛУЧЕНИЕ ОБЪЕКТОВ ПО ID
    Task getTaskById(int id);

    SubTask getSubTaskById(int id);

    Epic getEpicById(int id);

    //СОЗДАНИЕ ОБЪЕКТОВ.
    void createTask(Task newTask);

    void createSubTask(SubTask newSubTask);

    void createEpic(Epic newEpic);

    //ОБНОВЛЕНИЕ
    void updateTask(Task task);

    void updateSubTask(SubTask subTask);

    void updateEpic(Epic epic);

    //УДАЛЕНИЕ ПО ID
    void deleteTaskById(int id);

    void deleteSubTaskById(int id);

    void deleteEpicById(int id);

    ArrayList<SubTask> getSubTaskInSpecificEpic(int idEpic);

    Epic updateStatusInEpic(Epic epic);

    List<Task> getHistory();
}
