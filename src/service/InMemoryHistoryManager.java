package service;

import model.Task;
import main.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private Node<Task> head;
    private Node<Task> tail;
    private Map<Integer, Node<Task>> historyMap = new HashMap<>();

    @Override
    public void add(Task task) {
        if (task == null) {
            return; // Не добавляем null
        }
        remove(task.getId());
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        if (historyMap.containsKey(id)) {
            removeNode(historyMap.get(id));
            historyMap.remove(id);
        }
    }

    @Override
    public void removeAll() {
        historyMap.clear();
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void linkLast(Task task) {
        Node<Task> newNode = new Node<>(tail, task, null); // Передаем текущий tail как prev, и null как next

        if (tail == null) { // Если список пустой
            head = newNode;
        } else {
            tail.setNext(newNode); // Связываем предыдущий tail с новым узлом
        }
        tail = newNode; // Обновляем tail на новый узел
        historyMap.put(task.getId(), newNode); // Обновляем историю
    }

    public List<Task> getTasks() {
        List<Task> listOfTasks = new ArrayList<>();
        Node<Task> node = head;
        while (node != null) {
            listOfTasks.add(node.getTask());
            node = node.getNext();
        }
        return listOfTasks;
    }

    private void removeNode(Node<Task> node) {
        Node<Task> prevNode = node.getPrev();
        Node<Task> nextNode = node.getNext();
        if (historyMap.size() == 1) {
            head = null;
            tail = null;
        } else if (historyMap.size() > 1) {
            if (prevNode == null) {
                head = nextNode;
                nextNode.setPrev(null);
            } else if (nextNode == null) {
                tail = prevNode;
                prevNode.setNext(null);
            } else {
                prevNode.setNext(nextNode);
                nextNode.setPrev(prevNode);
            }
        }
    }
}
