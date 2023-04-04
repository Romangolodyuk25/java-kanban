package service;

import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    CustomLinkedList<Task> customLinkedList = new CustomLinkedList<>();

    @Override
    public List<Task> getHistory() {
        return customLinkedList.getTasks();
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            System.out.println("В значении лежит null");
            return;
        }
        customLinkedList.linkLast(task);

    }
    @Override
    public void remove(int id){
        customLinkedList.remove(id);
    }


}
