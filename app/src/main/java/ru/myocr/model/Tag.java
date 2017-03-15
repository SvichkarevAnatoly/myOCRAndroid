package ru.myocr.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import nl.littlerobots.cupboard.tools.provider.UriHelper;
import ru.myocr.db.ReceiptContentProvider;

public class Tag extends DbModel<Tag> {

    @SerializedName("tag")
    public String tag;

    public Tag() {
    }

    public static List<Tag> getAllTags() {
        return getProviderCompartment().query(UriHelper.with(ReceiptContentProvider.AUTHORITY)
                .getUri(Tag.class), Tag.class).list();
    }


    @Override
    protected Class<Tag> getEntityClass() {
        return Tag.class;
    }
}
