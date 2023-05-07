package test;

import model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.FileBackedTasksManager;
import service.HistoryManager;
import service.Managers;
import service.TaskManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager>{

    TaskManager taskManager = Managers.getDefaultFileBackedTasksManager();
    File file = new File("testFile.csv");
    HistoryManager historyManager = Managers.getDefaultHistory();

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public Task createNewTask(){
        return new Task("Переезд", "Я буду переезжать", Status.NEW,1);
    }

    public Epic createEpic(){
        return new Epic("Мы переезжаем", "Много задач по переезду", Status.NEW,1);
    }

    public SubTask createSubTask(){
        return new SubTask("Собрать вещи", "Разложить вещи в чемодан", Status.NEW,1, 1);
    }

    private String toString(Task task) {// метод который должен сохранить задачу в строку
        String epicId = "";
        if (task.getType() == TaskType.SUBTASK) {
            epicId = String.valueOf(((SubTask) task).getIdEpic());
        }
        return task.getId() + "," + task.getType() + "," + task.getName() + "," +
                task.getStatus() + "," + task.getDescription() + "," + epicId + "\n";
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
        if (typeTask.equals(TaskType.SUBTASK)){
            epicId = stringArr[5];
        }

        if (typeTask.equals(TaskType.TASK)) {
            newTask = new Task(nameTask, descriptionName, statusTask, idTask);
        } else if (typeTask.equals(TaskType.SUBTASK)) {
            newTask = new SubTask(nameTask, descriptionName, statusTask, Integer.parseInt(epicId), idTask);
        } else if (typeTask.equals(TaskType.EPIC)) {
            newTask = new Epic(nameTask, descriptionName, statusTask, idTask);
        }
        return newTask;
    }

    @Test
    public void shouldNotSaveIfTasksListEmpty() throws IOException {
        List<Task> tasks = getTaskManager().getAllTasks();
        try(FileReader fileReader = new FileReader(file.getName());
            BufferedReader bf = new BufferedReader(fileReader);
            FileWriter fileWriter = new FileWriter(file.getName())
        ){
            String receivedTask= "";
            for(Task task : tasks) {
                fileWriter.write(toString(task));
                receivedTask = String.valueOf(bf.lines());
            }
            assertEquals(0, tasks.size());
           assertEquals("", receivedTask);

        }
    }
    @Test
    public void shouldSaveTasksNotEmpty() throws IOException {
        Task task = createNewTask();
        try(FileWriter fileWriter = new FileWriter(file)){
            fileWriter.write(toString(task));
        }
        try(FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader)
        ) {
           String line = bufferedReader.readLine();
           assertEquals(task, fromString(line));
        }
    }

    @Test
    public void shouldWhatEpicAddAndNotHaveSubTask() throws IOException {
        Task task = createNewTask();
        Epic epic = createEpic();
        List<Task> receivedTasks = new ArrayList<>();
        try(FileWriter fileWriter = new FileWriter(file)){
            fileWriter.write(toString(task));
            fileWriter.write(toString(epic));
            assertEquals(0, receivedTasks.size());
        }
        try(FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader)
        ) {
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                receivedTasks.add(fromString(line));
            }
            assertEquals(2, receivedTasks.size());
            assertEquals(epic, receivedTasks.get(1));
            Epic newEpic = (Epic) receivedTasks.get(1);
            assertEquals(0, newEpic.getAllListSubTaskId().size());
        }
    }

    @Test
    public void shouldCheckIfHistoryListEmpty() throws IOException {
        Task task = createNewTask();
        Epic epic = createEpic();

        String historyStr = FileBackedTasksManager.historyToString(historyManager);
        assertEquals("", historyStr);

        try(FileWriter fileWriter = new FileWriter(file)){
            fileWriter.write(toString(task));
            fileWriter.write(toString(epic));
        }
        try(FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader)
        ){
            String receivedStr = "";
            while(bufferedReader.ready()){
                receivedStr = bufferedReader.readLine();
                assertFalse(receivedStr.contains("1,2"));
            }
        }
    }

    @Test
    public void shouldSaveIfEpicHaveSubTask() throws IOException {
        Task task = createNewTask();
        Epic epic = createEpic();
        SubTask subTask = createSubTask();
        List<Task> receivedTasks = new ArrayList<>();
        try(FileWriter fileWriter = new FileWriter(file)){
            fileWriter.write(toString(task));
            fileWriter.write(toString(epic));
            fileWriter.write(toString(subTask));
            assertEquals(0, receivedTasks.size());
        }
        try(FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader)
        ) {
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                receivedTasks.add(fromString(line));
            }
            assertEquals(3, receivedTasks.size());
            assertEquals(subTask, receivedTasks.get(2));
            Epic receivedEpic = (Epic) receivedTasks.get(1);
            SubTask receivedSubTask = (SubTask) receivedTasks.get(2);
            assertEquals(receivedEpic.getId(), receivedSubTask.getIdEpic());
        }
    }

    @Test
    public void shouldSaveIfHistoryListIsNotEmpty() throws IOException{
        Task task = createNewTask();
        Epic epic = createEpic();
        epic.setId(2);
        historyManager.add(task);
        historyManager.add(epic);

        String historyStr = FileBackedTasksManager.historyToString(historyManager);
        assertEquals("1,2", historyStr);

        try(FileWriter fileWriter = new FileWriter(file)){
            fileWriter.write(toString(task));
            fileWriter.write(toString(epic));
            fileWriter.write(historyStr);
        }
        try(FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader)
        ){
            String receivedStr = "";
            while(bufferedReader.ready()){
                receivedStr = bufferedReader.readLine();
            }
            assertTrue(receivedStr.contains("1,2"));
        }
    }
}
