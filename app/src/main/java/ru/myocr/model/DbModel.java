package ru.myocr.model;


import android.content.ContentValues;
import android.net.Uri;

import java.io.Serializable;

import nl.littlerobots.cupboard.tools.provider.UriHelper;
import nl.qbusict.cupboard.ProviderCompartment;
import ru.myocr.App;
import ru.myocr.db.ReceiptContentProvider;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public abstract class DbModel<T extends DbModel> implements Serializable {

    public Long _id;

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

    public Long getId() {
        return _id == null ? -1 : _id;
    }

    public void setId(Long id) {
        _id = id;
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