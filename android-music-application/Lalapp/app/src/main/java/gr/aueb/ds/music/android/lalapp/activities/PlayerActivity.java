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
import com.google.android.exoplayer2.upstream.ByteArrayDataSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.Optional;

import gr.aueb.ds.music.android.lalapp.R;
import gr.aueb.ds.music.android.lalapp.common.AppFileOperations;
import gr.aueb.ds.music.framework.model.dto.MusicFile;

public class PlayerActivity extends AppCompatActivity {

    // This field Holds info for the Song Being Player
    private MusicFile musicFile;
    private String tmpMusicFileName;

    private SimpleExoPlayer player;
    private PlayerNotificationManager playerNotificationManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        byte[] musicFileBytes = initMusicFile();
        playTrack(musicFileBytes);
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
    private byte[] initMusicFile() {
        byte[] musicFileBytes = null;
        try {
            Intent intent = getIntent();

            this.tmpMusicFileName = Optional
                    .ofNullable(intent.getExtras())
                    .map(bundle -> bundle.get("musicFile"))
                    .map(String::valueOf)
                    .orElse("");
            this.musicFile = AppFileOperations.mapFileToMusicFile(AppFileOperations.getMusicFileFromName(this, this.tmpMusicFileName));
            musicFileBytes = AppFileOperations.getFileBytes(this, this.tmpMusicFileName);

            // Delete stored Tmp File
            AppFileOperations.deleteTmpFile(this, this.tmpMusicFileName);
        }
        catch (Exception ex) {
            // TODO
        }

        return musicFileBytes;
    }

    private void playTrack(byte[] musicFileBytes) {
        DefaultRenderersFactory defaultRenderersFactory = new DefaultRenderersFactory(
                getApplicationContext(), null, DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF
        );

        TrackSelector trackSelector = new DefaultTrackSelector();

        this.player = ExoPlayerFactory.newSimpleInstance(PlayerActivity.this, defaultRenderersFactory, trackSelector);

        String userAgent = Util.getUserAgent(getApplicationContext(), "Lalapp");
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getApplicationContext(), userAgent);

        // This is the MediaSource representing the media to be played.
        MediaSource musicSource = createMediaSourceFromByteArray(musicFileBytes);

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

    private MediaSource createMediaSourceFromByteArray(byte[] data) {
        ByteArrayDataSource byteArrayDataSource = new ByteArrayDataSource(data);
        DataSource.Factory factory = new DataSource.Factory() {
            @Override
            public DataSource createDataSource() {
                return byteArrayDataSource;
            }
        };
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
            return tmpTrackName.replace("tmp_", "");
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
