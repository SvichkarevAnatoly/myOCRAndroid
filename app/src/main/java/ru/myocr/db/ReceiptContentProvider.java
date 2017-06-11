package ru.myocr.db;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.google.gson.Gson;

import nl.littlerobots.cupboard.tools.gson.GsonFieldConverter;
import nl.littlerobots.cupboard.tools.provider.CupboardContentProvider;
import nl.qbusict.cupboard.Cupboard;
import nl.qbusict.cupboard.CupboardBuilder;
import nl.qbusict.cupboard.convert.EntityConverter;
import nl.qbusict.cupboard.convert.FieldConverter;
import ru.myocr.BuildConfig;
import ru.myocr.model.City;
import ru.myocr.model.Receipt;
import ru.myocr.model.ReceiptItem;
import ru.myocr.model.Shop;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;
import static nl.qbusict.cupboard.CupboardFactory.setCupboard;

public class ReceiptContentProvider extends CupboardContentProvider {

    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";
    public static final int DATABASE_VERSION = 4;
    public static final String _ID = "_id";

    public static final String PATH_RECEIPT_SEARCH = "PATH_RECEIPT_SEARCH";

    public static final Uri URI_RECEIPT_SEARCH = Uri.parse("content://" + AUTHORITY + "/" + PATH_RECEIPT_SEARCH);

    private static final UriMatcher URI_MATCHER;

    private static final int CODE_SEARCH = 0;

    static {
        URI_MATCHER = new UriMatcher(0);
        URI_MATCHER.addURI(AUTHORITY, PATH_RECEIPT_SEARCH, CODE_SEARCH);
    }

    static {
        Cupboard cupboard = new CupboardBuilder()
                .useAnnotations()
                .registerFieldConverter(Receipt.Market.class,
                        new GsonFieldConverter<>(new Gson(), Receipt.Market.class))
                .registerFieldConverter(Uri.class,
                        new FieldConverter<Uri>() {
                            @Override
                            public Uri fromCursorValue(Cursor cursor, int columnIndex) {
                                return Uri.parse(cursor.getString(columnIndex));
                            }

                            @Override
                            public void toContentValue(Uri value, String key, ContentValues values) {
                                values.put(key, value.toString());
                            }

                            @Override
                            public EntityConverter.ColumnType getColumnType() {
                                return EntityConverter.ColumnType.TEXT;
                            }
                        })
                .build();

        setCupboard(cupboard);

        cupboard().register(Receipt.class);
        cupboard().register(ReceiptItem.class);
        cupboard().register(City.class);
        cupboard().register(Shop.class);
    }

    public ReceiptContentProvider() {
        super(AUTHORITY, DATABASE_VERSION);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        if (CODE_SEARCH == URI_MATCHER.match(uri)) {
            String queryByTitle = String.format("SELECT * FROM Receipt WHERE Receipt.market LIKE '%%%s%%'", selectionArgs[0]);
            return rawQuery(queryByTitle, null);
        } else {
            return super.query(uri, projection, selection, selectionArgs, sortOrder);
        }
    }

    private Cursor rawQuery(String query, String[] selectionArgs) {
        SQLiteDatabase db = getDatabaseHelper().getWritableDatabase();
        return db.rawQuery(query, selectionArgs);
    }
}
