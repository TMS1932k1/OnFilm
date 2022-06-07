package com.tms.onfilm.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.tms.onfilm.R;
import com.tms.onfilm.broadcasts.BroadCastInternet;
import com.tms.onfilm.fragments.AccountFragment;
import com.tms.onfilm.fragments.FavoriteFragment;
import com.tms.onfilm.fragments.HomeFragment;
import com.tms.onfilm.listeners.FilmSearchListener;
import com.tms.onfilm.listeners.OpenNavigationListener;
import com.tms.onfilm.models.Account;
import com.tms.onfilm.utilities.PreferencesManager;
import com.tms.onfilm.models.Film;

public class MainActivity extends AppCompatActivity implements OpenNavigationListener, FilmSearchListener {

    private final int HOME_FRAGMENT = 0;
    private final int FAVORITE_FRAGMENT = 1;
    private final int ACCOUNT_FRAGMENT = 2;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private View header;
    private TextView txtLogin, txtEmailAccount, txtFirstChar;

    private int currentFragment = 0;
    private PreferencesManager preferencesManager;

    public static Account account = null;

    private BroadCastInternet broadCastInternet;

    private BroadcastReceiver broadcastReceiverLogin = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            account = (Account) intent.getSerializableExtra("object_account");
            txtEmailAccount.setText(account.getEmailAccount());
            txtFirstChar.setText(String.valueOf(account.getEmailAccount().charAt(0)).toUpperCase());

            txtFirstChar.setVisibility(View.VISIBLE);
            txtLogin.setText(R.string.title_logout);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        broadCastInternet = new BroadCastInternet();

        drawerLayout = findViewById(R.id.drawerlayout_main_activity);
        navigationView = findViewById(R.id.navigationview_menu);
        header = navigationView.getHeaderView(0);

        txtLogin = header.findViewById(R.id.text_logout_login);
        txtEmailAccount = header.findViewById(R.id.text_email_account);
        txtFirstChar = header.findViewById(R.id.text_first_char_name);

        preferencesManager = new PreferencesManager(this);
        preferencesManager.getObjectValue();

        setDataNavigationView();

        txtLogin.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);

            if(account != null) {
                account = null;
                preferencesManager.clearPreference();

                txtFirstChar.setVisibility(View.INVISIBLE);
                txtEmailAccount.setText("");
                txtLogin.setText(R.string.title_login);

                Intent intentLogout = new Intent(getString(R.string.logout_account));
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intentLogout);
            }
        });
    }

    private void setDataNavigationView() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            switch (id) {
                case R.id.menu_home:
                    if (currentFragment != HOME_FRAGMENT) {
                        replaceFragment(new HomeFragment(this));
                        currentFragment = HOME_FRAGMENT;
                    }
                    break;

                case R.id.menu_favorite:
                    if (currentFragment != FAVORITE_FRAGMENT) {
                        if(account != null) {
                            replaceFragment(new FavoriteFragment(this));
                            currentFragment = FAVORITE_FRAGMENT;
                        } else {
                            Intent intentFavoriteToLogin = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intentFavoriteToLogin);
                        }
                    }
                    break;

                case R.id.menu_search:
                    Intent intent = new Intent(this, SearchActivity.class);
                    startActivity(intent);
                    break;

                case R.id.menu_account:
                    if (currentFragment != ACCOUNT_FRAGMENT) {
                        if(account != null) {
                            replaceFragment(new AccountFragment(this));
                            currentFragment = ACCOUNT_FRAGMENT;
                        } else {
                            Intent intentAccountToLogin = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intentAccountToLogin);
                        }
                    }
                    break;

                default:
                    replaceFragment(new HomeFragment(this));
                    currentFragment = HOME_FRAGMENT;
                    break;
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return false;
        });

        replaceFragment(new HomeFragment(this));
        currentFragment = HOME_FRAGMENT;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout_content, fragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void openNavigation() {
        if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.openDrawer(GravityCompat.START);
        } else {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onClickFilm(Film film) {
        Intent intent = new Intent(this, WatchFilmActivity.class);
        intent.putExtra(getString(R.string.intent_object_film), film);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiverLogin, new IntentFilter(getString(R.string.action_get_account_login)));
        registerReceiver(broadCastInternet, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiverLogin);
        unregisterReceiver(broadCastInternet);
    }
}