package com.tms.onfilm.utilities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.tms.onfilm.R;
import com.tms.onfilm.models.Account;

import java.util.ArrayList;

public class PreferencesManager {
    private SharedPreferences sharedPreferences;
    public static final String KEY_PREFERENCE_NAME = "on_film_preference";
    private final String KEY_PUT_GET_ACCOUNT = "key_account";

    private Context context;

    private DatabaseReference rootAccount = FirebaseDatabase.getInstance().getReference("Accounts");

    public PreferencesManager(Context context) {
        this.context = context;
    }

    public void putObjectValue(Account account) {
        Login login = new Login(account.getEmailAccount(), account.getPasswordAccount());

        sharedPreferences = context.getSharedPreferences(KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(login);
        editor.putString(KEY_PUT_GET_ACCOUNT, json);

        editor.apply();
    }

    public void getObjectValue() {
        sharedPreferences = context.getSharedPreferences(KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sharedPreferences.getString(KEY_PUT_GET_ACCOUNT, "");
        if(!json.equals("")) {
            Login login = gson.fromJson(json, Login.class);

            rootAccount.orderByChild("emailAccount").equalTo(login.email)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ArrayList<Account> accountArrayList = new ArrayList<>();

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                accountArrayList.add(dataSnapshot.getValue(Account.class));
                            }

                            if(accountArrayList.size() > 0) {
                                if (accountArrayList.get(0).getPasswordAccount().equals(login.password)) {
                                    Intent intent = new Intent(context.getString(R.string.action_get_account_login));
                                    intent.putExtra("object_account", accountArrayList.get(0));
                                    LocalBroadcastManager.getInstance(context.getApplicationContext()).sendBroadcast(intent);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }

    public void clearPreference() {
        sharedPreferences = context.getSharedPreferences(KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    private class Login {
        private String email, password;

        public Login(String email, String password) {
            this.email = email;
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
