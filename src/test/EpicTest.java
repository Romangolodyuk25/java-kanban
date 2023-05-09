package test;

import model.Epic;
import model.Status;
import model.SubTask;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

    public Epic createEpic(){
        return new Epic("Мы переезжаем", "Много задач по переезду", Status.NEW,1, LocalDateTime.of(2023, 1, 1, 12, 0));
    }

    public SubTask createSubTaskStatusNew(){
        return new SubTask("Собрать вещи", "Разложить вещи в чемодан", Status.NEW,1, 1, LocalDateTime.of(2023, 1, 1, 15, 0),60);
    }

    public SubTask createSubTaskStatusDone(){
        return new SubTask("Собрать вещи", "Разложить вещи в чемодан", Status.DONE,1, 1, LocalDateTime.of(2023, 1, 1, 17, 0),60);
    }

    public SubTask createSubTaskStatusInProgress(){
        return new SubTask("Собрать вещи", "Разложить вещи в чемодан", Status.IN_PROGRESS,1, 1, LocalDateTime.of(2023, 1, 1, 12, 0),60);
    }

    @Test
    public void shouldReturnStatusNewWhenSubTaskListEmpty(){
        Epic epic = createEpic();

        assertEquals(Status.NEW, epic.getStatus(), "Статус расчитан неправильно");
    }

    @Test
    public void shouldReturnStatusNewWhenAllSubTaskHaveStatusNew(){ // может нужно проверить расчет статуса епика исходя из сабтасок??
        int idEpic = inMemoryTaskManager.createEpic(createEpic());
        int idSubTask1 = inMemoryTaskManager.createSubTask(createSubTaskStatusNew());
        int idSubTask2 = inMemoryTaskManager.createSubTask(createSubTaskStatusNew());
        Epic epic = inMemoryTaskManager.getEpicById(idEpic);
        inMemoryTaskManager.updateStatusInEpic(epic);

        assertEquals(Status.NEW, epic.getStatus(), "Статус расчитан неправильно");

    }


    @Test
    public void shouldReturnStatusDoneWhenAllSubTaskHaveStatusDone() {// может нужно проверить расчет статуса епика исходя из сабтасок??
        int idEpic = inMemoryTaskManager.createEpic(createEpic());
        int idSubTask1 = inMemoryTaskManager.createSubTask(createSubTaskStatusDone());
        int idSubTask2 = inMemoryTaskManager.createSubTask(createSubTaskStatusDone());
        Epic epic = inMemoryTaskManager.getEpicById(idEpic);
        inMemoryTaskManager.updateStatusInEpic(epic);

        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    public void shouldReturnStatusInProgressWhenAllSubTaskHaveStatusDoneAndNew() { // может нужно проверить расчет статуса епика исходя из сабтасок??
        int idEpic = inMemoryTaskManager.createEpic(createEpic());
        int idSubTask1 = inMemoryTaskManager.createSubTask(createSubTaskStatusDone());
        int idSubTask2 = inMemoryTaskManager.createSubTask(createSubTaskStatusDone());
        int idSubTask3 = inMemoryTaskManager.createSubTask(createSubTaskStatusNew());
        Epic epic = inMemoryTaskManager.getEpicById(idEpic);
        inMemoryTaskManager.updateStatusInEpic(epic);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус расчитан неправильно");
    }

    @Test
    public void shouldReturnStatusInProgressWhenAllSubTaskHaveStatusInProgress() { // может нужно проверить расчет статуса епика исходя из сабтасок??
        int idEpic = inMemoryTaskManager.createEpic(createEpic());
        int idSubTask1 = inMemoryTaskManager.createSubTask(createSubTaskStatusInProgress());
        int idSubTask2 = inMemoryTaskManager.createSubTask(createSubTaskStatusInProgress());
        Epic epic = inMemoryTaskManager.getEpicById(idEpic);
        inMemoryTaskManager.updateStatusInEpic(epic);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус расчитан неправильно");
    }
}