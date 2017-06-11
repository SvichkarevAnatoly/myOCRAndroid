package ru.myocr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.text.SimpleDateFormat;
import java.util.Locale;

import nl.littlerobots.cupboard.tools.provider.UriHelper;
import ru.myocr.R;
import ru.myocr.db.ReceiptContentProvider;
import ru.myocr.fragment.ReceiptViewFragment;
import ru.myocr.fragment.ocr.ReceiptPhotoFragment;
import ru.myocr.model.DbModel;
import ru.myocr.model.Receipt;
import ru.myocr.model.ReceiptItem;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class TicketActivity extends AppCompatActivity {

    public static final String ARG_RECEIPT = "ARG_RECEIPT";

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private Receipt receipt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        long id = getIntent().getLongExtra(ARG_RECEIPT, -1);
        receipt = DbModel.getById(id, Receipt.class);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setTitle("Чек от " + new SimpleDateFormat("d MMM ", new Locale("ru", "RU")).format(receipt.date));

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        ((TabLayout) findViewById(R.id.tab_layout)).setupWithViewPager(mViewPager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ticket, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_delete) {
            delete();
        } else if (id == R.id.action_edit) {
            edit();
        }

        return super.onOptionsItemSelected(item);
    }

    private void edit() {
        Intent intent = new Intent(this, AddReceiptActivity.class);
        intent.putExtra(AddReceiptActivity.ARG_OCR_RECEIPT, receipt._id);
        intent.putExtra(AddReceiptActivity.ARG_OCR_PHOTO, receipt.photo);
        startActivity(intent);
    }

    private void delete() {
        new AlertDialog.Builder(this)
                .setTitle("Вы действительно хотите удалить этот чек?")
                .setPositiveButton("Да", (dialog, which) -> {
                    cupboard().withContext(this)
                            .delete(UriHelper.with(ReceiptContentProvider.AUTHORITY).getUri(Receipt.class), receipt);
                    cupboard().withContext(this)
                            .delete(UriHelper.with(ReceiptContentProvider.AUTHORITY).getUri(ReceiptItem.class),
                                    "receiptId = ?", receipt._id.toString());
                    finish();
                }).show();
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ReceiptViewFragment.newInstance(receipt._id);
                case 1:
                    return ReceiptPhotoFragment.newInstance(receipt.photo);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Продукты";
                case 1:
                    return "Фото";
            }
            return null;
        }
    }
}
