package ru.myocr.model;


import android.content.ContentValues;
import android.net.Uri;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import nl.littlerobots.cupboard.tools.provider.UriHelper;
import nl.qbusict.cupboard.ProviderCompartment;
import ru.myocr.App;
import ru.myocr.db.ReceiptContentProvider;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public abstract class DbModel<T extends DbModel> implements Serializable {

    @SerializedName("id")
    protected Long _id = -1L;

    public DbModel() {
    }

    public DbModel(Long _id) {
        this._id = _id;
    }

    public static <T> T byId(Uri uri,
                             Long id, Class<T> entityClass) {
        return getProviderCompartment().query(uri, entityClass)
                .withSelection(ReceiptContentProvider._ID + "=?", String.valueOf(id)).get();
    }

    public static ProviderCompartment getProviderCompartment() {
        return cupboard().withContext(App.getContext());
    }

    public static UriHelper getUriHelper() {
        return UriHelper.with(ReceiptContentProvider.AUTHORITY);
    }

    public static <T> List<T> getAll(Class<T> entityClass) {
        return getProviderCompartment().query(UriHelper.with(ReceiptContentProvider.AUTHORITY)
                .getUri(entityClass), entityClass).list();
    }

    public static <T> void deleteAll(Class<T> entityClass) {
        getProviderCompartment().delete(UriHelper.with(ReceiptContentProvider.AUTHORITY)
                .getUri(entityClass), "", (String[]) null);
    }

    public Long getId() {
        return _id == null ? -1 : _id;
    }

    public void setId(Long id) {
        _id = id;
    }

    public void putIfNotExist() {
        T t = cupboard().withContext(App.getContext())
                .query(getTableUri(), getEntityClass())
                .withSelection("_id = ?", String.valueOf(_id)).get();
        if (t != null) {
            this._id = t._id;
        }
        getProviderCompartment().put(getTableUri(), this);
    }

    public ContentValues buildContentValues() {
        return cupboard().withEntity(getEntityClass()).toContentValues((T) this);
    }

    protected abstract Class<T> getEntityClass();

    public Uri getTableUri() {
        return UriHelper.with(ReceiptContentProvider.AUTHORITY)
                .getUri(getEntityClass());
    }

    public void updateDb() {
        if (_id != null && _id >= 0) {
            String where = ReceiptContentProvider._ID + "=?";
            String[] args = new String[]{_id + ""};
            getProviderCompartment().update(getTableUri(), buildContentValues(), where, args);
        } else {
            Uri put = getProviderCompartment().put(getTableUri(), this);
            _id = Long.valueOf(put.getLastPathSegment());
        }
    }

    public boolean delete() {
        if (!isValidId()) {
            throw new IllegalStateException("Trying to remove entity ");
        }
        return getProviderCompartment().delete(getTableUri(), this) == 1;
    }

    public boolean isValidId() {
        return _id != null && _id >= 0;
    }
}