package ru.vsu.cs.novichikhin;

import java.util.NoSuchElementException;

public class SimpleLinkedListStack<T> extends SimpleLinkedList<T> implements SimpleStack<T> {
    @Override
    public void push(T value) {
        this.addFirst(value);
    }

    @Override
    public T pop() {
        T result = this.peek();
        this.removeFirst();
        return result;
    }

    @Override
    public T peek() {
        if (this.empty()) {
            throw new NoSuchElementException("Stack is empty");
        }
        return this.getFirst();
    }

    @Override
    public int count() {
        return super.size();
    }

    @Override
    public boolean empty() {
        return this.count() == 0;
    }
}
