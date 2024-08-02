package manager;

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
        if (nodeMap.containsKey(task.getId())) {
            remove(task.getId());
        }
        Task taskHistory = new Task(task.getNameTask(), task.getDescriptionTask(), task.getStatus(), task.getId());
        tasksDoubleList.linkLast(taskHistory);
        nodeMap.put(task.getId(), tasksDoubleList.tail);
    }

    @Override
    public List<Task> getHistory() {
        return tasksDoubleList.getTask();
    }

    @Override
    public void remove(int id) {
        tasksDoubleList.removeNode(nodeMap.remove(id));
    }

    public static class TasksDoubleList<T> {
        private Node<T> head; // Поле head теперь приватное
        private Node<T> tail; // Поле tail теперь приватное
        private int size = 0; // Поле size теперь приватное

        public void linkLast(T task) {
            final Node<T> oldTail = tail;
            final Node<T> newNode = new Node<>(task, oldTail, null);
            tail = newNode;
            if (oldTail == null) {
                head = newNode;
            } else {
                oldTail.next = newNode;
            }
            size++;
        }

        public List<T> getTask() {
            List<T> history = new ArrayList<>();
            Node<T> task = head; // Начинаем с головы списка
            while (task != null) { // Пока есть узлы
                history.add(task.data); // Добавляем данные в историю
                task = task.next; // Переходим к следующему узлу
            }
            return history; // Возвращаем собранный список
        }

        public void removeNode(Node<T> taskNode) {
            if (taskNode == null) {
                throw new IllegalArgumentException("taskNode cannot be null");
            }

            Node<T> prevNode = taskNode.prev;
            Node<T> nextNode = taskNode.next;

            size--;
            if (size == 0) {
                head = tail = null;
            } else if (taskNode == tail) {
                tail = prevNode;
                if (tail != null) {
                    tail.next = null; // Проверка на null, чтобы избежать NPE
                }
            } else if (taskNode == head) {
                head = nextNode;
                if (head != null) {
                    head.prev = null; // Проверка на null, чтобы избежать NPE
                }
            } else {
                if (prevNode != null) {
                    prevNode.next = nextNode;
                }
                if (nextNode != null) {
                    nextNode.prev = prevNode;
                }
            }
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