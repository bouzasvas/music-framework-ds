package gr.aueb.ds.music.android.lalapp.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;

import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.Log;

import java.lang.ref.WeakReference;
import java.util.Optional;

import gr.aueb.ds.music.android.lalapp.R;
import gr.aueb.ds.music.android.lalapp.common.AppCommon;
import gr.aueb.ds.music.android.lalapp.common.AppFileOperations;
import gr.aueb.ds.music.android.lalapp.player.DataSourceProducer;
import gr.aueb.ds.music.android.lalapp.request.async.TrackAsyncRequest;
import gr.aueb.ds.music.framework.model.dto.MusicFile;

public class PlayerActivity extends AppCompatActivity {

    // This field Holds info for the Song Being Player
    private MusicFile musicFile;
    private String tmpMusicFileName;
    private boolean onlineMode = false;

    private SimpleExoPlayer player;
    private ConcatenatingMediaSource mediaSource = new ConcatenatingMediaSource();
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
            this.onlineMode = Optional
                    .ofNullable(AppCommon.readDataFromIntent(intent, "onlineMode"))
                    .map(v -> (Boolean) v)
                    .orElse(false);

            if (onlineMode) TrackAsyncRequest.player = new WeakReference<>(this);
            this.musicFile = AppFileOperations.mapFileToMusicFile(AppFileOperations.getMusicFileFromName(this, this.tmpMusicFileName));

            // TODO -- Delete stored Tmp File
//            AppFileOperations.deleteTmpFile(this, this.tmpMusicFileName);
        } catch (Exception ex) {
            Log.e(PlayerActivity.class.getSimpleName(), "initMusicFile", ex);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void playTrack() {
        DefaultRenderersFactory defaultRenderersFactory = new DefaultRenderersFactory(
                getApplicationContext(), null, DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF
        );

        TrackSelector trackSelector = new DefaultTrackSelector();

        this.player = ExoPlayerFactory.newSimpleInstance(PlayerActivity.this, defaultRenderersFactory, trackSelector);

        // This is the MediaSource representing the media to be played.
        MediaSource musicSource = DataSourceProducer.createMediaSource(this, this.tmpMusicFileName);
        mediaSource.addMediaSource(musicSource);

        // Prepare the player with the source.
        this.player.prepare(mediaSource);

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

    public void addMediaSource(MediaSource mediaSource) {
        this.mediaSource.addMediaSource(mediaSource);
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
