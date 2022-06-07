package com.tms.onfilm.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tms.onfilm.R;
import com.tms.onfilm.adapters.FilmSearchAdapter;
import com.tms.onfilm.broadcasts.BroadCastInternet;
import com.tms.onfilm.listeners.FilmSearchListener;
import com.tms.onfilm.models.Film;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SearchActivity extends AppCompatActivity implements FilmSearchListener {
    private EditText edtSearch;
    private TextView txtResult;
    private ImageView imgBack;
    private RecyclerView rvFilm;

    private DatabaseReference rootFilm = FirebaseDatabase.getInstance().getReference("Films");

    private String typeSearch;
    private ArrayList<Film> filmArrayList, filmSearchArrayList;

    private FilmSearchAdapter filmSearchAdapter;

    private BroadCastInternet broadCastInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        broadCastInternet = new BroadCastInternet();

        typeSearch = getIntent().getStringExtra("type_search");

        edtSearch = findViewById(R.id.edit_search);
        imgBack = findViewById(R.id.image_back);
        txtResult = findViewById(R.id.text_title_result);
        rvFilm = findViewById(R.id.recyclerview_film);

        filmArrayList = new ArrayList<>();
        filmSearchArrayList = new ArrayList<>();

        filmSearchAdapter = new FilmSearchAdapter(this, filmSearchArrayList);
        rvFilm.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        rvFilm.setAdapter(filmSearchAdapter);

        loadFilm();

        imgBack.setOnClickListener(view -> finish());

        edtSearch.setOnEditorActionListener((textView, i, keyEvent) -> {
            txtResult.setText(R.string.title_result);
            filmSearchArrayList.clear();

            if(!edtSearch.getText().toString().isEmpty() && !edtSearch.getText().toString().equals("")) {
                for (Film film : filmArrayList) {
                    if(film.getNameFilm().contains(edtSearch.getText().toString().trim().toUpperCase())) {
                        filmSearchArrayList.add(film);
                    }
                }
            } else {
                filmSearchArrayList.addAll(filmArrayList);
            }

            filmSearchAdapter.notifyDataSetChanged();
            return false;
        });
    }

    private void loadFilm() {
        rootFilm.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                filmArrayList.clear();

                // Lấy tất cả film
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Film film = dataSnapshot.getValue(Film.class);
                    filmArrayList.add(film);
                }

                loadResult();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadResult() {
        filmSearchArrayList.addAll(filmArrayList);

        if(typeSearch != null) {
            switch (typeSearch) {
                case "new_film":
                    txtResult.setText(R.string.title_result_new_film);
                    sortNewFilm();
                    break;

                case "hot_film":
                    txtResult.setText(R.string.title_result_hot_film);
                    sortHotFilm();
                    break;
            }
        } else {
            txtResult.setText(R.string.title_result);
        }

        filmSearchAdapter.notifyDataSetChanged();
    }

    private void sortHotFilm() {
        Collections.sort(filmSearchArrayList, new CustomComparatorFilmByFavorite());
    }

    private class CustomComparatorFilmByFavorite implements Comparator<Film> {
        @Override
        public int compare(Film film, Film t1) {
            return film.getCountFavorite()<= t1.getCountFavorite() ? 1 : -1;
        }
    }

    private void sortNewFilm() {
        Collections.sort(filmSearchArrayList, new CustomComparatorFilmByRelease());
    }

    private class CustomComparatorFilmByRelease implements Comparator<Film> {
        @Override
        public int compare(Film film, Film t1) {
            return film.getReleaseFilm() <= t1.getReleaseFilm() ? 1 : -1;
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
        registerReceiver(broadCastInternet, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadCastInternet);
        filmSearchAdapter.releaseContext();
    }


}