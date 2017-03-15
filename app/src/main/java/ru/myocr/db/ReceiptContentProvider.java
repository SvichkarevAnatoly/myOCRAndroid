package ru.myocr.db;

import com.google.gson.Gson;

import nl.littlerobots.cupboard.tools.gson.GsonFieldConverter;
import nl.littlerobots.cupboard.tools.provider.CupboardContentProvider;
import nl.qbusict.cupboard.Cupboard;
import nl.qbusict.cupboard.CupboardBuilder;
import ru.myocr.BuildConfig;
import ru.myocr.model.City;
import ru.myocr.model.Receipt;
import ru.myocr.model.ReceiptItem;
import ru.myocr.model.Tag;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;
import static nl.qbusict.cupboard.CupboardFactory.setCupboard;

public class ReceiptContentProvider extends CupboardContentProvider {

    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";
    public static final int DATABASE_VERSION = 3;

    public static final String _ID = "_id";

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
    }

    public ReceiptContentProvider() {
        super(AUTHORITY, DATABASE_VERSION);
    }
}
