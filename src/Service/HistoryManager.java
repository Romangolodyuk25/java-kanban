package Service;

import Model.Task;

import java.util.LinkedList;

public interface HistoryManager {

    void add(Task task);//должен помечать задачи как просмотренные

    LinkedList<Task> getHistory();

}
