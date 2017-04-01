package ru.myocr.util.collection;


public interface Stack<T> {
    void push(T e);

    T pop();

    T peek();

    int size();
}
