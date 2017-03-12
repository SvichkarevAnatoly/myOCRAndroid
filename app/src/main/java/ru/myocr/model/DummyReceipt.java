package ru.myocr.model;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class DummyReceipt {

    public static final List<Receipt> LIST;
    public static final Random RANDOM;

    static {
        LIST = new ArrayList<>();
        RANDOM = new Random();

        for (int i = 0; i < 3 + RANDOM.nextInt(3); i++) {
            LIST.add(buildItem());
        }
    }

    private static Receipt buildItem() {

        Receipt receipt = new Receipt();

        receipt.cashier = "Иванова Н. А";
        receipt.date = new Date();
        receipt.total_cost_sum = RANDOM.nextInt();
        receipt.market = new Receipt.Market();
        receipt.market.title = "Ашан";

        receipt.items = new ArrayList<>();

        for (int i = 0; i < 5 + RANDOM.nextInt(5); i++) {
            int price = 100 + RANDOM.nextInt(100);
            float amount = RANDOM.nextInt(100) / 100.0f;
            receipt.items.add(new ReceiptItem(i, "Продукт " + RANDOM.nextInt(100),
                    price, amount, (int) (price * amount)));
        }
        return receipt;
    }
}
