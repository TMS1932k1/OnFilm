package com.tms.onfilm.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tms.onfilm.R;
import com.tms.onfilm.activities.MainActivity;
import com.tms.onfilm.adapters.FilmSearchAdapter;
import com.tms.onfilm.listeners.OpenNavigationListener;
import com.tms.onfilm.models.Account;
import com.tms.onfilm.models.Film;

import java.util.ArrayList;

public class FavoriteFragment extends Fragment {
    private ImageView imgOpenNavigation;
    private RecyclerView rvFavorite;

    private ArrayList<Film> filmFavoriteArrayList;

    private OpenNavigationListener openNavigationListener;
    private FilmSearchAdapter filmSearchAdapter;

    public FavoriteFragment(OpenNavigationListener openNavigationListener) {
        this.openNavigationListener = openNavigationListener;
    }

    private BroadcastReceiver broadcastReceiverLogout = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            filmFavoriteArrayList.clear();
            filmSearchAdapter.notifyDataSetChanged();
        }
    };

    private BroadcastReceiver broadcastReceiverLogin = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(MainActivity.account != null) {
                loadFilmFavorite();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        imgOpenNavigation = view.findViewById(R.id.image_open_navigation);
        rvFavorite = view.findViewById(R.id.recyclerview_favorite);

        filmFavoriteArrayList = new ArrayList<>();

        filmSearchAdapter = new FilmSearchAdapter(getContext(), filmFavoriteArrayList);
        rvFavorite.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        rvFavorite.setAdapter(filmSearchAdapter);

        if(MainActivity.account != null) {
            loadFilmFavorite();
        }

        imgOpenNavigation.setOnClickListener(view1 -> openNavigationListener.openNavigation());

        return view;
    }

    private void loadFilmFavorite() {
        DatabaseReference rootAccount = FirebaseDatabase.getInstance().getReference("Accounts");
        rootAccount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DatabaseReference rootFilm = FirebaseDatabase.getInstance().getReference("Films");
                rootFilm.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        filmFavoriteArrayList.clear();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Film film = dataSnapshot.getValue(Film.class);
                            if(checkIsFavorite(film.getIdFilm())) {
                                filmFavoriteArrayList.add(film);
                            }
                        }

                        filmSearchAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private boolean checkIsFavorite(String idFilm) {
        for(String s : MainActivity.account.getIdFilmFavoriteArrayList()) {
            if(s.equals(idFilm)) {
                return true;
            }
        }
        return false;
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