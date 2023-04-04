package service;

import model.Epic;
import model.SubTask;
import model.Task;
import model.Status;

import java.security.spec.ECPoint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    private final HashMap<Integer, Task> taskStorage;
    private final HashMap<Integer, SubTask> subTaskStorage;
    private final HashMap<Integer, Epic> epicStorage;
    public HistoryManager inMemoryHistoryManager;

    public InMemoryTaskManager(){
        taskStorage = new HashMap<>();
        subTaskStorage = new HashMap<>();
        epicStorage = new HashMap<>();
        inMemoryHistoryManager = Managers.getDefaultHistory();
        // 23 строка - HistoryManager inMemoryHistoryManager = new InMemoryHistoryManager()
    }

    int taskId = 10;
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
        for(Task task : taskStorage.values()){
            int id = task.getId();
            inMemoryHistoryManager.remove(id);
        }
        taskStorage.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        for(SubTask subTask : subTaskStorage.values()){
            int id = subTask.getId();
            inMemoryHistoryManager.remove(id);
        }
        for (Epic epic : epicStorage.values()) {
            epic.clearListSubTaskId();
        }
        subTaskStorage.clear();
        // при удалении саб тасков нужно отчистить у эпиков сабтаски(так как я буду хранить теперь там айди то айдишники стереть)
    }

    @Override
    public void deleteAllEpic() {
        for(Epic epic : epicStorage.values()){
            int id = epic.getId();
            for (Integer i : epic.getAllListSubTaskId()) {
               inMemoryHistoryManager.remove(i);
            }
            inMemoryHistoryManager.remove(id);
        }
        epicStorage.clear();
        subTaskStorage.clear();// при удалении эпика будут стираться все SubTask без эпика нет SubTask
    }

    // ПОЛУЧЕНИЕ ОБЪЕКТОВ ПО ID
    @Override
    public Task getTaskById(int id) {
        final Task task = taskStorage.get(id);
        inMemoryHistoryManager.add(task);
        return task;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        final SubTask subTask = subTaskStorage.get(id);
        inMemoryHistoryManager.add(subTask);
        return subTask;

    }

    @Override
    public Epic getEpicById(int id) {
        final Epic epic = epicStorage.get(id);
        inMemoryHistoryManager.add(epic);
        return epic;
    }

    //СОЗДАНИЕ ОБЪЕКТОВ.
    @Override
    public int createTask(Task newTask) {
        newTask.setId(taskId);
        taskStorage.put(taskId, newTask);
        taskId++;
        return taskId - 1;
    }

    @Override
    public int createSubTask(SubTask newSubTask) {//int epicId хранится в самой подзадаче
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
        return subTaskId - 1;
    }

    @Override
    public int createEpic(Epic newEpic) {
        newEpic.setId(epicTaskId);
        epicStorage.put(epicTaskId, newEpic);
        epicTaskId++;
        return epicTaskId - 1;
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
        inMemoryHistoryManager.remove(id);
    }

    @Override
    public void deleteSubTaskById(int id) {
        int idCountEpic = subTaskStorage.get(id).getIdEpic();
        epicStorage.get(idCountEpic).removeSubTuskId(id);
        updateStatusInEpic(epicStorage.get(idCountEpic));
        subTaskStorage.remove(id);
        inMemoryHistoryManager.remove(id);
        // после удаления саб таски, ее нужно удалить из определенного эпика
    }

    @Override
    public void deleteEpicById(int id) {
        ArrayList<Integer> listSubTaskId = epicStorage.get(id).getAllListSubTaskId();
        epicStorage.remove(id);
        for (Integer i : listSubTaskId) {
            subTaskStorage.remove(i);
            inMemoryHistoryManager.remove(i);
        }
        // если удаляется эпик, то удаляются все саб таски
        inMemoryHistoryManager.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return inMemoryHistoryManager.getHistory();
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
