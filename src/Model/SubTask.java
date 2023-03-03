package Model;

import java.util.Objects;

public class SubTask extends Task {

    private int idEpic;

    public SubTask(String nameTask, String description, String status, int idEpic) {
        super(nameTask, description, status);
        this.idEpic = idEpic;
    }

    public int getIdEpic() {
        return idEpic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SubTask subTask = (SubTask) o;
        return idEpic == subTask.idEpic;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), idEpic);
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
