package service;

import model.*;

import java.io.*;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final File file;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public FileBackedTasksManager(File file){
       this.file = file;
    }

    public FileBackedTasksManager(){
        this.file = new File("testFile.csv");
    }

    public void save() {
        if(!file.exists()){
            System.out.println("Файл не был создан");
            return;
        }
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(createFirstString());
            for (Task task : getAllTasks()) {
                fileWriter.write(toString(task));
            }
            for (Task task : getAllEpic()) {
                fileWriter.write(toString(task));
            }
            for (Task task : getAllSubTask()) {
                fileWriter.write(toString(task));
            }
            fileWriter.write("\n");

            String historyString = historyToString(inMemoryHistoryManager);
            fileWriter.write(historyString);
        } catch (IOException e) {
           throw new ManagerSaveException();
        }
    }

    public static FileBackedTasksManager loadFromFile(File file){
        FileBackedTasksManager manager = Managers.getDefaultFileBackedTasksManager();
        try (FileReader fileReader = new FileReader(file.getName());
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
             ArrayList<Integer> historyList = new ArrayList<>();
             int generatorId = 0;

             bufferedReader.readLine();// считал header;

            while (bufferedReader.ready()) {
                String readLine = bufferedReader.readLine();

                if (!readLine.isEmpty()) {
                Task newTask = manager.fromString(readLine);
                if (newTask!=null) {
                    if (newTask.getId() > generatorId){
                        generatorId = newTask.getId();
                    }
                    if (newTask.getType() == TaskType.SUBTASK) {
                        SubTask newSubTask = (SubTask) newTask;
                        manager.subTaskStorage.put(newSubTask.getId(), newSubTask);
                        manager.epicStorage.get(newSubTask.getIdEpic()).addListSubTaskId(newTask.getId());
                        manager.calculateTime(manager.getEpicById(newSubTask.getIdEpic()));
                        manager.prioritizedTask.add(newSubTask);
                    } else if (newTask.getType() == TaskType.EPIC) {
                        Epic newEpic = (Epic) newTask;
                        manager.epicStorage.put(newEpic.getId(), newEpic);
                    } else {
                        manager.taskStorage.put(newTask.getId(), newTask);
                        manager.prioritizedTask.add(newTask);
                    }
                }
                } else {
                    readLine = bufferedReader.readLine();
                    historyList.addAll(historyFromString(readLine));
                    for (Integer id : historyList){
                        manager.inMemoryHistoryManager.add(manager.findTask(id));
                    }
                    manager.id = generatorId + 1;
                    break;
                }
            }
        } catch (IOException e){
            throw new ManagerSaveException();
        }
        return manager;
    }

    private Task findTask(int id){
        if (taskStorage.get(id) != null){
            return taskStorage.get(id);
        } else if (subTaskStorage.get(id) != null) {
            return subTaskStorage.get(id);
        }
        return epicStorage.get(id);
    }

    private String createFirstString() {
        return "id,type,name,status,description,epic,start_time,duration,end_time" + "\n";
    }

    private String toString(Task task) {// метод который должен сохранить задачу в строку
        String epicId = "";
        LocalDateTime endTime = task.getEndTime();
        if (task.getType() == TaskType.SUBTASK) {
            epicId = String.valueOf(((SubTask) task).getIdEpic());
        }
        if (task.getType() == TaskType.EPIC){
            Epic epic = (Epic)task;
            endTime = epic.getEndTime();
        }
        String endTimeStr = endTime == null ? "" : dateTimeFormatter.format(endTime);
        String startTimeStr = task.getStartTime() == null ? "" : dateTimeFormatter.format(task.getStartTime());
        return task.getId() + "," + task.getType() + "," + task.getName() + "," +
                task.getStatus() + "," + task.getDescription() + "," + epicId + ","
                + startTimeStr + ","
                + task.getDuration() + "," + endTimeStr + "\n";
    }

    private Task fromString(String value) { //метод который должен создать задачу из Строки
        Task newTask = null;
        String[] stringArr = value.split(",");
        int idTask = Integer.parseInt(stringArr[0]);
        TaskType typeTask = TaskType.valueOf(stringArr[1]);
        String nameTask = stringArr[2];
        Status statusTask = Status.valueOf(stringArr[3]);
        String descriptionName = stringArr[4];
        String epicId = "";
        LocalDateTime startTime = null;
        int duration = 0;
        if (typeTask.equals(TaskType.SUBTASK)){
            epicId = stringArr[5];
        }
        startTime = LocalDateTime.parse(stringArr[6],dateTimeFormatter);
        duration = Integer.parseInt(stringArr[7]);

        if (typeTask.equals(TaskType.TASK)) {
            newTask = new Task(nameTask, descriptionName, statusTask, idTask, startTime, duration);
        } else if (typeTask.equals(TaskType.SUBTASK)) {
            newTask = new SubTask(nameTask, descriptionName, statusTask,idTask, Integer.parseInt(epicId), startTime, duration);

        } else if (typeTask.equals(TaskType.EPIC)) {
            newTask = new Epic(nameTask, descriptionName, statusTask, idTask);
        }
        return newTask;
    }

    public static String historyToString(HistoryManager manager) {
        StringBuilder historyString = new StringBuilder();
        for (Task task : manager.getHistory()) {
            historyString.append(task.getId());
            historyString.append(",");
        }
        if (historyString.length() > 0) {
            historyString.deleteCharAt(historyString.length() - 1);
        }
        return historyString.toString();
    }

    public static List<Integer> historyFromString(String value) {
        List<Integer> historyId = new ArrayList<>();
        String[] splitValue = value.split(",");
        for (String i : splitValue) {
            historyId.add(Integer.parseInt(i));
        }
        return historyId;
    }

    @Override
    public int createTask(Task newTask){
        super.createTask(newTask);
        save();
        return newTask.getId();
    }

    @Override
    public int createSubTask(SubTask newSubTask){
        super.createSubTask(newSubTask);
        save();
        return newSubTask.getId();
    }

    @Override
    public int createEpic(Epic newEpic){
        super.createEpic(newEpic);
        save();
        return newEpic.getId();
    }

    @Override
    public void updateTask(Task task){
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask){
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void updateEpic(Epic epic){
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteTaskById(int id){
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id){
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubTaskById(int id) {
        super.deleteSubTaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks(){
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubTasks(){
        super.deleteAllSubTasks();
        save();
    }

    @Override
    public void deleteAllEpic() {
        super.deleteAllEpic();
        save();
    }

    @Override
    public Epic updateStatusInEpic(Epic epic){
        Epic epicName = super.updateStatusInEpic(epic);
        save();
        return epicName;
    }

    @Override
    public Task getTaskById(int id){
        Task countTask = super.getTaskById(id);
        save();
        return countTask;
    }

    @Override
    public SubTask getSubTaskById(int id){
        SubTask countSubTask = super.getSubTaskById(id);
        save();
        return countSubTask;
    }

    @Override
    public Epic getEpicById(int id){
        Epic countEpic = super.getEpicById(id);
        save();
        return countEpic;
    }
}
