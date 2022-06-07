package com.tms.onfilm.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.tms.onfilm.R;
import com.tms.onfilm.adapters.DecribePicAdapter;
import com.tms.onfilm.adapters.EpisodeAdapter;
import com.tms.onfilm.adapters.GenreInWatchFilmAdapter;
import com.tms.onfilm.broadcasts.BroadCastInternet;
import com.tms.onfilm.listeners.FullScreenVideoListener;
import com.tms.onfilm.listeners.SelectEpisodeListener;
import com.tms.onfilm.models.Account;
import com.tms.onfilm.models.Episode;
import com.tms.onfilm.models.Film;
import com.tms.onfilm.utilities.FullScreenMediaController;
import com.tms.onfilm.models.Picture;
import com.tms.onfilm.utilities.DownloadImageTask;

import java.util.ArrayList;

public class WatchFilmActivity extends AppCompatActivity implements SelectEpisodeListener, FullScreenVideoListener {
    private Film film;
    private ArrayList<Picture> describePicArrayList;
    private Picture pictureStandard;
    private Episode episodeWatch;
    private FrameLayout layoutSupportVideoView;

    private TextView txtNameFilm, txtDescribeFilm, txtDirectorFilm, txtActorsFilm,
            txtDurationFilm, txtReleaseFilm, txtLimitFilm, txtReadMoreHide;
    private RecyclerView rvGenre, rvEpisode;
    private ConstraintLayout layoutInfoFilm;
    private SliderView sliderDescribePic;
    private ImageView imgBack, imgLogo, imgPicStandard, imgIsFavorite;
    private VideoView vvEpisode;

    private boolean isReadMore = false;

    private Handler picDescribeSlideHandler = new Handler();

    private GenreInWatchFilmAdapter genreInWatchFilmAdapter;
    private DecribePicAdapter decribePicAdapter;
    private EpisodeAdapter episodeAdapter;

    private BroadCastInternet broadCastInternet;

    private BroadcastReceiver broadcastReceiverLogin = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setFavoriteIcon();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_film);

        broadCastInternet = new BroadCastInternet();

        Intent intent = getIntent();
        film = (Film) intent.getSerializableExtra(getString(R.string.intent_object_film));

        if(film == null) {
            finish();
        }

        imgBack = findViewById(R.id.image_back);
        imgLogo = findViewById(R.id.image_logo);
        imgPicStandard = findViewById(R.id.image_pic_standard);
        txtNameFilm = findViewById(R.id.text_name_film);
        txtDescribeFilm = findViewById(R.id.text_describe_film);
        txtDirectorFilm = findViewById(R.id.text_director_film);
        txtActorsFilm = findViewById(R.id.text_actors_film);
        txtDurationFilm = findViewById(R.id.text_duration_film);
        txtReleaseFilm = findViewById(R.id.text_release_film);
        txtReadMoreHide = findViewById(R.id.text_read_more_hide);
        txtLimitFilm = findViewById(R.id.text_limit_film);
        rvGenre = findViewById(R.id.recyclerview_genre);
        layoutInfoFilm = findViewById(R.id.layout_info_film);
        sliderDescribePic = findViewById(R.id.layout_slide_describe_pic);
        rvEpisode = findViewById(R.id.recyclerview_episode);
        vvEpisode = findViewById(R.id.videoview_episode);
        layoutSupportVideoView = findViewById(R.id.layout_support_videoview);
        imgIsFavorite = findViewById(R.id.image_is_favorite);

        txtDescribeFilm.setText(film.getDescribeFilm());
        txtDirectorFilm.setText(film.getDirectorFilm());
        txtActorsFilm.setText(film.getActorsFilm());
        txtDurationFilm.setText(film.getDurationFilm() + " "+ getString(R.string.duration_on_episode));
        txtReleaseFilm.setText(film.getReleaseFilm() + "");
        txtLimitFilm.setText(film.getLimitFilm() + "");

        // Check phim có trong danh sách yêu thích của tài khoản
        setFavoriteIcon();

        // Load ds hình miêu tả và hình chuẩn
        setStandardDescribePicArrayList();

        // Set Slider Pic
        setSliderView();

        // Set hình chuẩn của film
        if(pictureStandard != null) {
            new DownloadImageTask(imgPicStandard).execute(pictureStandard.getLinkPic());
        }

        txtNameFilm.setText(film.getNameFilm());

        genreInWatchFilmAdapter = new GenreInWatchFilmAdapter(film.getGenreArrayList());
        int spanCount = getSpanCount(2, film.getGenreArrayList().size());
        rvGenre.setLayoutManager(new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.HORIZONTAL));
        rvGenre.setAdapter(genreInWatchFilmAdapter);

        episodeAdapter = new EpisodeAdapter(film.getEpisodeArrayList(), this);
        rvEpisode.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        rvEpisode.setAdapter(episodeAdapter);

        // Bắt sự kiện click
        imgBack.setOnClickListener(view -> finish());

        txtReadMoreHide.setOnClickListener(view -> {
            if(!isReadMore) {
                isReadMore = true;
                layoutInfoFilm.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                txtReadMoreHide.setText(getString(R.string.hide));
            } else {
                isReadMore = false;
                layoutInfoFilm.getLayoutParams().height = getResources().getDimensionPixelSize(R.dimen.dimen_layout_info_film);
                txtReadMoreHide.setText(getString(R.string.read_more));
            }
        });

        imgIsFavorite.setOnClickListener(view -> {
            if(MainActivity.account != null) {
                if(!checkFilmIsFavorited()) {
                    imgIsFavorite.setImageResource(R.drawable.ic_favorite);
                    addCountFavorite();
                } else {
                    imgIsFavorite.setImageResource(R.drawable.ic_unfavorite);
                    deleteCountFavorite();
                }

            } else {
                Intent intentLogin = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intentLogin);
            }
        });
    }

    // Hiển thị icon yêu thích
    private void setFavoriteIcon() {
        if(MainActivity.account != null) {
            if(!checkFilmIsFavorited()) {
                imgIsFavorite.setImageResource(R.drawable.ic_unfavorite);
            } else {
                imgIsFavorite.setImageResource(R.drawable.ic_favorite);
            }
        }
    }

    private void deleteCountFavorite() {
        DatabaseReference rootFilm = FirebaseDatabase.getInstance().getReference("Films");

        film.setCountFavorite(film.getCountFavorite() - 1);
        rootFilm.child(film.getIdFilm()).setValue(film)
                .addOnSuccessListener(unused -> {
                    MainActivity.account.getIdFilmFavoriteArrayList().remove(film.getIdFilm());

                    DatabaseReference rootAccount = FirebaseDatabase.getInstance().getReference("Accounts");
                    rootAccount.child(MainActivity.account.getIdAccount()).setValue(MainActivity.account);
                });
    }

    private void addCountFavorite() {
        DatabaseReference rootFilm = FirebaseDatabase.getInstance().getReference("Films");

        film.setCountFavorite(film.getCountFavorite() + 1);
        rootFilm.child(film.getIdFilm()).setValue(film)
                .addOnSuccessListener(unused -> {
                    MainActivity.account.getIdFilmFavoriteArrayList().add(film.getIdFilm());

                    DatabaseReference rootAccount1 = FirebaseDatabase.getInstance().getReference("Accounts");
                    rootAccount1.child(MainActivity.account.getIdAccount()).setValue(MainActivity.account);
                });
    }

    private boolean checkFilmIsFavorited() {
        if(MainActivity.account.getIdFilmFavoriteArrayList() != null) {
            for(String idFilm : MainActivity.account.getIdFilmFavoriteArrayList()) {
                if(idFilm.equals(film.getIdFilm())) {
                    return true;
                }
            }
        }
        return false;
    }

    private int getSpanCount(int countOnLine, int sum) {
        if(sum % countOnLine != 0) {
            sum = sum / 2 + 1;
        } else {
            sum = sum / 2;
        }
        return sum;
    }

    private void setSliderView() {
        if(describePicArrayList == null || describePicArrayList.size() == 0) {
            imgLogo.setVisibility(View.VISIBLE);
            sliderDescribePic.setVisibility(View.INVISIBLE);
        } else {
            imgLogo.setVisibility(View.INVISIBLE);
            sliderDescribePic.setVisibility(View.VISIBLE);

            decribePicAdapter = new DecribePicAdapter(describePicArrayList);
            sliderDescribePic.setSliderAdapter(decribePicAdapter);
            sliderDescribePic.setIndicatorAnimation(IndicatorAnimationType.WORM);
            sliderDescribePic.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
            sliderDescribePic.startAutoCycle();
        }
    }

    private void setStandardDescribePicArrayList() {
        if(film.getPictureArrayList() != null) {
            describePicArrayList = new ArrayList<>();
            for (Picture picture : film.getPictureArrayList()) {
                if(picture.getTypePic().equals(getString(R.string.Standard))) {
                    pictureStandard = picture;
                } else {
                    describePicArrayList.add(picture);
                }
            }
        } else {
            describePicArrayList = null;
            pictureStandard = null;
        }
    }

    @Override
    public void selectEpisode(Episode episode) {
        episodeWatch = episode;
        episodeAdapter.notifyDataSetChanged();

        layoutSupportVideoView.setVisibility(View.VISIBLE);

        Uri videoUri = Uri.parse(episode.getLinkEpisode());

        vvEpisode.setVideoURI(videoUri);

        MediaController mediaController = new FullScreenMediaController(this, this);
        mediaController.setAnchorView(vvEpisode);

        vvEpisode.setMediaController(mediaController);
        vvEpisode.start();
    }

    @Override
    public boolean isSelectingEpisode(Episode episode) {
        if(episodeWatch == null || !episodeWatch.equals(episode)) {
            return false;
        }
        return true;
    }

    @Override
    public void moveFullScreenVideoActivity() {
        Intent intent = new Intent(this, FullScreenVideoActivity.class);
        intent.putExtra("fullScreenInd", "y");
        intent.putExtra("current_position_episode", vvEpisode.getCurrentPosition());
        intent.putExtra("object_link_episode", episodeWatch.getLinkEpisode());
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
        unregisterReceiver(broadCastInternet);
        episodeAdapter.releaseContext();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiverLogin);
    }
}