package ru.myocr.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.List;

import ru.myocr.R;
import ru.myocr.api.ApiHelper;
import ru.myocr.databinding.ActivityMainBinding;
import ru.myocr.fragment.TicketFragment;
import ru.myocr.model.City;
import ru.myocr.preference.Preference;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.fabCam).setOnClickListener(v -> onClickAddCam());
        findViewById(R.id.fabGallery).setOnClickListener(v -> onClickAddGallery());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ApiHelper.makeApiRequest(null, ApiHelper::getAllCities,
                throwable -> {
                },
                this::onLoadCities, null);

        openFragment(TicketFragment.newInstance());
    }

    private void onLoadCities(List<City> cities) {
        Preference.setString(Preference.CITY, cities.get(0).id);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.my_tickets) {
            openFragment(TicketFragment.newInstance());
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_share) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    private void onClickAddGallery() {
        selectImage();
    }

    private void onClickAddCam() {
        runCamScanner();
    }

    // old Activity
    private void handleIncomingImage(Intent intent) {
        if (intent != null) {
            final String type = intent.getType();
            final Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            if (type != null && type.startsWith("image/") && imageUri != null) {
                startCropImageActivity(imageUri);
            }
        }
    }

    private void startCropImageActivity(Uri imageUri) {
        final CropImage.ActivityBuilder activityBuilder = CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMaxZoom(16)
                .setMinCropWindowSize(0, 0) // remove minimum restriction
                .setBorderCornerThickness(0) // remove border corners
                .setMultiTouchEnabled(true);
        final Intent intent = activityBuilder.getIntent(this);
        intent.setClass(this, CropActivity.class);
        startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIncomingImage(intent);
    }

    public void runCamScanner() {
        Intent intent = new Intent("com.intsig.camscanner.NEW_DOC");
        startActivity(intent);
    }

    public void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        final Intent chooser = Intent.createChooser(intent, "Select Picture");
        startActivityForResult(chooser, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_IMAGE_REQUEST:
                    if (data != null && data.getData() != null) {
                        final Uri imageUri = data.getData();
                        Toast.makeText(MainActivity.this, imageUri.toString(), Toast.LENGTH_LONG).show();
                        startCropImageActivity(imageUri);
                    }
                    break;
            }
        }
    }

}
