package ru.myocr.viewmodel;


import rx.Observable;

public interface DataSource<T> {

    Observable<T> get();

}
