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

    public static final List<String> DUMMY_PRODUCTS =
            Arrays.asList("Шоколад 200 гр", "Молоко простаквашино 1 л", "Мясо говядниа", "Макароны Макфа",
                    "Чай Griendfield", "Кетчуп 100 гр", "Консервы", "Вода питьевая",
                    "Майонез Calve", "Шоколад KIT KAT", "Фарш Домашний хол 1кг", "Мандарины Марокко 1кг",
                    "Томаты Южниые 1кг", "Хдеь НОВОСИБХЛЕБ тостовый", "Сыр плавленный ХОХЛАНД",
                    "Нектар Фруктовый", "Улитка с корицей 100гр", "Бананы восток", "Злаки Любятово",
                    "Пирожки мясные", "Кока-Кола 0.5л", "Час листовой Lipton", "Салфетки 1000 шт",
                    "Печенье овсяные Любятово", "Яйца Новосибские 10 шт");

    private static final List<String> DUMMY_SHOPS =
            Arrays.asList("Ашан", "OKEY", "ТЦ Академгородка", "Холидей",
                    "Быстроном", "Яблоко", "Лента");

    private static final List<String> DUMMY_ADDRESS =
            Arrays.asList("г. Новосибирск, ул. Николаева 12а", "г. Новосибирск, ул. Пирогова 5",
                    "г. Новосибирск, ул. Ляпунова 13/2");

    private static final List<String> DUMMY_TAGS =
            Arrays.asList("Наличными", "Работа", "Новый год");


    static {
        LIST = new ArrayList<>();
        RANDOM = new Random();

        for (int i = 0; i < 10 + RANDOM.nextInt(10); i++) {
            LIST.add(buildItem());
        }

    }

    public static String getDummyProduct() {
        return DUMMY_PRODUCTS.get(RANDOM.nextInt(DUMMY_PRODUCTS.size()));
    }

    public static void addToDb() {
        addToDb(true);
    }

    public static void addToDb(boolean addTag) {
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

        if (addTag) {
            String tag = DUMMY_TAGS.get(RANDOM.nextInt(DUMMY_TAGS.size()));
            addTag(tag, id);
        }
    }

    private static void addTag(String tag, long receiptId) {
        Tag newTag = new Tag(tag);
        Uri tagUri = DbModel.getUriHelper().getUri(Tag.class);
        Tag existingTag = DbModel.getProviderCompartment()
                .query(tagUri, Tag.class)
                .withSelection("tag = ?", tag)
                .get();
        Long id;

        if (existingTag == null) {
            Uri uri = DbModel.getProviderCompartment().put(tagUri, newTag);
            id = Long.valueOf(uri.getLastPathSegment());
        } else {
            id = existingTag._id;
        }

        ReceiptTag receiptTag = new ReceiptTag(receiptId, id);
        receiptTag.updateDb();
    }
    private static Receipt buildItem() {

        Receipt receipt = new Receipt();

        receipt.cashier = "Иванова Н. А";
        receipt.date = new Date(System.currentTimeMillis() - RANDOM.nextInt(1000) * 1000000L);
        receipt.market = new Receipt.Market();
        receipt.market.title = DUMMY_SHOPS.get(RANDOM.nextInt(DUMMY_SHOPS.size()));
        receipt.market.inn = "" + (100000 + RANDOM.nextInt(100000));
        receipt.market.address = DUMMY_ADDRESS.get(RANDOM.nextInt(DUMMY_ADDRESS.size()));

        receipt.items = new ArrayList<>();

        int totalCost = 0;

        int size = 5 + RANDOM.nextInt(5);
        for (int i = 0; i < size; i++) {
            int price = 10000 + 500 * RANDOM.nextInt(100);
            float amount = RANDOM.nextInt(100) / 100.0f;
            int cost = (int) (price);

            receipt.items.add(new ReceiptItem(
                    i,
                    DUMMY_PRODUCTS.get(RANDOM.nextInt(DUMMY_PRODUCTS.size())),
                    receipt.date,
                    price,
                    amount,
                    cost));

            totalCost += cost;
        }


        receipt.totalCostSum = totalCost;
        return receipt;
    }

}
