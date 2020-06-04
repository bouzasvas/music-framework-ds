package gr.aueb.ds.music.android.lalapp.request.async;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Optional;

import gr.aueb.ds.music.android.lalapp.R;
import gr.aueb.ds.music.android.lalapp.activities.PlayerActivity;
import gr.aueb.ds.music.android.lalapp.common.AppFileOperations;
import gr.aueb.ds.music.android.lalapp.helpers.NotificationsHelper;
import gr.aueb.ds.music.android.lalapp.request.ConsumerImplementation;
import gr.aueb.ds.music.framework.model.dto.MusicFile;
import gr.aueb.ds.music.framework.model.dto.Value;
import gr.aueb.ds.music.framework.nodes.api.Consumer;

public class TrackAsyncRequest extends MusicFilesManipulationAsync {

    public static WeakReference<PlayerActivity> player;

    private ConsumerImplementation consumer;

    private File musicFile;
    private int currentChunk;
    private boolean onlineMode;

    private FileOutputStream fileOutputStream;

    public TrackAsyncRequest(Context context, Consumer consumer, boolean onlineMode) {
        super(context);

        TrackAsyncRequest.player = null;
        this.consumer = (ConsumerImplementation) consumer;
        this.onlineMode = onlineMode;
    }

    private void initFileStream(MusicFile mf) {
        String musicFileName = this.onlineMode
                ? mf.getTrackName().concat("_chunk")
                : mf.getTrackName();

        this.musicFile = new File(AppFileOperations.getApplicationFilesFolder(context), musicFileName.concat(AppFileOperations.MP3_FORMAT_SUFFIX));
        try {
            this.fileOutputStream = new FileOutputStream(musicFile);
        } catch (FileNotFoundException ex) {
            Log.e(getClass().getSimpleName(), "initFileStream", ex);
        }
    }

    @Override
    protected MusicFile doInBackground(MusicFile... musicFiles) {
        MusicFile musicFile = null;

        initFileStream(musicFiles[0]);

        Value musicFileRequest = new Value(musicFiles[0]);

        this.currentChunk = 1;
        MusicFile mfChunk;
        while ((mfChunk = this.consumer.getMusicFileChunk(musicFileRequest, this.currentChunk)) != null) {
            musicFile = mfChunk;
            try {
                this.currentChunk++;
                publishProgress(mfChunk);
            } catch (Exception ex) {
                Log.e(getClass().getSimpleName(), "doInBackground", ex);
            }
        }

        return musicFile;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onProgressUpdate(MusicFile... values) {
        super.onProgressUpdate(values);

        MusicFile mf = values[0];
        saveFileInDevice(mf);

        if (onlineMode) playChunk(mf);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onPostExecute(MusicFile musicFile) {
        super.onPostExecute(musicFile);

        closeOfflineStream();
        updatePlayer();

        String trackName = musicFile.getTrackName().substring(0, musicFile.getTrackName().indexOf("_"));
        NotificationsHelper.showToastNotification(context, context.getString(R.string.track_downloaded), trackName);
    }

    @Override
    protected void saveFileInDevice(MusicFile musicFile) {
        try {
            this.fileOutputStream.write(musicFile.getMusicFileExtract());
        } catch (IOException ex) {
            Log.e(getClass().getSimpleName(), "saveFileInDevice", ex);
        }
    }

    private void closeOfflineStream() {
        try {
            this.fileOutputStream.close();
        } catch (IOException ex) {
            Log.e(getClass().getSimpleName(), "closeOfflineStream", ex);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updatePlayer() {
        Optional
                .ofNullable(TrackAsyncRequest.player)
                .map(WeakReference::get)
                .ifPresent(PlayerActivity::updatePlayer);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void playChunk(MusicFile mf) {
        if (isFirstChunk(mf)) {
            mf.setTrackName(mf.getTrackName().substring(0, mf.getTrackName().length()-1));
            this.playTrackInPlayerActivity(true, mf);
        }
    }

    private boolean isFirstChunk(MusicFile mf) {
        return mf.getTrackName().endsWith("_chunk1");
    }
}
