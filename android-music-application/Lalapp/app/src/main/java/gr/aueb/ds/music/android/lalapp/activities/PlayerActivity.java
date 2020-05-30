package gr.aueb.ds.music.android.lalapp.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;

import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;

import gr.aueb.ds.music.android.lalapp.R;
import gr.aueb.ds.music.android.lalapp.common.AppCommon;
import gr.aueb.ds.music.android.lalapp.common.AppFileOperations;
import gr.aueb.ds.music.android.lalapp.player.DataSourceProducer;
import gr.aueb.ds.music.framework.model.dto.MusicFile;

public class PlayerActivity extends AppCompatActivity {

    // This field Holds info for the Song Being Player
    private MusicFile musicFile;
    private String tmpMusicFileName;

    private byte[] musicFileBytes;

    private SimpleExoPlayer player;
    private PlayerNotificationManager playerNotificationManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        initMusicFile();
        playTrack();
        initPlayerNotificationBar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Stop the Player
        this.player.release();
        // Stop Notifications
        this.playerNotificationManager.setPlayer(null);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initMusicFile() {
        try {
            Intent intent = getIntent();

            // Read Data from Intent
            this.tmpMusicFileName = AppCommon.readDataFromIntent(intent, "musicFile");
            Boolean onlineMode = AppCommon.readDataFromIntent(intent, "onlineMode");

            // If offline mode read file from Device Storage
            if (onlineMode == null || !onlineMode) {
                this.musicFile = AppFileOperations.mapFileToMusicFile(AppFileOperations.getMusicFileFromName(this, this.tmpMusicFileName));
                this.musicFileBytes = AppFileOperations.getFileBytes(this, this.tmpMusicFileName);
            }
            else if (onlineMode) {
//                this.musicFileBytesStream
            }

            // Delete stored Tmp File
            AppFileOperations.deleteTmpFile(this, this.tmpMusicFileName);
        }
        catch (Exception ex) {
            Log.e(PlayerActivity.class.getSimpleName(), "initMusicFile", ex);
        }
    }

    private void playTrack() {
        DefaultRenderersFactory defaultRenderersFactory = new DefaultRenderersFactory(
                getApplicationContext(), null, DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF
        );

        TrackSelector trackSelector = new DefaultTrackSelector();

        this.player = ExoPlayerFactory.newSimpleInstance(PlayerActivity.this, defaultRenderersFactory, trackSelector);

        String userAgent = Util.getUserAgent(getApplicationContext(), "Lalapp");
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getApplicationContext(), userAgent);

        // This is the MediaSource representing the media to be played.
        MediaSource musicSource = createMediaSource();

        // Prepare the player with the source.
        this.player.prepare(musicSource);

        attachPlayerToView(player);
        this.player.setPlayWhenReady(true);
    }

    private void initPlayerNotificationBar() {
        this.playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(this, "Lala App", R.string.app_name, 1, mediaDescriptionAdapter);
        this.playerNotificationManager.setPlayer(this.player);
    }

    // Helper Methods
    private void attachPlayerToView(SimpleExoPlayer player) {
        PlayerView playerView = (PlayerView) findViewById(R.id.player_view);
        playerView.setPlayer(player);
    }

    private MediaSource createMediaSource() {
        DataSource mediaDataSource = DataSourceProducer.createDataSource(this.musicFileBytes);
        DataSource.Factory factory = () -> mediaDataSource;
        MediaSource mediaSource = new ExtractorMediaSource.Factory(factory)
                .setExtractorsFactory(new DefaultExtractorsFactory())
                .createMediaSource(Uri.EMPTY);

        return mediaSource;
    }

    // ExoPlayer Notification Bar
    private PlayerNotificationManager.MediaDescriptionAdapter mediaDescriptionAdapter = new PlayerNotificationManager.MediaDescriptionAdapter() {
        @Override
        public String getCurrentContentTitle(Player player) {
            String tmpTrackName = PlayerActivity.this.musicFile.getTrackName();

            // Remove _tmp if Exists
            return tmpTrackName.replace("tmp_", "").replace("_chunk1", "");
        }

        @Override
        public PendingIntent createCurrentContentIntent(Player player) {
            return null;
        }

        @Override
        public String getCurrentContentText(Player player) {
            return PlayerActivity.this.musicFile.getArtistName();
        }

        @Override
        public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
            return null;
        }
    };
}
