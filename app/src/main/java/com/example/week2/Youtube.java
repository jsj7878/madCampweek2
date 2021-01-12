package com.example.week2;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.week2.Page3Fragment.playLists;

public class Youtube extends YouTubeBaseActivity {

    static ImageView imageView = null;
    static TextView s_time = null;
    static TextView s_desc = null;
    static GridView gridView = null;
    static int size;

    YouTubePlayerFragment youtubeFragment;
    static YouTubePlayer ytp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.youtube);




        youtubeFragment = (YouTubePlayerFragment)
                getFragmentManager().findFragmentById(R.id.youtubeFragment);
        youtubeFragment.initialize("AIzaSyANYM5TIbJohkt1_z0P48A4WB8IEr2cVe0",
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                        YouTubePlayer youTubePlayer, boolean b) {
                        ytp = youTubePlayer;
                        ytp.loadVideo(playLists.get(0).getKeys());

                    }
                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                        YouTubeInitializationResult youTubeInitializationResult) {

                    }
                });

        s_time = (TextView) findViewById(R.id.s_time);
        s_desc = (TextView) findViewById(R.id.s_desc);
        s_time.setText(playLists.get(0).getTime());
        s_desc.setText(playLists.get(0).getExplain());

        // this is size of your list with data

        // Calculated single Item Layout Width for each grid element .. for me it was ~100dp
        int width = 100 ;
        // than just calculate sizes for layout params and use it
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        int totalWidth = (int) (width * 40 * density);
        int singleItemWidth = (int) (width * density);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(totalWidth, LinearLayout.LayoutParams.MATCH_PARENT);

        gridView = (GridView) this.findViewById(R.id.cdimages);
        gridView.setLayoutParams(params);
        //gridView.setColumnWidth(singleItemWidth);
        gridView.setHorizontalSpacing(2);
        //gridView.setStretchMode(GridView.STRETCH_SPACING);



    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }



}
