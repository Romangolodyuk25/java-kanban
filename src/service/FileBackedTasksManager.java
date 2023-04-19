package service;

import model.*;

import java.io.*;
import java.nio.Buffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final Path file;

    public FileBackedTasksManager(String fileName) throws IOException {
        if (new File(fileName).exists()) {
            this.file = Paths.get(fileName);
        } else {
            file = Files.createFile(Path.of(fileName));
        }
    }

    public static void main(String[] args) throws IOException {
        FileBackedTasksManager manager = new FileBackedTasksManager("testFile.csv");
        FileBackedTasksManager.loadFromFile(manager.file.toFile());
    }
//    public void init() throws IOException {
//        loadFromFile(file.toFile());
//    }

    private void save() throws IOException {
        try (FileWriter fileWriter = new FileWriter(file.toString())) {
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
            System.out.println("Произошла ошибка");
        }
    }

    public static FileBackedTasksManager loadFromFile(File file) throws IOException {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        FileBackedTasksManager manager = new FileBackedTasksManager(file.getName());

        try (FileReader fileReader = new FileReader(file.getName());
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
             ArrayList<Integer> historyList = new ArrayList<>();

            while (bufferedReader.ready()) {
                String readLine = bufferedReader.readLine();
                if (readLine.startsWith("id") || readLine.isEmpty()) {
                    continue;
                } else if (readLine.contains(TaskType.TASK.name()) || readLine.contains(TaskType.SUBTASK.name())
                || readLine.contains(TaskType.EPIC.name())) {
                Task newTask = manager.fromString(readLine);
                if (newTask!=null) {
                    if (newTask instanceof SubTask) {
                        SubTask newSubTask = (SubTask) newTask;
                        inMemoryTaskManager.createSubTask(newSubTask);
                    } else if (newTask instanceof Epic) {
                        Epic newEpic = (Epic) newTask;
                        newEpic.setSubTaskListId(historyList);
                        inMemoryTaskManager.createEpic(newEpic);
                    } else if (newTask instanceof Task) {
                        inMemoryTaskManager.createTask(newTask);
                    }
                }
                } else {
                    historyList.addAll(historyFromString(readLine));
                }
            }
        }
        return manager;
    }

    private String createFirstString() {
        return "id,type,name,status,description,epic" + "\n";
    }

    private String toString(Task task) {// метод который должен сохранить задачу в строку
        if (task instanceof SubTask) {
            return task.getId() + "," + TaskType.SUBTASK.name() + "," + task.getName() + "," +
                    task.getStatus() + "," + task.getDescription() + "," + ((SubTask) task).getIdEpic() + "\n";
        } else if (task instanceof Epic) {
            return task.getId() + "," + TaskType.EPIC.name() + "," + task.getName() + "," +
                    task.getStatus() + "," + task.getDescription() + "," + "\n";
        }
        return task.getId() + "," + TaskType.TASK.name() + "," + task.getName() + "," +
                task.getStatus() + "," + task.getDescription() + "," + "\n";
    }

    private Task fromString(String value) { //метод который должен создать задачу из Строки
        Task newTask = null;
        String[] stringArr = value.split(",");
        int idTask = Integer.parseInt(stringArr[0]);
        String typeTask = stringArr[1];
        String nameTask = stringArr[2];
        String statusTask = stringArr[3];
        String descriptionName = stringArr[4];
        String epicId = "";
        if (typeTask.equals(TaskType.SUBTASK.name())){
            epicId = stringArr[5];
        }

        if (typeTask.equals(TaskType.TASK.name())) {
            newTask = new Task(nameTask, descriptionName, Status.valueOf(statusTask));
            newTask.setId(idTask);
        } else if (typeTask.equals(TaskType.SUBTASK.name())) {
            newTask = new SubTask(nameTask, descriptionName, Status.valueOf(statusTask), Integer.parseInt(epicId));
            newTask.setId(idTask);
        } else if (typeTask.equals(TaskType.EPIC.name())) {
            newTask = new Epic(nameTask, descriptionName, Status.valueOf(statusTask));
            newTask.setId(idTask);
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
    public int createTask(Task newTask) throws IOException {
        super.createTask(newTask);
        save();// отловить IOException
        return newTask.getId();
    }

    @Override
    public int createSubTask(SubTask newSubTask) throws IOException {
        super.createTask(newSubTask);
        save();// отловить IOException
        return newSubTask.getId();
    }

    @Override
    public int createEpic(Epic newEpic) throws IOException {
        super.createEpic(newEpic);
        save();// отловить IOException
        return newEpic.getId();
    }

    @Override
    public void updateTask(Task task) throws IOException {
        super.updateTask(task);
        save();// отловить IOException
    }

    @Override
    public void updateSubTask(SubTask subTask) throws IOException {
        super.updateSubTask(subTask);
        save();// отловить IOException
    }

    @Override
    public void updateEpic(Epic epic) throws IOException {
        super.updateEpic(epic);
        save();// отловить IOException
    }

    @Override
    public void deleteTaskById(int id) throws IOException {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) throws IOException {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubTaskById(int id) throws IOException {
        super.deleteSubTaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() throws IOException {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubTasks() throws IOException {
        super.deleteAllSubTasks();
        save();
    }

    @Override
    public void deleteAllEpic() throws IOException {
        super.deleteAllEpic();
        save();
    }

    @Override
    public Epic updateStatusInEpic(Epic epic) throws IOException {
        Epic epicName = super.updateStatusInEpic(epic);
        save();
        return epicName;
    }

    @Override
    public Task getTaskById(int id) throws IOException {
        Task countTask = super.getTaskById(id);
        save();
        return countTask;
    }

    @Override
    public SubTask getSubTaskById(int id) throws IOException {
        SubTask countSubTask = super.getSubTaskById(id);
        save();
        return countSubTask;
    }

    @Override
    public Epic getEpicById(int id) throws IOException {
        Epic countEpic = super.getEpicById(id);
        save();
        return countEpic;
    }
}
