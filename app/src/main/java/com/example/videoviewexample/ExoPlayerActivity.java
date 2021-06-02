package com.example.videoviewexample;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

public class ExoPlayerActivity extends AppCompatActivity {

    PlayerView playerView;
    SimpleExoPlayer player;
    boolean playWhenReady = true;
    int currentWindow = 0;
    long playbackPosition = 0;

    @Override
    protected void onStart() {
        super.onStart();
        if (Util.SDK_INT >= 24)
            initPlayer();
    }

    @Override
    protected void onStop() {
        if (Util.SDK_INT >= 24)
            releasePlayer();
        super.onStop();
    }

    private void releasePlayer() {
        if (player != null) {
            playWhenReady = player.getPlayWhenReady();
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            player.release();
            player = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if ((Util.SDK_INT < 24 || player == null)) {
            initPlayer();
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        playerView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LOW_PROFILE |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION


        );
    }

    @Override
    protected void onPause() {
        if (Util.SDK_INT < 24)
            releasePlayer();
        super.onPause();
    }

    //    // url of video which we are loading.
//    String videoURL = "https://www.youtube.com/watch?v=7Q_8s7LEX1Q";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerView = (PlayerView) findViewById(R.id.ExoPlayerVIew);
        initPlayer();
    }

    private void initPlayer() {
        player = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(player);



        playYoutubeVideo ("https://www.youtube.com/watch?v=7Q_8s7LEX1Q");
    }

    private void playYoutubeVideo(String youtubeUrl) {
        new YouTubeExtractor(this) {

            @Override
            protected void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta videoMeta) {
                if (ytFiles != null) {
                    int videotag = 137;
                    int audiotag = 140;
                    MediaSource audioSource = new ProgressiveMediaSource
                            .Factory(new DefaultHttpDataSource.Factory())
                            .createMediaSource(MediaItem.fromUri(ytFiles.get(audiotag).getUrl()));
                    MediaSource videoSource = new ProgressiveMediaSource
                            .Factory(new DefaultHttpDataSource.Factory())
                            .createMediaSource(MediaItem.fromUri(ytFiles.get(videotag).getUrl()));
                    player.setMediaSource(new MergingMediaSource(
                                    true,
                                    videoSource,
                                    audioSource),
                            true
                    );
                    player.prepare();
                    player.setPlayWhenReady(playWhenReady);
                    player.seekTo(currentWindow, playbackPosition);
                }
            }
        }.extract(youtubeUrl, false, true);
    }
}
//        exoPlayerView = findViewById(R.id.idExoPlayerVIew);
//        try {
//
//            // bandwisthmeter is used for
//            // getting default bandwidth
//            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
//
//            // track selector is used to navigate between
//            // video using a default seekbar.
//            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
//
//            // we are adding our track selector to exoplayer.
//            exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
//
//            // we are parsing a video url
//            // and parsing its video uri.
//            Uri videouri = Uri.parse(videoURL);
//
//            // we are creating a variable for datasource factory
//            // and setting its user agent as 'exoplayer_view'
//            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
//
//            // we are creating a variable for extractor factory
//            // and setting it to default extractor factory.
//            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
//
//            // we are creating a media source with above variables
//            // and passing our event handler as null,
//            MediaSource mediaSource = new ExtractorMediaSource(videouri, dataSourceFactory, extractorsFactory, null, null);
//
//            // inside our exoplayer view
//            // we are setting our player
//            exoPlayerView.setPlayer(exoPlayer);
//
//            // we are preparing our exoplayer
//            // with media source.
//            exoPlayer.prepare(mediaSource);
//
//            // we are setting our exoplayer
//            // when it is ready.
//            exoPlayer.setPlayWhenReady(true);
//
//        } catch (Exception e) {
//            // below line is used for
//            // handling our errors.
//            Log.e("TAG", "Error : " + e.toString());
//        }
//    }
