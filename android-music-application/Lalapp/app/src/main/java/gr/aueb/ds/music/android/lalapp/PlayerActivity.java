package gr.aueb.ds.music.android.lalapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import gr.aueb.ds.music.framework.model.dto.MusicFile;

public class PlayerActivity extends AppCompatActivity {

    public static MusicFile musicFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

//        MusicFile musicFile = initMusicFile();
        playTrack(musicFile);
    }

    private MusicFile initMusicFile() {
        MusicFile musicFile = null;

        try {
            Intent intent = getIntent();

            musicFile = (MusicFile) intent.getExtras().get("musicFile");
        }
        catch (Exception ex) {
            // TODO
        }

        return musicFile;
    }

    private void playTrack(MusicFile musicFile) {
        DefaultRenderersFactory defaultRenderersFactory = new DefaultRenderersFactory(
                getApplicationContext(), null, DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF
        );

        TrackSelector trackSelector = new DefaultTrackSelector();

        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(defaultRenderersFactory, trackSelector);

        String userAgent = Util.getUserAgent(getApplicationContext(), "Lalapp");
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getApplicationContext(), userAgent);

        // This is the MediaSource representing the media to be played.
        MediaSource musicSource = createMediaSourceFromByteArray(musicFile.getMusicFileExtract());

        // Prepare the player with the source.
        player.prepare(musicSource);

        attachPlayerToView(player);
        player.setPlayWhenReady(true);
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
