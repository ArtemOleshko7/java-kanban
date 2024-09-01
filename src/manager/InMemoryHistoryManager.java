package manager;

import model.Epic;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final TasksDoubleList<Task> tasksDoubleList = new TasksDoubleList<>();
    private final Map<Integer, Node<Task>> nodeMap = new HashMap<>();

    @Override
    public void add(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }

        System.out.println("Adding task with ID: " + task.getId());
        Node<Task> existingNode = nodeMap.get(task.getId());
        if (existingNode != null) {
            System.out.println("Updating task: " + task.getId());
            tasksDoubleList.removeNode(existingNode, nodeMap);
        }

        tasksDoubleList.linkLast(task);
        nodeMap.put(task.getId(), tasksDoubleList.tail);
        System.out.println("Task added. Current nodeMap: " + nodeMap.keySet());
    }

    @Override
    public List<Task> getHistory() {
        List<Task> tasks = new ArrayList<>();
        Node<Task> current = tasksDoubleList.head; // Используем head из tasksDoubleList
        while (current != null) {
            tasks.add(current.data);
            current = current.next;
        }
        return tasks;
    }

    public void clearHistory() {
        nodeMap.clear();
        tasksDoubleList.clear(); // Здесь вам нужно реализовать метод clear в TasksDoubleList
    }


    @Override
    public void remove(int id) {
        System.out.println("Current History Node Map before removal: " + nodeMap.keySet());
        Node<Task> node = nodeMap.remove(id);
        System.out.println("Attempting to remove node with ID: " + id + ", Node found: " + (node != null));

        if (node != null) {
            tasksDoubleList.removeNode(node, nodeMap);
            System.out.println("Node removed from the doubly linked list.");
            if (node.data instanceof Epic) {
                Epic epic = (Epic) node.data;
                for (int subTaskId : epic.getSubtaskOfEpicIDs()) {
                    System.out.println("Recursively removing subtask with ID: " + subTaskId);
                    remove(subTaskId);
                }
            }
        } else {
            System.out.println("Node with ID " + id + " not found in history.");
        }

        System.out.println("Current History Node Map after removal: " + nodeMap.keySet());
    }

    public static class TasksDoubleList<T extends Task> {
        private Node<T> head;
        private Node<T> tail;

        public void linkLast(T task) {
            final Node<T> newNode = new Node<>(task, tail, null);
            if (tail != null) {
                tail.next = newNode;
            } else {
                head = newNode;
            }
            tail = newNode;
        }

        /*public List<Task> getTask() {
            List<Task> history = new ArrayList<>();
            Node<T> task = head;
            while (task != null) {
                history.add(task.data); // Здесь 'task.data' имеет тип Task
                task = task.next;
            }
            return history;
        }*/

        public void removeNode(Node<T> taskNode, Map<Integer, Node<T>> nodeMap) {
            if (taskNode == null) {
                throw new IllegalArgumentException("taskNode cannot be null");
            }

            Node<T> prevNode = taskNode.prev;
            Node<T> nextNode = taskNode.next;

            // Обновляем ссылки на соседние узлы
            if (prevNode != null) {
                prevNode.next = nextNode;
            } else {
                head = nextNode; // Если это голова
            }

            if (nextNode != null) {
                nextNode.prev = prevNode;
            } else {
                tail = prevNode; // Если это хвост
            }

            // Удаляем узел из nodeMap
            if (nodeMap.remove(taskNode.data.getId()) != null) {

            }

            // Очищаем ссылки у удаляемого узла
            taskNode.prev = null;
            taskNode.next = null;
        }

        public void clear() {
            head = null;
            tail = null;
        }
    }

    private static class Node<T> {
        public T data;
        public Node<T> next;
        public Node<T> prev;

        public Node(T data, Node<T> prev, Node<T> next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }
}