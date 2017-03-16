package ru.myocr.model;

import android.net.Uri;

public class ReceiptTag extends DbModel<ReceiptTag> {

    public static final Uri URI;

    static {
        URI = DbModel.getUriHelper().getUri(ReceiptTag.class);
    }
    public Long receiptId;
    public Long tagId;

    public ReceiptTag() {
    }

    public ReceiptTag(Long receiptId, Long tagId) {
        this.receiptId = receiptId;
        this.tagId = tagId;
    }

    @Override
    protected Class<ReceiptTag> getEntityClass() {
        return ReceiptTag.class;
    }
}
