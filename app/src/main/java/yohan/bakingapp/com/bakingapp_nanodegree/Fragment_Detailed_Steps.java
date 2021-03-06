package yohan.bakingapp.com.bakingapp_nanodegree;

import yohan.bakingapp.com.bakingapp_nanodegree.R;
import yohan.bakingapp.com.bakingapp_nanodegree.Steps;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

/**
 * Created by Yohan on 21/05/2018.
 */

public class Fragment_Detailed_Steps extends Fragment{
    public static final String PLAYBACK_POSITION = "playback_position";
    public static final String PLAY_WHEN_READY = "play_when_ready";

    private Steps step;
    private SimpleExoPlayer player;
    private long playbackPosition;
    private boolean playWhenReady;
    private SimpleExoPlayerView simpleExoPlayerView;
    private String videoURL;

    public Fragment_Detailed_Steps() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.detailed_step_fragment, container, false);

        TextView description = rootView.findViewById(R.id.detailed_textview);
        ImageView thumbnailImage = rootView.findViewById(R.id.image_thumbnail);
        TextView stepidTextview = rootView.findViewById(R.id.step_id_textview);

        simpleExoPlayerView = rootView.findViewById(R.id.video_view);

        if (this.getArguments() != null) {
            step = this.getArguments().getParcelable("steps");
            description.setText(step.getDescription());
        }

        if (savedInstanceState != null) {
            playbackPosition = savedInstanceState.getLong(PLAYBACK_POSITION);
            playWhenReady = savedInstanceState.getBoolean(PLAY_WHEN_READY);
        }

        stepidTextview.setText(String.valueOf("Step " + step.getSteps_id()));

        videoURL = step.getVideoURL();
        String thumbnailURL = step.getThumbnailURL();

        if (!TextUtils.isEmpty(thumbnailURL)) {
            thumbnailImage.setVisibility(View.GONE);

        } else {
            thumbnailImage.setVisibility(View.VISIBLE);

            if(!thumbnailURL.equals("")) {
                Picasso.get()
                        .load(thumbnailURL)
                        .error(R.drawable.noimage)
                        .into(thumbnailImage);
            } else {
                Picasso.get()
                        .load(R.drawable.noimage)
                        .into(thumbnailImage);
            }
        }

        if (!TextUtils.isEmpty(videoURL)) {
            initializePlayer(Uri.parse(videoURL));
            thumbnailImage.setVisibility(View.GONE);
        } else {
            simpleExoPlayerView.setVisibility(View.GONE);
        }
        return rootView;
    }

    private void initializePlayer(Uri uri) {
        player = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(getContext()),
                new DefaultTrackSelector(), new DefaultLoadControl());

        MediaSource mediaSource = buildMediaSource(uri);

        simpleExoPlayerView.setPlayer(player);
        player.prepare(mediaSource, true, false);
        player.setPlayWhenReady(playWhenReady);
        player.seekTo( playbackPosition);

    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory(getString(R.string.app_name))).
                createMediaSource(uri);
    }


    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer(Uri.parse(videoURL));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer(Uri.parse(videoURL));
        }
    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putLong(PLAYBACK_POSITION, playbackPosition);
        outState.putBoolean(PLAY_WHEN_READY, playWhenReady);
    }

    private void saveState() {
        if (player != null){
            playbackPosition = player.getContentPosition();
            playWhenReady = player.getPlayWhenReady();

        }
    }
}