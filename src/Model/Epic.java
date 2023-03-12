package Model;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    protected ArrayList<Integer> subTaskListId = new ArrayList<>();

    public Epic(String nameTask, String description, Status status) {
        super(nameTask, description, status);
    }

    public void addListSubTaskId(int id){
        subTaskListId.add(id);
    }

    public ArrayList<Integer> getAllListSubTaskId(){
        return subTaskListId;
    }

    public void clearListSubTaskId(){
        subTaskListId.clear();
    }

    public void removeSubTuskId(int id){
        subTaskListId.remove(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subTaskListId, epic.subTaskListId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTaskListId);
    }

    @Override
    public String toString() {
        return "Epic{" +
                ", Name=" + getName() +
                ", Descriptor=" + getDescription() +
                ", Status=" + getStatus() +
                ", id=" + getId() +
                ", subTaskListId=" + subTaskListId +
                '}';
    }
}
