package ru.myocr.model;

public class ReceiptTag extends DbModel<ReceiptTag> {

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
