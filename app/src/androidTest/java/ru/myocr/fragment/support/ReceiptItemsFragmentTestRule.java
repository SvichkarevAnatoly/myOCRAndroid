package ru.myocr.fragment.support;

import android.support.test.rule.ActivityTestRule;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import org.junit.Assert;

import ru.myocr.FragmentRunner;
import ru.myocr.R;
import ru.myocr.api.ocr.OcrReceiptResponse;
import ru.myocr.fragment.ocr.ReceiptItemsFragment;

public class ReceiptItemsFragmentTestRule extends ActivityTestRule<FragmentRunner> {

    private OcrReceiptResponse data;

    public ReceiptItemsFragmentTestRule() {
        super(FragmentRunner.class, true, false);
    }

    public void setData(OcrReceiptResponse data) {
        this.data = data;
    }

    @Override
    protected void afterActivityLaunched() {
        super.afterActivityLaunched();

        getActivity().runOnUiThread(() -> {
            try {
                //Instantiate and insert the fragment into the container layout
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();

                final ReceiptItemsFragment receiptItemsFragment = ReceiptItemsFragment.newInstance(data);

                transaction.replace(R.id.container, receiptItemsFragment);
                transaction.commit();
            } catch (Exception e) {
                Assert.fail(String.format("%s: Could not insert %s into FragmentRunner: %s",
                        getClass().getSimpleName(),
                        ReceiptItemsFragment.class.getSimpleName(),
                        e.getMessage()));
            }
        });
    }
}