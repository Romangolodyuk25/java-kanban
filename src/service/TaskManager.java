package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
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
    int createTask(Task newTask);

    int createSubTask(SubTask newSubTask);

    int createEpic(Epic newEpic);

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
