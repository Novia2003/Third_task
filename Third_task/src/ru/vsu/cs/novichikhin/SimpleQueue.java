package ru.vsu.cs.novichikhin;

public interface SimpleQueue<T> {
    void add(T value);

    T poll();

    T element();

    int count();

    boolean empty();
}