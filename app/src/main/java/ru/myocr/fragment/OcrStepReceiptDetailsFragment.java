package ru.myocr.fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import nl.littlerobots.cupboard.tools.provider.UriHelper;
import ru.myocr.App;
import ru.myocr.R;
import ru.myocr.activity.TicketActivity;
import ru.myocr.api.ApiHelper;
import ru.myocr.api.SavePriceRequest;
import ru.myocr.api.SavePriceRequest.ReceiptPriceItem;
import ru.myocr.databinding.FragmentOcrStepReceiptDetailsBinding;
import ru.myocr.db.ReceiptContentProvider;
import ru.myocr.model.DbModel;
import ru.myocr.model.Receipt;
import ru.myocr.model.ReceiptData;
import ru.myocr.model.ReceiptItem;
import ru.myocr.preference.Preference;
import ru.myocr.util.PriceUtil;
import ru.myocr.util.TimeUtil;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;
import static ru.myocr.activity.ReceiptOcrActivity.ARG_OCR_RESPONSE;
import static ru.myocr.activity.TicketActivity.ARG_RECEIPT;


public class OcrStepReceiptDetailsFragment extends Fragment {

    private ReceiptData receiptData;
    private FragmentOcrStepReceiptDetailsBinding binding;
    private Calendar date;

    public OcrStepReceiptDetailsFragment() {
        // Required empty public constructor
    }

    public static OcrStepReceiptDetailsFragment newInstance(ReceiptData receiptData) {
        OcrStepReceiptDetailsFragment fragment = new OcrStepReceiptDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_OCR_RESPONSE, receiptData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiptData = (ReceiptData) getArguments().getSerializable(ARG_OCR_RESPONSE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ocr_step_receipt_details, container, false);
        initUi();
        return binding.getRoot();
    }

    public void onClickSave() {
        sentToServer();
    }

    public void saveToLocalDb() {
        Receipt receipt = new Receipt();

        receipt.market = new Receipt.Market(binding.market.getText().toString(),
                binding.address.getText().toString(),
                binding.inn.getText().toString());

        receipt.date = date.getTime();

        List<ReceiptItem> items = new ArrayList<>();
        int idx = 0;
        for (Pair<String, String> item : receiptData.getProductsPricesPairs()) {
            idx++;
            int price = PriceUtil.getIntValue(item.second);
            items.add(new ReceiptItem(idx, item.first, receipt.date, price, 1f, price));
        }
        receipt.items = items;

        receipt.totalCostSum = Integer.valueOf(binding.total.getText().toString()) * 100;

        // Save to Db
        Uri uri = DbModel.getProviderCompartment().put(Receipt.URI, receipt);
        Long id = Long.valueOf(uri.getLastPathSegment());
        for (ReceiptItem item : receipt.items) {
            item.receiptId = id;
        }
        cupboard().withContext(App.getContext())
                .put(UriHelper.with(ReceiptContentProvider.AUTHORITY).getUri(ReceiptItem.class),
                        ReceiptItem.class, receipt.items);


        getActivity().finish();
        Intent intent = new Intent(getActivity(), TicketActivity.class);
        intent.putExtra(ARG_RECEIPT, id);
        startActivity(intent);
    }

    public void sentToServer() {
        final SavePriceRequest savePriceRequest = initSavePriceRequest();

        ApiHelper.makeApiRequest(savePriceRequest, ApiHelper::save,
                throwable -> Toast.makeText(getContext(), "Ошибка сохранения", Toast.LENGTH_SHORT).show(),
                integer -> {
                    Toast.makeText(getActivity(), "Успешно сохранено " + integer + " записей", Toast.LENGTH_SHORT).show();
                    saveToLocalDb();
                }, null);
    }

    @NonNull
    private SavePriceRequest initSavePriceRequest() {
        final List<ReceiptPriceItem> items = convert(receiptData.getProductsPricesPairs());
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        final String city = "Nsk";
        final String shop = "Auchan";
        final String time = TimeUtil.parse(date.getTime());
        return new SavePriceRequest(city, shop, time, items);
    }

    private List<ReceiptPriceItem> convert(List<Pair<String, String>> productPricePairs) {
        final ArrayList<ReceiptPriceItem> items = new ArrayList<>();
        for (Pair<String, String> pair : productPricePairs) {
            if (pair.first == null || pair.second == null) {
                break;
            }
            final int price = Integer.parseInt(pair.second.replace(".", ""));
            items.add(new ReceiptPriceItem(pair.first, price));
        }

        return items;
    }

    public void initUi() {
        date = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            this.date.set(Calendar.YEAR, year);
            this.date.set(Calendar.MONTH, monthOfYear);
            this.date.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            updateLabel();
        };

        binding.date.setOnClickListener(v -> new DatePickerDialog(getActivity(), date, this.date
                .get(Calendar.YEAR), this.date.get(Calendar.MONTH),
                this.date.get(Calendar.DAY_OF_MONTH)).show());

        updateLabel();

        double total = 0;
        for (Pair<String, String> item : receiptData.getProductsPricesPairs()) {
            total += Double.valueOf(item.second);
        }

        binding.total.setText(String.valueOf((int) total));
        binding.market.setText(Preference.getString(Preference.SHOP));
    }

    private void updateLabel() {
        String myFormat = "dd.MM.yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        binding.date.setText(sdf.format(date.getTime()));
    }
}
