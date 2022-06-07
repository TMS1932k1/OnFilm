package com.tms.onfilm.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tms.onfilm.R;
import com.tms.onfilm.broadcasts.BroadCastInternet;
import com.tms.onfilm.utilities.PreferencesManager;
import com.tms.onfilm.models.Account;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    private TextView txtMoveRegister, txtLogin;
    private ImageView imgBack;
    private EditText edtEmail, edtPassword;
    private CheckBox cbRememberLogin;

    private DatabaseReference rootAccount = FirebaseDatabase.getInstance().getReference("Accounts");

    private BroadCastInternet broadCastInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        broadCastInternet = new BroadCastInternet();

        txtMoveRegister = findViewById(R.id.text_move_register);
        imgBack = findViewById(R.id.image_back);
        edtEmail = findViewById(R.id.edit_email);
        edtPassword = findViewById(R.id.edit_password);
        txtLogin = findViewById(R.id.text_login);
        cbRememberLogin = findViewById(R.id.checkbox_remember_login);

        Account account = (Account) getIntent().getSerializableExtra("object_account");
        if(account != null) {
            edtEmail.setText(account.getEmailAccount());
            edtPassword.setText(account.getPasswordAccount());
        }

        txtLogin.setOnClickListener(view -> {
            if(checkEmptyInvalid()) {
                loginAccount();
            }
        });

        txtMoveRegister.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);

            finish();
        });

        imgBack.setOnClickListener(view -> {
            finish();
        });
    }

    // Hàm đăng nhập tài khoản
    private void loginAccount() {
        rootAccount.orderByChild("emailAccount").equalTo(edtEmail.getText().toString().trim())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<Account> accountArrayList = new ArrayList<>();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            accountArrayList.add(dataSnapshot.getValue(Account.class));
                        }

                        if(accountArrayList.size() > 0) {
                            if(accountArrayList.get(0).getPasswordAccount().equals(edtPassword.getText().toString().trim())) {
                                Intent intent = new Intent(getString(R.string.action_get_account_login));
                                intent.putExtra("object_account", accountArrayList.get(0));
                                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                                if(cbRememberLogin.isChecked()) {
                                    PreferencesManager preferencesManager = new PreferencesManager(getApplicationContext());
                                    preferencesManager.putObjectValue(accountArrayList.get(0));
                                }

                                Toast.makeText(getApplicationContext(), getString(R.string.notification_successful_login), Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.notification_exactly), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.notification_account_not_existed), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), getString(R.string.notification_failured_login), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Kiểm tra chuỗi rỗng hoặc ko hợp lệ
    private boolean checkEmptyInvalid() {
        if(edtEmail.getText().toString().isEmpty()
                || edtEmail.getText().toString().equals("")
                || !android.util.Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText().toString()).matches()) {
            Toast.makeText(this, getString(R.string.notification_email_invalid), Toast.LENGTH_SHORT).show();
            edtEmail.setError(getString(R.string.error_input_email));
            return false;
        }

        if(edtPassword.getText().toString().isEmpty() || edtPassword.getText().toString().equals("")) {
            Toast.makeText(this, getString(R.string.notification_password_invalid), Toast.LENGTH_SHORT).show();
            edtPassword.setError(getString(R.string.error_input_password));
            return false;
        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(broadCastInternet, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadCastInternet);
    }
}