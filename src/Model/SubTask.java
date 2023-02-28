package Model;

import java.util.Objects;

public class SubTask extends Task {

    public Epic epic;

    public SubTask(String nameTask, String description, String status) {
        super(nameTask, description, status);
    }

    public SubTask(String nameTask, String description, String status, int id){
        super(nameTask, description,status);
        this.setId(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SubTask subTask = (SubTask) o;
        return Objects.equals(epic, subTask.epic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epic);
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "EpicId=" + epic.getId() +
                ", Name=" + getName() +
                ", Descriptor=" + getDescription() +
                ", id=" + getId() +
                ", Status=" + getStatus() +
                '}';
    }
}
