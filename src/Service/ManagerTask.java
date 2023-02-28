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
    public HashMap<Integer, Task> getAllTasks() {
        return taskStorage;
    }

    public HashMap<Integer, SubTask> getAllSubTask() {
        return subTaskStorage;
    }

    public HashMap<Integer, Epic> getAllEpic() {
        return epicStorage;
    }

    // УДАЛЕНИЕ ВСЕХ ОБЪЕКТОВ
    public HashMap<Integer, Task> deleteAllTasks() {
        taskStorage.clear();
        return taskStorage;
    }

    public HashMap<Integer, SubTask> deleteAllSubTasks() {
        subTaskStorage.clear();
        return subTaskStorage;
    }

    public HashMap<Integer, Epic> deleteAllEpic() {
        epicStorage.clear();
        return epicStorage;
    }

    // ПОЛУЧЕНИЕ ОБЪЕКТОВ ПО ID
    public Task getTaskForId(int id) {
        return taskStorage.get(id);
    }

    public SubTask getSubTaskForId(int id) {
        return subTaskStorage.get(id);
    }

    public Epic getEpicForId(int id) {
        return epicStorage.get(id);
    }

    //СОЗДАНИЕ ОБЪЕКТОВ.
    public void createTask(Task newTask) {// приве
        newTask.setId(taskId);
        taskStorage.put(taskId, newTask);
        taskId++;
    }

    public void createSubTask(SubTask newSubTask, int epicId) {
        newSubTask.setId(subTaskId);
        Epic epicName = epicStorage.get(epicId);
        epicName.subTaskList.add(newSubTask);
        newSubTask.epic = epicName;
        subTaskStorage.put(subTaskId, newSubTask);
        subTaskId++;
    }

    public void createEpic(Epic newEpic) {
        newEpic.setId(epicTaskId);
        epicStorage.put(epicTaskId, newEpic);
        epicTaskId++;
    }

    //ОБНОВЛЕНИЕ
    public void updateTask(Task task) {
        taskStorage.put(task.getId(), task);//передал новый объеект со статусом внутри
    }

    public void updateSubTask(SubTask subTask) {
        Epic newEpic = subTaskStorage.get(subTask.getId()).epic;//зайти в епик найти по айди старый сабтаск удалить его и добавить новый
        subTask.epic = newEpic;
        subTaskStorage.put(subTask.getId(), subTask);
        int subTasksNewOrInProgress = 0;
        for (SubTask i : subTask.epic.subTaskList) {
            if (i.getStatus().equals(Task.STATUS_NEW) || i.getStatus().equals(Task.STATUS_IN_PROGRESS)) {
                subTasksNewOrInProgress++;
            }
        }
        if (subTasksNewOrInProgress == 0) {
            subTask.epic.setStatus(Task.STATUS_DONE);
        } else {
            subTask.epic.setStatus(Task.STATUS_IN_PROGRESS);
        }
    }

    public void updateEpic(Epic epic) {
        epicStorage.put(epic.getId(), epic);
        boolean statusIsNew = true;
        boolean statusIsDone = false;
        for (SubTask i : epic.subTaskList) {
            if (i.getStatus().equals(Task.STATUS_NEW)) {
                continue;
            }
            statusIsNew = false;
        }
        for (SubTask j : epic.subTaskList) {
            if (j.getStatus().equals(Task.STATUS_DONE)) {
                statusIsDone = true;
            } else {
                statusIsDone = false;
            }
        }
        if (epic.subTaskList.isEmpty() && statusIsNew) {
            epic.setStatus(Task.STATUS_NEW);
        } else if (statusIsDone) {
            epic.setStatus(Task.STATUS_DONE);
        } else {
            epic.setStatus(Task.STATUS_IN_PROGRESS);
        }
    }

    //УДАЛЕНИЕ ПО ID
    public void deleteForIdTask(int id) {
        taskStorage.remove(id);
    }

    public void deleteFromIdSubTask(int id) {
        subTaskStorage.remove(id);
    }

    public void deleteFromIdEpic(int id) {
        epicStorage.remove(id);
    }

    public ArrayList<SubTask> getSubTaskInSpecificEpic(int idEpic) {
        Epic countEpic = epicStorage.get(idEpic);
        return countEpic.subTaskList;
    }


}
