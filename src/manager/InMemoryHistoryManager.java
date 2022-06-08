package manager;

import exceptions.DefaultTasksException;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private Node<Task> tail = null;
    private Node<Task> head = null;

    private Map<Integer, Node<Task>> nodeMap = new HashMap<>();

    @Override
    public void add(Task task) {

        if (nodeMap.containsKey(task.getId())) {
            removeNode(nodeMap.get(task.getId()));
        }

        Node<Task> newNode = linkLast(task);
        nodeMap.put(task.getId(), newNode);
    }

    @Override
    public void remove(int id) {
        if (!nodeMap.containsKey(id)) {
            throw new DefaultTasksException("Такой задачи нет.");
        }

        removeNode(nodeMap.get(id));
        nodeMap.remove(id);
    }

    public Node<Task> linkLast(Task task) {

        if (head == null) {
            Node<Task> newNode = new Node(task, null, null);
            head = newNode;
            tail = newNode;

            return newNode;
        }

        Node<Task> oldTail = tail;
        Node<Task> newNode = new Node<>(task, null, oldTail);
        tail = newNode;
        oldTail.next = newNode;

        return newNode;
    }

    @Override
    public List<Task> getHistory() {
        List<Task> historyList = new ArrayList<>();

        Node<Task> currentHead = head;

        while (currentHead != null) {
            historyList.add(currentHead.data);
            currentHead = currentHead.next;
        }

        return historyList;
    }

    public void removeNode(Node<Task> node) {

        if (node.prev == null && node.next == null) {
            tail = null;
            head = null;
            return;
        }

        if (node == head) {
            head = node.next;
            head.prev = null;
            return;
        }

        if (node == tail) {
            tail = node.prev;
            tail.next = null;
            return;
        }

        Node<Task> prevNode = node.prev;
        Node<Task> nextNode = node.next;
        prevNode.next = nextNode;
        nextNode.prev = prevNode;
    }

    @Override
    public void removeAll() {
        head = null;
        tail = null;
        nodeMap.clear();
    }

    static class Node<T extends Task> {
        public T data;
        public Node<T> next;
        public Node<T> prev;

        public Node(T data, Node<T> next, Node<T> prev) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }
}
