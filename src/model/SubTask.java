package model;

import java.time.LocalDateTime;
import java.util.Objects;

public class SubTask extends Task {

    private int idEpic;

    public SubTask(String nameTask, String description, Status status, int idEpic) {
        super(nameTask, description, status);
        this.idEpic = idEpic;

    }

    public SubTask(String nameTask, String description, Status status, int idEpic, int id) {
        super(nameTask, description, status, id);
        this.idEpic = idEpic;
    }

    public SubTask(String name, String description, Status status, int id, int idEpic, LocalDateTime startTime, int duration) {
        super(name, description, status, id, startTime, duration);
        this.idEpic = idEpic;
    }

    public int getIdEpic() {
        return idEpic;
    }

    @Override
    public TaskType getType(){
        return TaskType.SUBTASK;
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
                ", startTime='" + getStartTime() + '\'' +
                ", duration='" + getDuration() + '\'' +
                '}';
    }
}
