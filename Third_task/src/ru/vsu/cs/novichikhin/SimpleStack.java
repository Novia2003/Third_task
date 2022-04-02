package ru.vsu.cs.novichikhin;

public interface SimpleStack<T> {
    void push(T value);

    T pop();

    T peek();

    int count();

    boolean empty();
}
