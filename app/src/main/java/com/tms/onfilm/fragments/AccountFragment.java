package com.tms.onfilm.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.tms.onfilm.R;
import com.tms.onfilm.activities.MainActivity;
import com.tms.onfilm.listeners.OpenNavigationListener;
import com.tms.onfilm.utilities.PreferencesManager;

public class AccountFragment extends Fragment {
    private ImageView imgOpenNavigation;
    private TextView txtFirstCharName, txtEmailAccount, txtUploadPasswords;
    private EditText edtPasswordCurrent, edtPasswordNew, edtPasswordConfirm;

    private OpenNavigationListener openNavigationListener;
    private SharedPreferences sharedPreferences;

    public static final String KEY_PREFERENCE_NAME = "on_film_preference";
    private final String KEY_PUT_GET_ACCOUNT = "key_account";

    private BroadcastReceiver broadcastReceiverLogout = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            txtFirstCharName.setVisibility(View.INVISIBLE);
            txtEmailAccount.setVisibility(View.INVISIBLE);
            txtUploadPasswords.setEnabled(false);
        }
    };

    private BroadcastReceiver broadcastReceiverLogin = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(MainActivity.account != null) {
                txtFirstCharName.setText(String.valueOf(MainActivity.account.getEmailAccount().charAt(0)).toUpperCase());
                txtEmailAccount.setText(MainActivity.account.getEmailAccount());

                txtFirstCharName.setVisibility(View.VISIBLE);
                txtEmailAccount.setVisibility(View.VISIBLE);
                txtUploadPasswords.setEnabled(true);
            }
        }
    };

    public AccountFragment(OpenNavigationListener openNavigationListener) {
        this.openNavigationListener = openNavigationListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        imgOpenNavigation = view.findViewById(R.id.image_open_navigation);
        txtFirstCharName = view.findViewById(R.id.text_first_char_name);
        txtEmailAccount = view.findViewById(R.id.text_email_account);
        txtUploadPasswords = view.findViewById(R.id.text_upload_password);
        edtPasswordCurrent = view.findViewById(R.id.edit_password_current);
        edtPasswordNew = view.findViewById(R.id.edit_password_new);
        edtPasswordConfirm = view.findViewById(R.id.edit_password_confirm);

        if(MainActivity.account != null) {
            txtFirstCharName.setText(String.valueOf(MainActivity.account.getEmailAccount().charAt(0)).toUpperCase());
            txtEmailAccount.setText(MainActivity.account.getEmailAccount());
        }

        imgOpenNavigation.setOnClickListener(view1 -> openNavigationListener.openNavigation());

        txtUploadPasswords.setOnClickListener(view12 -> {
            if(checkInputPassword()) {
                MainActivity.account.setPasswordAccount(edtPasswordConfirm.getText().toString().trim());

                DatabaseReference rootAccount = FirebaseDatabase.getInstance().getReference("Accounts");
                rootAccount.child(MainActivity.account.getIdAccount()).setValue(MainActivity.account)
                        .addOnSuccessListener(unused -> {
                            sharedPreferences = getContext().getSharedPreferences(KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);
                            String json = sharedPreferences.getString(KEY_PUT_GET_ACCOUNT, "");

                            // Kiểm tra có lưu login ko
                            if(!json.equals("")) {
                                PreferencesManager preferencesManager = new PreferencesManager(getContext());
                                preferencesManager.clearPreference();
                                preferencesManager.putObjectValue(MainActivity.account);
                            }

                            // Xóa hết thông tin đã điền
                            edtPasswordCurrent.setText("");
                            edtPasswordNew.setText("");
                            edtPasswordConfirm.setText("");

                            Toast.makeText(getContext(), getString(R.string.notification_update_password_successful), Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> Toast.makeText(getContext(), getString(R.string.notification_update_password_failured), Toast.LENGTH_SHORT).show());
            }
        });

        return view;
    }

    private boolean checkInputPassword() {
        if(edtPasswordCurrent.getText().toString().trim().isEmpty() || edtPasswordCurrent.getText().toString().trim().equals("")) {
            edtPasswordCurrent.setError(getString(R.string.error_input_password_current));
            return false;
        }

        if(edtPasswordNew.getText().toString().trim().isEmpty() || edtPasswordNew.getText().toString().trim().equals("")) {
            edtPasswordNew.setError(getString(R.string.error_input_password_new));
            return false;
        }

        if(edtPasswordConfirm.getText().toString().trim().isEmpty() || edtPasswordConfirm.getText().toString().trim().equals("")) {
            edtPasswordConfirm.setError(getString(R.string.error_input_password_confirm));
            return false;
        }

        if(!edtPasswordCurrent.getText().toString().trim().equals(MainActivity.account.getPasswordAccount())) {
            edtPasswordCurrent.setError(getString(R.string.error_password_current_not_match));
            return false;
        }

        if(edtPasswordNew.getText().toString().trim().equals(MainActivity.account.getPasswordAccount())) {
            edtPasswordNew.setError(getString(R.string.error_input_password_new_not_match_current));
            return false;
        }

        if(!edtPasswordNew.getText().toString().trim().equals(edtPasswordConfirm.getText().toString().trim())) {
            edtPasswordConfirm.setError(getString(R.string.error_input_confirm_not_match));
            return false;
        }

        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiverLogin, new IntentFilter(getString(R.string.action_get_account_login)));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiverLogout, new IntentFilter(getString(R.string.logout_account)));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiverLogin);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiverLogout);
    }
}