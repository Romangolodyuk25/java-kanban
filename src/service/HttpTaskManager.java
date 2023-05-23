package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.ManagerSaveException;
import model.SubTask;
import model.Task;
import server.KVClient;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

public class HttpTaskManager extends FileBackedTasksManager{

    KVClient client;
    Gson gson;

    public HttpTaskManager() throws IOException, URISyntaxException {
        gson = Managers.getGson();
        client = new KVClient(new URI("http://localhost:8078"));
    }

    @Override
    public void save() {
        try {
            client.put("tasks",gson.toJson(taskStorage));
            client.put("subtasks", gson.toJson(subTaskStorage));
            client.put("epics",gson.toJson(epicStorage));
            client.put("history", gson.toJson(getHistory()));
            client.put("tasks", gson.toJson(getPrioritizedTasks()));
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException();
        }
    }

    public void load() throws IOException, InterruptedException {
        String jsonTasks = client.load("task");
        taskStorage.put(id, gson.fromJson(jsonTasks,
                new TypeToken<HashMap<Integer, Task>>() {
                }.getType())
        );
        String jsonEpics = client.load("epic");
        taskStorage.put(id, gson.fromJson(jsonEpics,
                new TypeToken<HashMap<Integer, Epic>>() {
                }.getType())
        );
        String jsonSubTasks = client.load("subtask");
        taskStorage.put(id, gson.fromJson(jsonSubTasks,
                new TypeToken<HashMap<Integer, SubTask>>() {
                }.getType())
        );
        String jsonHistory = client.load("history");
        ArrayList<Integer> history = gson.fromJson(jsonHistory,
                new TypeToken<ArrayList<Integer>>() {
                }.getType());
        for (Integer id : history){
            if(taskStorage.containsKey(id)){
                getTaskById(id);
            } else if (subTaskStorage.containsKey(id)){
                getSubTaskById(id);
            } else {
                getEpicById(id);
            }
        }
        String jsonPrioritizedTask = client.load("tasks");
        prioritizedTask.addAll(gson.fromJson(jsonPrioritizedTask,
                new TypeToken<ArrayList<Task>>() {
                }.getType()));
    }
}
