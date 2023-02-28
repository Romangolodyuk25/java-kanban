package Model;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    public ArrayList<SubTask> subTaskList = new ArrayList<>();

    public Epic(String nameTusk, String description, String status) {
        super(nameTusk, description, status);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subTaskList, epic.subTaskList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTaskList);
    }

    @Override
    public String toString() {
        return "Epic{" +
                ", Name=" + getName() +
                ", Descriptor=" + getDescription() +
                ", Status=" + getStatus() +
                ", id=" + getId() +
                ", subTaskList=" + subTaskList +
                '}';
    }
}
