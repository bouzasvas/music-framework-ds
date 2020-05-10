package gr.aueb.ds.music.android.lalapp.request.async;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.ByteArrayDataSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import gr.aueb.ds.music.android.lalapp.R;
import gr.aueb.ds.music.android.lalapp.helpers.NotificationsHelper;
import gr.aueb.ds.music.framework.model.dto.MusicFile;

public abstract class MusicFilesManipulationAsync extends AsyncTask<MusicFile, Void, MusicFile> {

    @SuppressLint("StaticFieldLeak")
    protected Context context;

    private ProgressDialog progressDialog;

    @Override
    protected void onPreExecute() {
//        this.progressDialog = ProgressDialog.show(context, "Test", "Test", true);
    }

    public MusicFilesManipulationAsync(Context context) {
        this.context = context;
    }

    protected void playTrack(MusicFile musicFile) {
        DefaultRenderersFactory defaultRenderersFactory = new DefaultRenderersFactory(
                context, null, DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF
        );

        TrackSelector trackSelector = new DefaultTrackSelector();

        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(defaultRenderersFactory, trackSelector);

        String userAgent = Util.getUserAgent(context, "Play Audio");
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, userAgent);

        // This is the MediaSource representing the media to be played.
        MediaSource musicSource = createMediaSourceFromByteArray(musicFile.getMusicFileExtract());

        // Prepare the player with the source.
        player.prepare(musicSource);
        player.setPlayWhenReady(true);

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

    public Uri getUri(byte[] data) {

        try {
            URL url = new URL(null, "bytes:///audio", new URLStreamHandler() {
                @Override
                protected URLConnection openConnection(URL u) throws IOException {
                    return new URLConnection(u) {
                        @Override
                        public void connect() throws IOException {

                        }

                        @Override
                        public InputStream getInputStream() throws IOException {

                            return new ByteArrayInputStream(data);
                        }
                    };
                }
            });
            return Uri.parse( url.toURI().toString());
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }

    protected void saveFileInDevice(MusicFile musicFile) {
        File applicationFilesFolder = this.context.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(applicationFilesFolder, musicFile.getTrackName().concat(".mp3"));

        try (FileOutputStream fos = new FileOutputStream(file)){
            fos.write(musicFile.getMusicFileExtract());
        } catch (Exception ex) {
            Log.e(MusicFilesManipulationAsync.class.getSimpleName(), "saveFileInDevice: ", ex);
            NotificationsHelper.showToastNotification(context, context.getString(R.string.play_track_failure), musicFile.getTrackName());
        }

//        this.progressDialog.dismiss();
    }
}
