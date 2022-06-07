package com.tms.onfilm.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tms.onfilm.R;
import com.tms.onfilm.broadcasts.BroadCastInternet;
import com.tms.onfilm.models.Account;
import com.tms.onfilm.models.Film;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {
    private TextView txtMoveLogin, txtRegister;
    private ImageView imgBack;
    private EditText edtEmail, edtPassword, edtConfirm;

    private DatabaseReference rootAccount = FirebaseDatabase.getInstance().getReference("Accounts");

    private BroadCastInternet broadCastInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        broadCastInternet = new BroadCastInternet();

        txtMoveLogin = findViewById(R.id.text_move_login);
        imgBack = findViewById(R.id.image_back);
        edtEmail = findViewById(R.id.edit_email);
        edtPassword = findViewById(R.id.edit_password);
        edtConfirm = findViewById(R.id.edit_password_confirm);
        txtRegister = findViewById(R.id.text_register);

        txtRegister.setOnClickListener(view -> {
            if(checkEmptyInvalid()) {
                checkExistEmailAccount();
            }
        });

        txtMoveLogin.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);

            finish();
        });

        imgBack.setOnClickListener(view -> {
            finish();
        });
    }

    // Kiểm tra email tài khoản đã đăng kí chưa
    private void checkExistEmailAccount() {
        rootAccount.orderByChild("emailAccount").equalTo(edtEmail.getText().toString())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<Account> accountArrayList = new ArrayList<>();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            accountArrayList.add(dataSnapshot.getValue(Account.class));
                        }

                        if(accountArrayList.size() > 0) {
                            Toast.makeText(getApplicationContext(), getString(R.string.notification_email_existed), Toast.LENGTH_SHORT).show();
                        } else {
                            uploadAccountToFirebase();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), getString(R.string.notification_failured_check_email), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Lưu tài khoản đăng ký
    private void uploadAccountToFirebase() {
        Account account = new Account(rootAccount.push().getKey(), edtEmail.getText().toString().trim(), edtPassword.getText().toString().trim());
        rootAccount.child(account.getIdAccount()).setValue(account)
                .addOnSuccessListener(unused -> {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.putExtra("object_account", account);
                    startActivity(intent);

                    Toast.makeText(getApplicationContext(), getString(R.string.notification_successfull_register), Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), getString(R.string.notification_failured_check_email), Toast.LENGTH_SHORT).show());
    }

    // Kiểm tra nhập đúng fomat email, không để trống thông tin, xác nhận giống với mật khẩu
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

        if(edtConfirm.getText().toString().isEmpty()
                || edtConfirm.getText().toString().equals("")
                || !edtConfirm.getText().toString().equals(edtPassword.getText().toString())) {
            Toast.makeText(this, getString(R.string.notification_confirm_invalid), Toast.LENGTH_SHORT).show();
            edtConfirm.setError(getString(R.string.error_input_confirm));
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