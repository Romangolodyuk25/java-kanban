package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomLinkedList<T extends Task> {

    private Node<T> head;
    private Node<T> tail;

    private final Map<Integer, Node<T>> idAndNodeTaskMap;//d мапе храниться по АЙДИ задачи - Таскав Ноде

    public CustomLinkedList() {
        this.idAndNodeTaskMap = new HashMap<>();
    }

    public void linkLast(T task) {
        final int id = task.getId();
        remove(id);

        Node<T> newNode = new Node<>(tail, task, null);//при создании новой ноды она будет ссылаться на хвост

        if (head == null) {
            head = newNode;
        }else {
            tail.next = newNode;
        }
        tail = newNode;
        idAndNodeTaskMap.put(task.getId(), newNode); //нужно положить задачу в мапу что бы знать где какая нода по айди задачи
    }

    public List<Task> getTasks() {
        List<Task> historyList = new ArrayList<>();
        Node<T> currentNode = head;

        while (currentNode != null) {
            historyList.add(currentNode.value);
            currentNode = currentNode.next;
        }
        return historyList;
    }

    public void removeNode(Node<T> node) {
        if (node.prev != null) {
            node.prev.next = node.next;
            if (node.next == null) { // значит нода удалиться и ее предыдущая станет последней
                tail = node.prev;
            } else {
                node.next.prev = node.prev;
            }
        } else { // если у ноды предыдущий элемент null, значит нода которая пришла была ПЕРВОЙ, а значет некст ссылку перекинем
            head = node.next;
            if (head == null) {
                tail = null;
            } else {
                head.prev = null;
            }
        }
    }

    public void remove(int id) {
        if (idAndNodeTaskMap.containsKey(id)) {
            removeNode(idAndNodeTaskMap.get(id));
            idAndNodeTaskMap.remove(id);
        }
    }

    class Node<T> {
        public T value;
        public Node<T> prev;
        public Node<T> next;

        public Node(Node<T> prev, T value, Node<T> next) {
            this.value = value;
            this.next = next;
            this.prev = prev;
        }
    }
}
