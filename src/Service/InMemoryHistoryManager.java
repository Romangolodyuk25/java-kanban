package Service;

import Model.Task;

import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {
    public static LinkedList<Task> historyList = new LinkedList<>();

    //перенесите часть кода из TaskManager для работы с историей
    @Override
    public LinkedList<Task> getHistory() {
        return historyList;
    }

    @Override
    public void add(Task task) {
        historyList.add(task);
    }
}
