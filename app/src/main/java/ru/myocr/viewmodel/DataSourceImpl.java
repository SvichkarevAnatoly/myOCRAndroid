package ru.myocr.viewmodel;


import ru.myocr.util.RxUtil;
import rx.Observable;
import rx.functions.Func0;

public class DataSourceImpl<T> implements DataSource<T> {

    private Func0<T> getter;

    public DataSourceImpl(Func0<T> getter) {
        this.getter = getter;
    }

    @Override
    public Observable<T> get() {
        return RxUtil.getApiObservable(getter);
    }
}
