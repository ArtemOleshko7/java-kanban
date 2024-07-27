package main;

import model.Task;

public class Node<T extends Task> {
    private T task;
    private Node<T> next;
    private Node<T> prev;

    // Конструктор для создания узла с задачей и ссылками на соседние узлы
    public Node(Node<T> prev, T task, Node<T> next) {
        this.prev = prev;
        this.task = task;
        this.next = next;
    }

    // Новый конструктор для создания узла только с задачей
    public Node(T task) {
        this.task = task;
        this.next = null;
        this.prev = null;
    }

    public T getTask() {
        return task;
    }

    public void setTask(T task) {
        this.task = task;
    }

    public Node<T> getNext() {
        return next;
    }

    public void setNext(Node<T> next) {
        this.next = next;
    }

    public Node<T> getPrev() {
        return prev;
    }

    public void setPrev(Node<T> prev) {
        this.prev = prev;
    }
}

