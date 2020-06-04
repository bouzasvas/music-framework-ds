package gr.aueb.ds.music.android.lalapp.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
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

    public boolean isActive = true;

    // This field Holds info for the Song Being Player
    private MusicFile musicFile;
    private String tmpMusicFileName;
    private boolean onlineMode = false;

    public SimpleExoPlayer player;
    private MediaSource mediaSource;
    private PlayerNotificationManager playerNotificationManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        this.isActive = true;

        initMusicFile();
        playTrack();
        initPlayerNotificationBar();
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onDestroy() {
        super.onDestroy();

        // Delete Chunks from Storage
        AppFileOperations.deleteChunks(this);

        // This Flag is read by AsyncTask
        this.isActive = false;

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
        } catch (Exception ex) {
            Log.e(PlayerActivity.class.getSimpleName(), "initMusicFile", ex);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void playTrack() {
        this.player = new SimpleExoPlayer
                .Builder(PlayerActivity.this)
                .build();

        // This is the MediaSource representing the media to be played.
        this.mediaSource = DataSourceProducer.createMediaSource(this, this.tmpMusicFileName);

        // Prepare the player with the source.
        this.player.prepare(this.mediaSource);

        TrackAsyncRequest.player = new WeakReference<>(this);

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

    public void updatePlayer() {
        this.player.prepare(this.mediaSource, false, false);
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
            Intent intent = new Intent(PlayerActivity.this, PlayerActivity.class);
            return PendingIntent.getActivity(PlayerActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
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
