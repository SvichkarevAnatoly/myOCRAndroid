package ru.myocr.model;


import android.net.Uri;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import nl.littlerobots.cupboard.tools.provider.UriHelper;
import ru.myocr.App;
import ru.myocr.db.ReceiptContentProvider;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class DummyReceipt {

    public static final List<Receipt> LIST;
    public static final Random RANDOM;

    private static final List<String> DUMMY_PRODUCTS =
            Arrays.asList("Шоколад 200 гр", "Молоко простаквашино 1 л", "Мясо говядниа", "Макароны Макфа",
                    "Чай Griendfield");

    private static final List<String> DUMMY_SHOPS =
            Arrays.asList("Ашан", "Okey", "ТЦ Академгородка", "Холидей",
                    "Быстроном");


    static {
        LIST = new ArrayList<>();
        RANDOM = new Random();

        for (int i = 0; i < 10 + RANDOM.nextInt(10); i++) {
            LIST.add(buildItem());
        }

    }

    public static void addToDb() {
        Receipt receipt = buildItem();

        Uri uri = cupboard().withContext(App.getContext())
                .put(UriHelper.with(ReceiptContentProvider.AUTHORITY).getUri(Receipt.class),
                        receipt);
        Long id = Long.valueOf(uri.getLastPathSegment());

        for (ReceiptItem item : receipt.items) {
            item.receiptId = id;
        }

        cupboard().withContext(App.getContext())
                .put(UriHelper.with(ReceiptContentProvider.AUTHORITY).getUri(ReceiptItem.class),
                        ReceiptItem.class, receipt.items);
    }

    private static Receipt buildItem() {

        Receipt receipt = new Receipt();

        receipt.cashier = "Иванова Н. А";
        receipt.date = new Date(System.currentTimeMillis() - RANDOM.nextInt(100000000));
        receipt.totalCostSum = RANDOM.nextInt(200) * 1000;
        receipt.market = new Receipt.Market();
        receipt.market.title = DUMMY_SHOPS.get(RANDOM.nextInt(DUMMY_SHOPS.size()));
        receipt.market.inn = "" + (100000 + RANDOM.nextInt(100000));
        receipt.market.address = "г. Новосибирск, ул. Николаева 12а";

        receipt.items = new ArrayList<>();

        for (int i = 0; i < 5 + RANDOM.nextInt(10); i++) {
            int price = 100 + RANDOM.nextInt(100);
            float amount = RANDOM.nextInt(100) / 100.0f;
            receipt.items.add(new ReceiptItem(
                    i,
                    DUMMY_PRODUCTS.get(RANDOM.nextInt(DUMMY_PRODUCTS.size())),
                    receipt.date,
                    price,
                    amount,
                    (int) (price * amount)));
        }
        return receipt;
    }
}
