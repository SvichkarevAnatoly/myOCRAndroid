package ru.myocr.db;

import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.google.gson.Gson;

import nl.littlerobots.cupboard.tools.gson.GsonFieldConverter;
import nl.littlerobots.cupboard.tools.provider.CupboardContentProvider;
import nl.qbusict.cupboard.Cupboard;
import nl.qbusict.cupboard.CupboardBuilder;
import ru.myocr.BuildConfig;
import ru.myocr.model.City;
import ru.myocr.model.Receipt;
import ru.myocr.model.ReceiptItem;
import ru.myocr.model.ReceiptTag;
import ru.myocr.model.Tag;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;
import static nl.qbusict.cupboard.CupboardFactory.setCupboard;

public class ReceiptContentProvider extends CupboardContentProvider {

    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";
    public static final int DATABASE_VERSION = 3;
    public static final String _ID = "_id";

    public static final String PATH_RECEIPT_BY_TAG = "PATH_RECEIPT_BY_TAG";
    public static final String PATH_RECEIPT_TAG = "PATH_RECEIPT_TAG";
    public static final String PATH_DELETE_TAG = "PATH_DELETE_TAG";
    public static final Uri URI_RECEIPT_BY_TAG = Uri.parse("content://" + AUTHORITY + "/" + PATH_RECEIPT_BY_TAG);
    public static final Uri URI_RECEIPT_TAG = Uri.parse("content://" + AUTHORITY + "/" + PATH_RECEIPT_TAG);
    public static final Uri URI_DELETE_TAG = Uri.parse("content://" + AUTHORITY + "/" + PATH_DELETE_TAG);
    private static final UriMatcher URI_MATCHER;

    static {
        URI_MATCHER = new UriMatcher(0);
        URI_MATCHER.addURI(AUTHORITY, PATH_RECEIPT_BY_TAG, 0);
        URI_MATCHER.addURI(AUTHORITY, PATH_RECEIPT_TAG, 1);
        URI_MATCHER.addURI(AUTHORITY, PATH_DELETE_TAG, 2);
    }

    static {
        Cupboard cupboard = new CupboardBuilder()
                .useAnnotations()
                .registerFieldConverter(Receipt.Market.class,
                        new GsonFieldConverter<>(new Gson(), Receipt.Market.class))
                .build();

        setCupboard(cupboard);

        cupboard().register(Receipt.class);
        cupboard().register(ReceiptItem.class);
        cupboard().register(City.class);
        cupboard().register(Tag.class);
        cupboard().register(ReceiptTag.class);
    }

    public ReceiptContentProvider() {
        super(AUTHORITY, DATABASE_VERSION);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        if (0 == URI_MATCHER.match(uri)) {
            String queryTags = String.format("SELECT Tag._id FROM Tag WHERE Tag.tag LIKE '%%%s%%\'", selectionArgs[0]);
            String queryReceiptIds = String.format("SELECT DISTINCT ReceiptTag.receiptId FROM ReceiptTag " +
                    "WHERE ReceiptTag.tagId IN (%s)", queryTags);
            String query = String.format("SELECT * FROM Receipt WHERE Receipt._id IN (%s)",
                    queryReceiptIds);
            return rawQuery(query, null);
        } else if (1 == URI_MATCHER.match(uri)) {
            String queryTagsIds = "SELECT DISTINCT ReceiptTag.tagId FROM ReceiptTag WHERE ReceiptTag.receiptId = ?";
            String query = String.format("SELECT * FROM Tag WHERE Tag._id IN (%s)",
                    queryTagsIds);
            return rawQuery(query, selectionArgs);
        } else {
            return super.query(uri, projection, selection, selectionArgs, sortOrder);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (2 == URI_MATCHER.match(uri)) {
            SQLiteDatabase db = getDatabaseHelper().getWritableDatabase();
            String queryTags = "SELECT Tag._id FROM Tag WHERE Tag.tag = ?";
            String query = String.format("DELETE FROM ReceiptTag WHERE ReceiptTag.receiptId = ? AND " +
                            "ReceiptTag.tagId in (%s)",
                    queryTags);
            db.execSQL(query, selectionArgs);
            return 0;
        } else {
            return super.delete(uri, selection, selectionArgs);
        }
    }

    private Cursor rawQuery(String query, String[] selectionArgs) {
        SQLiteDatabase db = getDatabaseHelper().getWritableDatabase();
        return db.rawQuery(query, selectionArgs);
    }
}
