package Service;

import Model.Task;

import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {
    private final LinkedList<Task> historyList = new LinkedList<>();

    //перенесите часть кода из TaskManager для работы с историей
    @Override
    public LinkedList<Task> getHistory() {
        return historyList;
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            System.out.println("В значении лежит null");
            return;
        }
        if (historyList.size() >= 10) {
            historyList.removeFirst();
        }
        historyList.add(task);
    }
}
