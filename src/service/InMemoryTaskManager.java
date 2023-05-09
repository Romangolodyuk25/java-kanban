package service;

import model.Epic;
import model.SubTask;
import model.Task;
import model.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected final HashMap<Integer, Task> taskStorage;
    protected final HashMap<Integer, SubTask> subTaskStorage;
    protected final HashMap<Integer, Epic> epicStorage;
    protected final HistoryManager inMemoryHistoryManager;
    protected TreeSet<Task> prioritizedTask;
    protected int id = 1;


    public InMemoryTaskManager() {
        taskStorage = new HashMap<>();
        subTaskStorage = new HashMap<>();
        epicStorage = new HashMap<>();
        inMemoryHistoryManager = Managers.getDefaultHistory();
        prioritizedTask = new TreeSet<>(
                Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
        );
    }

    protected void calculateTime(Epic epic){
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        int duration = 0;
        if (epic.getAllListSubTaskId().size() == 0) {
            System.out.println("У данного эпика при расчете времени нет подзадач");
            epic.setStartTime(null);// нужно сбросить поля(если нет подзадачи то и времени у эпика нет)
            epic.setEndTime(null);
            epic.setDuration(0);
            return;
        } else {
            startTime = subTaskStorage.get(epic.getAllListSubTaskId().get(0)).getStartTime();
            endTime = subTaskStorage.get(epic.getAllListSubTaskId().get(0)).getStartTime();
        }
        for(Integer subtaskId : epic.getAllListSubTaskId()) {
            SubTask receivedSub = subTaskStorage.get(subtaskId);
            if(receivedSub.getStartTime().isBefore(startTime)){
                startTime = receivedSub.getStartTime();
            }
            if (receivedSub.getEndTime().isAfter(endTime)) {
                endTime = receivedSub.getEndTime();
            }
            duration += receivedSub.getDuration();
        }
        epic.setStartTime(startTime);
        epic.setEndTime(endTime);
        epic.setDuration(duration);
        epicStorage.put(epic.getId(), epic);
    }

    private boolean addAndCheckIntersection(Task task) {
        final LocalDateTime startTime = task.getStartTime();
        final LocalDateTime endTime = task.getEndTime();
        boolean isIntersect = false;// вернет true если задачи пересекаются
        for (Task t : prioritizedTask) {
            final LocalDateTime existStart = t.getStartTime();
            final LocalDateTime existEnd = t.getEndTime();
            if (endTime.isAfter(existStart) && endTime.isBefore(existEnd)) {// newTimeEnd <= existTimeStart
                isIntersect = true;
            }
            if (startTime.isAfter(existStart) && startTime.isBefore(existEnd)) {// newTimeEnd <= existTimeStart
                isIntersect = true;
            }
        }
        return isIntersect;
    }

    @Override
    public List<Task> getPrioritizedTasks(){
        return new ArrayList<>(prioritizedTask);
    }

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
        for (Task task : taskStorage.values()) {
            int id = task.getId();
            Task taskForDelete = taskStorage.get(id);

            inMemoryHistoryManager.remove(id);
            prioritizedTask.remove(taskForDelete);
        }
        taskStorage.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        for (SubTask subTask : subTaskStorage.values()) {
            int id = subTask.getId();
            inMemoryHistoryManager.remove(id);

            SubTask subtaskForDelete = subTaskStorage.get(id);
            prioritizedTask.remove(subtaskForDelete);
        }
        for (Epic epic : epicStorage.values()) {
            epic.clearListSubTaskId();
        }
        subTaskStorage.clear();
        // при удалении саб тасков нужно отчистить у эпиков сабтаски(так как я буду хранить теперь там айди то айдишники стереть)
    }

    @Override
    public void deleteAllEpic() {
        for (Epic epic : epicStorage.values()) {
            int id = epic.getId();

            for (Integer i : epic.getAllListSubTaskId()) {
                inMemoryHistoryManager.remove(i);

                SubTask subTaskForDelete = subTaskStorage.get(id);
                prioritizedTask.remove(subTaskForDelete);
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
        newTask.setId(id);
        boolean intersect = addAndCheckIntersection(newTask);
        if(!intersect){
            taskStorage.put(id, newTask);
            prioritizedTask.add(newTask);
            id++;
        } else {
            System.out.println("Задача не может быть добавленна из-за пересечения");
        }
         //При создании задач и подзадачи нужно сделать проверку на пересечение по времени
        return id - 1;
    }

    @Override
    public int createSubTask(SubTask newSubTask) {//int epicId хранится в самой подзадаче
        newSubTask.setId(id);
        Epic epicName;
        if (epicStorage.containsKey(newSubTask.getIdEpic())) {
            boolean intersect = addAndCheckIntersection(newSubTask);
            if(!intersect) {
                epicName = epicStorage.get(newSubTask.getIdEpic());
                epicName.addListSubTaskId(newSubTask.getId());
                subTaskStorage.put(id, newSubTask);
                prioritizedTask.add(newSubTask);
                updateStatusInEpic(epicName);
                calculateTime(epicName);
                id++;
            }
            //При создании задач и подзадачи нужно сделать проверку на пересечение по времени
        } else {
            System.out.println("Подзадача не может существовать без эпика");
        }
        return id - 1;
    }

    @Override
    public int createEpic(Epic newEpic) {
        newEpic.setId(id);
        epicStorage.put(id, newEpic);
        id++;
        return id - 1;
    }

    //ОБНОВЛЕНИЕ

    @Override
    public void updateTask(Task task) {
        boolean intersect = addAndCheckIntersection(task);
        if(!intersect) {
            taskStorage.put(task.getId(), task);
            prioritizedTask.remove(taskStorage.get(task.getId()));
            prioritizedTask.add(task);
        } else {
            System.out.println("Задаче не может быть обновлена из-за пересечения по времени");
        }
        // Удаляю таску из дерева по айди и потом добавляю новую
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTaskStorage.containsKey(subTask.getId()) && epicStorage.containsKey(subTask.getIdEpic())) {//проверил айди в SubTask и айди Epic
            boolean intersect = addAndCheckIntersection(subTask);
            if (!intersect) {
                subTaskStorage.put(subTask.getId(), subTask);
                Epic newEpic = epicStorage.get(subTask.getIdEpic()); //достал епик указанный в SubTask
                updateStatusInEpic(newEpic); //сделал расчет статуса
                calculateTime(newEpic);

                prioritizedTask.remove(subTaskStorage.get(subTask.getId()));
                prioritizedTask.add(subTask);
            }
           // add(subTask);

            //Удаляю сабтаску из дерева по айди и потом добавить новую
        } else {
            System.out.println("Данного id для subTask не существует");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        //не использую epicStorage.put(epic.getId(), epic) потому что могу затереть поля (например SubTaskListId)
        Epic savedEpic = epicStorage.get(epic.getId());
        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
    }

    //УДАЛЕНИЕ ПО ID
    @Override
    public void deleteTaskById(int id) {
        Task taskForDelete = taskStorage.get(id);
        if (taskForDelete!=null) {
            prioritizedTask.remove(taskForDelete);
        }

        taskStorage.remove(id);
        inMemoryHistoryManager.remove(id);

    }

    @Override
    public void deleteSubTaskById(int id) {
        SubTask subTaskForDelete = subTaskStorage.get(id);
        if (subTaskForDelete!=null) {
            prioritizedTask.remove(subTaskForDelete);
        }
        int idCountEpic = subTaskStorage.get(id).getIdEpic();
        epicStorage.get(idCountEpic).removeSubTuskId(id);
        updateStatusInEpic(epicStorage.get(idCountEpic));
        subTaskStorage.remove(id);
        inMemoryHistoryManager.remove(id);
        calculateTime(epicStorage.get(idCountEpic));
        // после удаления саб таски, ее нужно удалить из определенного эпика

    }

    @Override
    public void deleteEpicById(int id) {
        ArrayList<Integer> listSubTaskId = epicStorage.get(id).getAllListSubTaskId();
        epicStorage.remove(id);

        Epic epicForDelete = epicStorage.get(id);
        if (epicForDelete!=null) {
            prioritizedTask.remove(epicForDelete);
        }
        //Получить epic и все его сабтаски по айди
        for (Integer i : listSubTaskId) {
            subTaskStorage.remove(i);
            inMemoryHistoryManager.remove(i);

            SubTask subTaskForDelete = subTaskStorage.get(i);
            if (subTaskForDelete!=null) {
                prioritizedTask.remove(subTaskForDelete);
            }
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
        if (epic.getAllListSubTaskId().isEmpty() || subTasksStatusInProgressOrDone == 0) {//или пустой или там ни одного done или InProgress
            epic.setStatus(Status.NEW);
        } else if (subTasksStatusIsNewOrProgress == 0) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
        return epic;
    }
}
