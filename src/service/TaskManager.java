package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface TaskManager {// сделать интерфейсом перенести все в InMemory

    // ПОЛУЧЕНИЕ ВСЕХ ОБЪЕКТОВ
    ArrayList<Task> getAllTasks();

    ArrayList<SubTask> getAllSubTask();

    ArrayList<Epic> getAllEpic();

    // УДАЛЕНИЕ ВСЕХ ОБЪЕКТОВ
    void deleteAllTasks() throws IOException;

    void deleteAllSubTasks() throws IOException;

    void deleteAllEpic() throws IOException;

    // ПОЛУЧЕНИЕ ОБЪЕКТОВ ПО ID
    Task getTaskById(int id) throws IOException;

    SubTask getSubTaskById(int id) throws IOException;

    Epic getEpicById(int id) throws IOException;

    //СОЗДАНИЕ ОБЪЕКТОВ.
    int createTask(Task newTask) throws IOException;

    int createSubTask(SubTask newSubTask) throws IOException;

    int createEpic(Epic newEpic) throws IOException;

    //ОБНОВЛЕНИЕ
    void updateTask(Task task) throws IOException;

    void updateSubTask(SubTask subTask) throws IOException;

    void updateEpic(Epic epic) throws IOException;

    //УДАЛЕНИЕ ПО ID
    void deleteTaskById(int id) throws IOException;

    void deleteSubTaskById(int id) throws IOException;

    void deleteEpicById(int id) throws IOException;

    ArrayList<SubTask> getSubTaskInSpecificEpic(int idEpic);

    Epic updateStatusInEpic(Epic epic) throws IOException;

    List<Task> getHistory();
}
