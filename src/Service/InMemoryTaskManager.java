package Service;

import Model.Epic;
import Model.SubTask;
import Model.Task;
import Model.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class InMemoryTaskManager implements TaskManager {

    public HashMap<Integer, Task> taskStorage;
    public HashMap<Integer, SubTask> subTaskStorage;
    public HashMap<Integer, Epic> epicStorage;
    public InMemoryHistoryManager inMemoryHistoryManager;

    public InMemoryTaskManager(){
        taskStorage = new HashMap<>();
        subTaskStorage = new HashMap<>();
        epicStorage = new HashMap<>();
        inMemoryHistoryManager = new InMemoryHistoryManager();
    }

    int taskId = 1;
    int subTaskId = 1;
    int epicTaskId = 1;


    // ПОЛУЧЕНИЕ ВСЕХ ОБЪЕКТОВ
    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<Task>(taskStorage.values());
    }


    @Override
    public ArrayList<SubTask> getAllSubTask() {
        return new ArrayList<SubTask>(subTaskStorage.values());
    }

    @Override
    public ArrayList<Epic> getAllEpic() {
        return new ArrayList<Epic>(epicStorage.values());
    }

    // УДАЛЕНИЕ ВСЕХ ОБЪЕКТОВ
    @Override
    public void deleteAllTasks() {
        taskStorage.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        for (Epic epic : epicStorage.values()) {
            epic.clearListSubTaskId();
        }
        subTaskStorage.clear();
        // при удалении саб тасков нужно отчистить у эпиков сабтаски(так как я буду хранить теперь там айди то айдишники стереть)
    }

    @Override
    public void deleteAllEpic() {
        epicStorage.clear();
        subTaskStorage.clear();// при удалении эпика будут стираться все SubTask без эпика нет SubTask
    }

    // ПОЛУЧЕНИЕ ОБЪЕКТОВ ПО ID
    @Override
    public Task getTaskById(int id) {
        inMemoryHistoryManager.add(taskStorage.get(id));
        return taskStorage.get(id);
    }

    @Override
    public SubTask getSubTaskById(int id) {
       inMemoryHistoryManager.add(subTaskStorage.get(id));
        return subTaskStorage.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        inMemoryHistoryManager.add(epicStorage.get(id));
        return epicStorage.get(id);
    }

    //СОЗДАНИЕ ОБЪЕКТОВ.
    @Override
    public void createTask(Task newTask) {
        newTask.setId(taskId);
        taskStorage.put(taskId, newTask);
        taskId++;
    }

    @Override
    public void createSubTask(SubTask newSubTask) {//int epicId хранится в самой подзадаче
        newSubTask.setId(subTaskId);
        Epic epicName;
        if (epicStorage.containsKey(newSubTask.getIdEpic())) {
            epicName = epicStorage.get(newSubTask.getIdEpic());
            epicName.addListSubTaskId(newSubTask.getId());
            subTaskStorage.put(subTaskId, newSubTask);
            subTaskId++;
        } else {
            System.out.println("Подзадача не может существовать без эпика");
        }
    }

    @Override
    public void createEpic(Epic newEpic) {
        newEpic.setId(epicTaskId);
        epicStorage.put(epicTaskId, newEpic);
        epicTaskId++;
    }

    //ОБНОВЛЕНИЕ

    @Override
    public void updateTask(Task task) {
        taskStorage.put(task.getId(), task);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTaskStorage.containsKey(subTask.getId()) && epicStorage.containsKey(subTask.getIdEpic())) {//проверил айди в SubTask и айди Epic
            subTaskStorage.put(subTask.getId(), subTask);
            Epic newEpic = epicStorage.get(subTask.getIdEpic()); //достал епик указанный в SubTask
            updateStatusInEpic(newEpic); //сделал расчет статуса
        } else {
            System.out.println("Данного id для subTask не существует");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        //не использую epicStorage.put(pic.getId(), epic) потому что могу затереть поля (например SubTaskListId)
        Epic savedEpic = epicStorage.get(epic.getId());
        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
    }

    //УДАЛЕНИЕ ПО ID
    @Override
    public void deleteTaskById(int id) {
        taskStorage.remove(id);
    }

    @Override
    public void deleteSubTaskById(int id) {
        int idCountEpic = subTaskStorage.get(id).getIdEpic();
        epicStorage.get(idCountEpic).removeSubTuskId(id);
        updateStatusInEpic(epicStorage.get(idCountEpic));
        subTaskStorage.remove(id);

        // после удаления саб таски, ее нужно удалить из определенного эпика
    }

    @Override
    public void deleteEpicById(int id) {
        ArrayList<Integer> listSubTaskId = epicStorage.get(id).getAllListSubTaskId();
        epicStorage.remove(id);
        for (Integer i : listSubTaskId) {
            subTaskStorage.remove(i);
        }
        // если удаляется эпик, то удаляются все саб таски
    }

    @Override
    public LinkedList<Task> getHistory() {
        return InMemoryHistoryManager.historyList;
    }

    @Override
    public ArrayList<SubTask> getSubTaskInSpecificEpic(int idEpic) {
        ArrayList<SubTask> subTasks = new ArrayList<>();
        Epic epic = epicStorage.get(idEpic);
        if (epic == null) {
            return null;
        }
        for (Integer id : epic.getAllListSubTaskId()) {
            subTasks.add(subTaskStorage.get(id));
        }
        return subTasks;
    }

    @Override
    public Epic updateStatusInEpic(Epic epic) {
        int subTasksStatusInProgressOrDone = 0;
        int subTasksStatusIsNewOrProgress = 0;

        for (Integer i : epic.getAllListSubTaskId()) {
            if (subTaskStorage.get(i).getStatus().equals(Status.DONE) ||
                    subTaskStorage.get(i).getStatus().equals(Status.IN_PROGRESS)) {
                subTasksStatusInProgressOrDone++;
            }
        }
        for (Integer j : epic.getAllListSubTaskId()) {
            if (subTaskStorage.get(j).getStatus().equals(Status.NEW) ||
                    subTaskStorage.get(j).getStatus().equals(Status.IN_PROGRESS)) {
                subTasksStatusIsNewOrProgress++;
            }
        }
        if (epic.getAllListSubTaskId().isEmpty() && subTasksStatusInProgressOrDone == 0) {
            epic.setStatus(Status.NEW);
        } else if (subTasksStatusIsNewOrProgress == 0) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
        return epic;
    }
}
