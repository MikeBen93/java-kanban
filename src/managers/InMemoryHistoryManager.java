package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node<Task>> mapOfTasks;
    private Node<Task> head;
    private Node<Task> tail;

    InMemoryHistoryManager() {
        mapOfTasks = new HashMap<Integer, Node<Task>>();
    }

    public void add(Task task) {
        if (task != null) {
            remove(task.getId());
            linkLast(task);
        }
    }

    public List<Task> getHistory() {
        return getTasks();
    }

    public void remove(int id) {
        if (!mapOfTasks.containsKey(id))
            return;

        removeNode(mapOfTasks.get(id));
    }

    void linkLast(Task newTask) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<Task>(oldTail, newTask, null);
        tail = newNode;
        mapOfTasks.put(newTask.getId(), newNode);
        if (oldTail == null)
            head = newNode;
        else
            oldTail.next = newNode;
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<Task>();
        Node<Task> currentNode = head;
        while (!(currentNode == null)) {
            tasks.add(currentNode.data);
            currentNode = currentNode.next;
        }
        return tasks;
    }

    private void removeNode(Node<Task> nodeToRemove) {
        Node<Task> nextNode = nodeToRemove.next;
        Node<Task> prevNode = nodeToRemove.prev;

        if (head == nodeToRemove && tail == nodeToRemove) {
            head = null;
            tail = null;
        } else if (head == nodeToRemove && !(tail == nodeToRemove)) {
            head = nextNode;
            head.prev = null;
        } else if (!(head == nodeToRemove) && tail == nodeToRemove) {
            tail = prevNode;
            tail.next = null;
        } else {
            nextNode.prev = prevNode;
            prevNode.next = nextNode;
        }
    }

    private class Node<T> {

        public T data;
        public Node<T> next;
        public Node<T> prev;

        public Node(Node<T> prev, T data, Node<T> next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }
}


