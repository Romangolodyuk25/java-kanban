package Model;

import java.util.Objects;

public class SubTask extends Task {

    public Epic epic;
    private int idEpic;

    public SubTask(String nameTask, String description, String status, int idEpic) {
        super(nameTask, description, status);
        this.idEpic = idEpic;
    }

    public int getIdEpic() {
        return idEpic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
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
                "EpicId=" + getIdEpic() +
                ", Name=" + getName() +
                ", Descriptor=" + getDescription() +
                ", id=" + getId() +
                ", Status=" + getStatus() +
                '}';
    }
}
