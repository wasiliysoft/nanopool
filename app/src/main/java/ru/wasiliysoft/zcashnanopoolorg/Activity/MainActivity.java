package ru.wasiliysoft.zcashnanopoolorg.Activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Map;
import java.util.TreeMap;

import ru.wasiliysoft.zcashnanopoolorg.App;
import ru.wasiliysoft.zcashnanopoolorg.BuildConfig;
import ru.wasiliysoft.zcashnanopoolorg.Frafment.GeneralFragment;
import ru.wasiliysoft.zcashnanopoolorg.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_CODE_ADD_MINER = 1;
    private FragmentManager fm;
    final int MENU_ADD_MINER = 1;
    final int MENU_SELECT_MINER = 2;
    final int MENU_SHARE = 3;
    final int MENU_DONATE = 4;
    final int MENU_CONTACT = 5;
    public static final String BUNDLE_MINER_ADDRESS = "BUNDLE_MINER_ADDRESS";
    public static final String BUNDLE_MINER_NAME = "BUNDLE_MINER_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        settingNavigationView(navigationView.getMenu());

        fm = getFragmentManager();
        if (BuildConfig.DEBUG) {
//            App.getMiners().add("TestMiner", "t1b9Um3KH6xa1bNV8UmtkwSEPXHZZnSoSPN");
            App.getMiners().add("Test " + BuildConfig.COIN_NAME + " miner", BuildConfig.TEST_MINER);
        }
        // проверка списка майнеров
        if (App.getMiners().read().size() == 0) {
            // майнеров нет
            showDialogAddMiner();
        } else {
            // показываем данные первого майнера в списке
            Map.Entry<String, String> miner = App.getMiners().read().firstEntry();
            Bundle arg = new Bundle();
            arg.putString(BUNDLE_MINER_NAME, miner.getKey());
            arg.putString(BUNDLE_MINER_ADDRESS, miner.getValue());
            ActivateGeneralFragment(arg);
        }
    }

    /**
     * Показывает диалог добавления майнера
     */
    void showDialogAddMiner() {
        startActivityForResult(new Intent(this, AddMinerActivity.class), REQUEST_CODE_ADD_MINER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResult", resultCode + "");
        if (requestCode == REQUEST_CODE_ADD_MINER && resultCode == RESULT_OK) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    /**
     * Наполнение бокового меню
     */
    void settingNavigationView(Menu menu) {
        for (final TreeMap.Entry<String, String> entry : App.getMiners().read().entrySet()) {
            menu.add(0, MENU_SELECT_MINER, 1, entry.getKey()).setIcon(R.drawable.ic_poll_black_24dp)
                    .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            Bundle arg = new Bundle();
                            arg.putString(BUNDLE_MINER_NAME, entry.getKey());
                            arg.putString(BUNDLE_MINER_ADDRESS, entry.getValue());
                            ActivateGeneralFragment(arg);
                            return false;
                        }
                    });
        }

        menu.add(0, MENU_ADD_MINER, 3, getString(R.string.add_miner)).setIcon(R.drawable.ic_add_black_24dp);
        menu.add(1, MENU_CONTACT, 5, R.string.feedback).setIcon(R.drawable.ic_email_black_24dp);
        menu.add(1, MENU_SHARE, 6, R.string.share_app).setIcon(R.drawable.ic_share_black_24dp);
    }

    /**
     * Добавлет фрагемент в контейнер
     */
    void ActivateGeneralFragment(Bundle arg) {
        setTitle(arg.getString(BUNDLE_MINER_NAME));
        Fragment f = new GeneralFragment();
        f.setArguments(arg);
        fm.beginTransaction()
                .replace(R.id.fragmentContainer, f)
                .commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete_miner) {
            App.getMiners().delete(getTitle().toString());
            finish();
            startActivity(new Intent(this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == MENU_ADD_MINER) {
            showDialogAddMiner();
        } else if (id == MENU_SHARE) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Try the app https://play.google.com/store/apps/details?id=" + getPackageName());
            sendIntent.setType("text/plain");
            if (sendIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(sendIntent);
            }
        } else if (id == MENU_CONTACT) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setType("text/plain");
            intent.setData(Uri.parse(getString(R.string.contact_email))); // only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL, "");
            intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
