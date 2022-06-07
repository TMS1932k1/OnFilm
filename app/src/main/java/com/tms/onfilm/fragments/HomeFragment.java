package com.tms.onfilm.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tms.onfilm.R;
import com.tms.onfilm.activities.SearchActivity;
import com.tms.onfilm.activities.WatchFilmActivity;
import com.tms.onfilm.adapters.FilmInAllAdapter;
import com.tms.onfilm.adapters.GenreAdapter;
import com.tms.onfilm.adapters.StandardFilmAdapter;
import com.tms.onfilm.listeners.OpenNavigationListener;
import com.tms.onfilm.listeners.SelectFilmListener;
import com.tms.onfilm.listeners.SelectGenreListener;
import com.tms.onfilm.models.Film;
import com.tms.onfilm.models.Genre;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class HomeFragment extends Fragment implements SelectFilmListener, SelectGenreListener {
    private ImageView imgOpenNavigation, imgSearch, imgMoreNewFilm, imgMoreHotFilm;
    private ViewPager2 vpFilmInAll;
    private RecyclerView rvNewFilm, rvHotFilm, rvGenre, rvGenreFilm;

    private OpenNavigationListener openNavigationListener;
    private FilmInAllAdapter filmInAllAdapter;
    private StandardFilmAdapter newFilmAdapter, hotFilmAdapter, genreFilmAdapter;
    private GenreAdapter genreAdapter;

    private DatabaseReference rootFilm = FirebaseDatabase.getInstance().getReference("Films");
    private DatabaseReference rootGenre = FirebaseDatabase.getInstance().getReference("Genres");

    private ArrayList<Film> filmArrayList, newFilmArrayList, hotFilmArrayList, genreFilmAdrrayList;
    private ArrayList<Genre> genreArrayList, genreSelectedArrayList;

    public HomeFragment(OpenNavigationListener openNavigationListener) {
        this.openNavigationListener = openNavigationListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Khởi tạo các ds film và genre
        filmArrayList = new ArrayList<>();
        newFilmArrayList = new ArrayList<>();
        hotFilmArrayList = new ArrayList<>();
        genreArrayList = new ArrayList<>();
        genreFilmAdrrayList = new ArrayList<>();
        genreSelectedArrayList = new ArrayList<>();

        // Ánh xạ
        imgOpenNavigation = view.findViewById(R.id.image_open_navigation);
        vpFilmInAll = view.findViewById(R.id.viewpaper2_film_in_all);
        rvNewFilm = view.findViewById(R.id.recyclerview_new_film);
        rvHotFilm = view.findViewById(R.id.recyclerview_hot_film);
        rvGenre = view.findViewById(R.id.recyclerview_genre);
        rvGenreFilm = view.findViewById(R.id.recyclerview_genre_film);
        imgSearch = view.findViewById(R.id.image_search);
        imgMoreNewFilm = view.findViewById(R.id.image_more_new_film);
        imgMoreHotFilm = view.findViewById(R.id.image_more_hot_film);

        // Set recyclerview và adapter
        genreFilmAdapter = new StandardFilmAdapter(getContext(), genreFilmAdrrayList, this);
        rvGenreFilm.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        rvGenreFilm.setAdapter(genreFilmAdapter);

        genreAdapter = new GenreAdapter(getContext(), genreArrayList, this);
        rvGenre.setLayoutManager(new StaggeredGridLayoutManager(1 , StaggeredGridLayoutManager.HORIZONTAL));
        rvGenre.setAdapter(genreAdapter);

        hotFilmAdapter = new StandardFilmAdapter(getContext(), hotFilmArrayList, this);
        rvHotFilm.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        rvHotFilm.setAdapter(hotFilmAdapter);

        newFilmAdapter = new StandardFilmAdapter(getContext(), newFilmArrayList, this);
        rvNewFilm.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        rvNewFilm.setAdapter(newFilmAdapter);

        filmInAllAdapter = new FilmInAllAdapter(getContext(), filmArrayList, this);
        vpFilmInAll.setAdapter(filmInAllAdapter);

        vpFilmInAll.setClipToPadding(false);
        vpFilmInAll.setClipChildren(false);
        vpFilmInAll.setOffscreenPageLimit(3);
        vpFilmInAll.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });

        vpFilmInAll.setPageTransformer(compositePageTransformer);

        // Load tất cả film
        loadFilm();

        // Load tất cả các thể loại
        loadGenre();

        imgOpenNavigation.setOnClickListener(view1 -> openNavigationListener.openNavigation());

        imgSearch.setOnClickListener(view12 -> {
            Intent intent = new Intent(getContext(), SearchActivity.class);
            startActivity(intent);
        });

        imgMoreNewFilm.setOnClickListener(view13 -> {
            Intent intent = new Intent(getContext(), SearchActivity.class);
            intent.putExtra("type_search", "new_film");
            startActivity(intent);
        });

        imgMoreHotFilm.setOnClickListener(view14 -> {
            Intent intent = new Intent(getContext(), SearchActivity.class);
            intent.putExtra("type_search", "hot_film");
            startActivity(intent);
        });

        return view;
    }

    // Hàm load ds film
    private void loadFilm() {
        rootFilm.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                filmArrayList.clear();
                newFilmArrayList.clear();
                hotFilmArrayList.clear();

                // Dùng biến tạm để sắp xếp new và hot
                ArrayList<Film> tempArrayList = new ArrayList<>();

                // Lấy tất cả film
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Film film = dataSnapshot.getValue(Film.class);
                    filmArrayList.add(film);
                    tempArrayList.add(film);
                }
                Collections.shuffle(filmArrayList);
                vpFilmInAll.setCurrentItem(0);
                filmInAllAdapter.notifyDataSetChanged();

                // Lấy film mới nhất
                Collections.sort(tempArrayList, new CustomComparatorFilmByRelease());
                for (Film film : tempArrayList) {
                    if(newFilmArrayList.size() < 5) {
                        newFilmArrayList.add(film);
                    } else {
                        break;
                    }
                }
                newFilmAdapter.notifyDataSetChanged();

                // Lấy film ưa thích
                Collections.sort(tempArrayList, new CustomComparatorFilmByFavorite());
                for (Film film : tempArrayList) {
                    if (hotFilmArrayList.size() < 5) {
                        hotFilmArrayList.add(film);
                    } else {
                        break;
                    }
                }
                hotFilmAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), getString(R.string.notìication_in_load_film), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class CustomComparatorFilmByFavorite implements Comparator<Film> {
        @Override
        public int compare(Film film, Film t1) {
            return film.getCountFavorite()<= t1.getCountFavorite() ? 1 : -1;
        }
    }

    private class CustomComparatorFilmByRelease implements Comparator<Film> {
        @Override
        public int compare(Film film, Film t1) {
            return film.getReleaseFilm() <= t1.getReleaseFilm() ? 1 : -1;
        }
    }

    // Hàm load thể loại
    private void loadGenre() {
        rootGenre.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                genreArrayList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Genre genre = dataSnapshot.getValue(Genre.class);
                    genreArrayList.add(genre);
                }
                genreAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void setOnClickFilm(Film film) {
        Intent intent = new Intent(getContext(), WatchFilmActivity.class);
        intent.putExtra(getString(R.string.intent_object_film), film);
        startActivity(intent);
    }

    @Override
    public void selectGenre(Genre genre) {
        genreSelectedArrayList.add(genre);
        loadGenreFilmByGenreArrayList();
    }

    @Override
    public boolean isCheckSelected(Genre genre) {
        for (Genre g : genreSelectedArrayList) {
            if(g.getIdGenre().equals(genre.getIdGenre())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void cancleSelectGenre(Genre genre) {
        genreSelectedArrayList.remove(genre);
        loadGenreFilmByGenreArrayList();
    }

    private void loadGenreFilmByGenreArrayList() {
        genreFilmAdrrayList.clear();
        for (Film film : filmArrayList) {
            if(checkFilmIsGenre(film.getGenreArrayList())) {
                genreFilmAdrrayList.add(film);
            }
        }
        genreFilmAdapter.notifyDataSetChanged();
    }

    private boolean checkFilmIsGenre(ArrayList<Genre> genreArrayList) {
        for (Genre genre : genreArrayList) {
            for (Genre genre1 : genreSelectedArrayList) {
                if (genre.getIdGenre().equals(genre1.getIdGenre())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        filmInAllAdapter.releaseContext();
        newFilmAdapter.releaseContext();
        hotFilmAdapter.releaseContext();
        genreFilmAdapter.releaseContext();
        genreAdapter.releaseContext();
    }
}