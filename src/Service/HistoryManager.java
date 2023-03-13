package Service;

import Model.Task;
import java.util.List;

public interface HistoryManager {

    void add(Task task);//должен помечать задачи как просмотренные

    List<Task> getHistory();

}
