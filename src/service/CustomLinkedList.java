package service;

import model.Node;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomLinkedList<T extends Task> {

    public Node<T> head;
    public Node<T> tail;
    int size = 0;

    private final Map<Integer, Node<T>> idAndNodeTaskMap;//d мапе храниться по АЙДИ задачи - Таскав Ноде

    public CustomLinkedList() {
        this.idAndNodeTaskMap = new HashMap<>();
    }

    public void linkLast(T node) {
        Node<T> newNode = new Node<T>(node);

        if (this.head == null) {
            this.head = newNode;
            this.tail = newNode;
            idAndNodeTaskMap.put(node.getId(), newNode); //нужно положить задачу в мапу что бы знать где какая нода по айди задачи
            size++;
            return;
        }
        remove(node.getId());

        if (this.tail != null) {
            this.tail.next = newNode;
        }
        this.tail = newNode;
        idAndNodeTaskMap.put(node.getId(), newNode); //нужно положить задачу в мапу что бы знать где какая нода по айди задачи
        size++;
        /**либо с помощью цикла
         * Node<T> currentNode = head;
         * while(currentNode.next!=null){
         * currentNode = currentNode.next
         * }
         * currentNode = new Node<T>(node);
         */
    }

    public List<Task> getTasks() {
        List<Task> historyList = new ArrayList<>();

        if (this.head == null) {
            System.out.println("Связный список пустой");
            return historyList;
        }
        Node<T> currentNode = head;

        while (currentNode != null) {
            historyList.add(currentNode.value);
            currentNode = currentNode.next;
        }
        return historyList;
    }

    public void removeNode(Node<T> node) {
        Node<T> nextNode = node.next;
        Node<T> prevNode = node.prev;
        if (node == head) {
            head = nextNode;
        }
        if (node == tail) {
            tail = prevNode;
        }
        if (prevNode != null) {
            node.prev.next = nextNode;//ссылаюсь на ПРЕДЫДУЩИЙ узел говорю что ОН указывает теперь на СЛЕДУБЩИЙ узел
        }
        if (nextNode != null) {
            node.next.prev = prevNode;//ссылаюсь на CЛЕДУЮЩИЙ УЗЕЛ говорю что он указывает теперь ПРЕДЫДУЩИЙ узел
        }
    }

    public void remove(int id) {
        if (idAndNodeTaskMap.containsKey(id)) {
            removeNode(idAndNodeTaskMap.get(id));
            idAndNodeTaskMap.remove(id);
        }
    }
}
