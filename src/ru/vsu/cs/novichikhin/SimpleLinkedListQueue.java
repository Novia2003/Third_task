package ru.vsu.cs.novichikhin;

import java.util.NoSuchElementException;

public class SimpleLinkedListQueue<T> extends SimpleLinkedList<T> implements SimpleQueue<T> {
    @Override
    public void add(T value) {
        this.addLast(value);
    }

    @Override
    public T poll() {
        T result = this.element();
        this.removeFirst();
        return result;
    }

    @Override
    public T element() {
        if (this.empty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        return this.getFirst();
    }

    @Override
    public int count() {
        return this.size();
    }

    @Override
    public boolean empty() {
        return this.count() == 0;
    }
}
