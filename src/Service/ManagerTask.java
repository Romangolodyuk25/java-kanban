package Service;

import Model.Epic;
import Model.SubTask;
import Model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class ManagerTask {
    public HashMap<Integer, Task> taskStorage = new HashMap<>();
    public HashMap<Integer, SubTask> subTaskStorage = new HashMap<>();
    public HashMap<Integer, Epic> epicStorage = new HashMap<>();

    int taskId = 1;
    int subTaskId = 1;
    int epicTaskId = 1;


    // ПОЛУЧЕНИЕ ВСЕХ ОБЪЕКТОВ
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<Task>(taskStorage.values());
    }

    public ArrayList<SubTask> getAllSubTask() {
        return new ArrayList<SubTask>(subTaskStorage.values());
    }

    public ArrayList<Epic> getAllEpic() {
        return new ArrayList<Epic>(epicStorage.values());
    }

    // УДАЛЕНИЕ ВСЕХ ОБЪЕКТОВ
    public void deleteAllTasks() {
        taskStorage.clear();
    }

    public void deleteAllSubTasks() {
        for (Epic epic : epicStorage.values()){
            epic.clearListSubTaskId();
        }
        subTaskStorage.clear();
        // при удалении саб тасков нужно отчистить у эпиков сабтаски(так как я буду хранить теперь там айди то айдишники стереть)
    }

    public void deleteAllEpic() {
        epicStorage.clear();
        subTaskStorage.clear();// при удалении эпика будут стираться все SubTask без эпика нет SubTask
    }

    // ПОЛУЧЕНИЕ ОБЪЕКТОВ ПО ID
    public Task getTaskById(int id) {
        return taskStorage.get(id);
    }

    public SubTask getSubTaskById(int id) {
        return subTaskStorage.get(id);
    }

    public Epic getEpicById(int id) {
        return epicStorage.get(id);
    }

    //СОЗДАНИЕ ОБЪЕКТОВ.
    public void createTask(Task newTask) {
        newTask.setId(taskId);
        taskStorage.put(taskId, newTask);
        taskId++;
    }

        public void createSubTask(SubTask newSubTask) {//int epicId хранится в самой подзадаче
        newSubTask.setId(subTaskId);
        Epic epicName;
        if(epicStorage.containsKey(newSubTask.getIdEpic())) {
            epicName = epicStorage.get(newSubTask.getIdEpic());
            epicName.addListSubTaskId(newSubTask.getId());
            subTaskStorage.put(subTaskId, newSubTask);
            subTaskId++;
        } else {
            System.out.println("Подзадача не может существовать без эпика");
        }
    }

    public void createEpic(Epic newEpic) {
        newEpic.setId(epicTaskId);
        epicStorage.put(epicTaskId, newEpic);
        epicTaskId++;
    }

    //ОБНОВЛЕНИЕ
    public void updateTask(Task task) {
        taskStorage.put(task.getId(), task);
    }

    public void updateSubTask(SubTask subTask) {
        if (subTaskStorage.containsKey(subTask.getId()) && epicStorage.containsKey(subTask.getIdEpic())) {//проверил айди в SubTask и айди Epic
            subTaskStorage.put(subTask.getId(), subTask);
            Epic newEpic = epicStorage.get(subTask.getIdEpic()); //достал епик указанный в SubTask
            updateStatusInEpic(newEpic); //сделал расчет статуса
        } else {
            System.out.println("Данного id для subTask не существует");
        }
    }

    public void updateEpic(Epic epic) {
        //не использую epicStorage.put(pic.getId(), epic) потому что могу затереть поля (например SubTaskListId)
        Epic savedEpic = epicStorage.get(epic.getId());
        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
    }

    //УДАЛЕНИЕ ПО ID
    public void deleteTaskById(int id) {
        taskStorage.remove(id);
    }

    public void deleteSubTaskById(int id) {
        int idCountEpic = subTaskStorage.get(id).getIdEpic();
        epicStorage.get(idCountEpic).removeSubTuskId(id);
        updateStatusInEpic(epicStorage.get(idCountEpic));
        subTaskStorage.remove(id);

        // после удаления саб таски, ее нужно удалить из определенного эпика
    }

    public void deleteEpicById(int id) {
        ArrayList<Integer> listSubTaskId = epicStorage.get(id).getAllListSubTaskId();
        epicStorage.remove(id);
        for (Integer i : listSubTaskId) {
            subTaskStorage.remove(i);
        }
        // если удаляется эпик, то удаляются все саб таски
    }

    public ArrayList<SubTask> getSubTaskInSpecificEpic(int idEpic) {
        ArrayList<SubTask> subTasks = new ArrayList<>();
        Epic epic = epicStorage.get(idEpic);
        if (epic == null) {
            return null;
        }
        for (Integer id : epic.getAllListSubTaskId()){
            subTasks.add(subTaskStorage.get(id));
        }
        return subTasks;
    }

    public Epic updateStatusInEpic(Epic epic){
        int subTasksStatusInProgressOrDone = 0;
        int subTasksStatusIsNewOrProgress = 0;

        for (Integer i : epic.getAllListSubTaskId()){
            if(subTaskStorage.get(i).getStatus().equals(Task.STATUS_DONE) ||
                    subTaskStorage.get(i).getStatus().equals(Task.STATUS_IN_PROGRESS)) {
                subTasksStatusInProgressOrDone++;
            }
        }
        for (Integer j : epic.getAllListSubTaskId()){
            if(subTaskStorage.get(j).getStatus().equals(Task.STATUS_NEW) ||
                    subTaskStorage.get(j).getStatus().equals(Task.STATUS_IN_PROGRESS)){
                subTasksStatusIsNewOrProgress++;
            }
        }
        if (epic.getAllListSubTaskId().isEmpty() && subTasksStatusInProgressOrDone==0) {
            epic.setStatus(Task.STATUS_NEW);
        } else if (subTasksStatusIsNewOrProgress==0) {
            epic.setStatus(Task.STATUS_DONE);
        } else {
            epic.setStatus(Task.STATUS_IN_PROGRESS);
        }
        return epic;
    }

}
