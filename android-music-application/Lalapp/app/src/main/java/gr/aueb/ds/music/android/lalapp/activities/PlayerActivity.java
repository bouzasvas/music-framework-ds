package gr.aueb.ds.music.android.lalapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;

import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.ByteArrayDataSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import gr.aueb.ds.music.android.lalapp.R;
import gr.aueb.ds.music.android.lalapp.common.AppFileOperations;

public class PlayerActivity extends AppCompatActivity {

    private SimpleExoPlayer player;
    private String tmpMusicFileName;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        byte[] musicFileBytes = initMusicFile();
        playTrack(musicFileBytes);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.player.stop();
        AppFileOperations.deleteTmpFile(this, this.tmpMusicFileName);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private byte[] initMusicFile() {
        byte[] musicFileBytes = null;
        try {
            Intent intent = getIntent();

            this.tmpMusicFileName = (String) intent.getExtras().get("musicFile");
            musicFileBytes = AppFileOperations.getFileBytes(this, this.tmpMusicFileName);
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
}
