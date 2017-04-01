package ru.myocr.util.collection;

import java.util.ArrayList;

public class ArrayStack<T> extends ArrayList<T> implements Stack<T> {
    @Override
    public void push(T e) {
        add(e);
    }

    @Override
    public T pop() {
        return remove(size() - 1);
    }

    @Override
    public T peek() {
        return get(size() - 1);
    }
}
