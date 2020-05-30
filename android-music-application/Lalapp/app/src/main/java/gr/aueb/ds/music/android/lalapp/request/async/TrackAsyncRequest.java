package gr.aueb.ds.music.android.lalapp.request.async;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import gr.aueb.ds.music.android.lalapp.R;
import gr.aueb.ds.music.android.lalapp.helpers.NotificationsHelper;
import gr.aueb.ds.music.android.lalapp.request.ConsumerImplementation;
import gr.aueb.ds.music.framework.model.dto.MusicFile;
import gr.aueb.ds.music.framework.model.dto.Value;
import gr.aueb.ds.music.framework.nodes.api.Consumer;

public class TrackAsyncRequest extends MusicFilesManipulationAsync {

    private ConsumerImplementation consumer;
    private boolean onlineMode;

    public TrackAsyncRequest(Context context, Consumer consumer, boolean onlineMode) {
        super(context);

        this.consumer = (ConsumerImplementation) consumer;
        this.onlineMode = onlineMode;
    }

    @Override
    protected MusicFile doInBackground(MusicFile... musicFiles) {
        MusicFile musicFile = null;

        Value musicFileRequest = new Value(musicFiles[0]);
        // When in Offline mode download whole Track and Store it in Device Storage
        if (!onlineMode) {
            musicFile = this.consumer.downloadSelectedTrack(musicFileRequest);
        }
        // Download Chunks 1 by 1
        else {
            /* TODO -- Implement Chucks Task

                    1. Save each Chunk in Device Storage using publishProgress & Implement onProgressUpdate
                        which will save the file and update the ConcatenatedMediaSource of PlayerActivity
                        (see WeakReference<Activity>)
                    2. onPostExecute delete tmp files

             */

            List<MusicFile> musicFilesChunks = this.consumer.getMusicFileChunks(musicFileRequest);
            musicFile = mergeChunks(musicFilesChunks);
        }

        return musicFile;
    }

    @Override
    protected void onPostExecute(MusicFile musicFile) {
        super.onPostExecute(musicFile);

        if (!onlineMode) {
            this.saveFileInDevice(musicFile);
            NotificationsHelper.showToastNotification(context, context.getString(R.string.track_downloaded), musicFile.getTrackName());
        } else {
            this.playTrackInPlayerActivity(musicFile);
        }
    }

    private MusicFile mergeChunks(List<MusicFile> musicFilesChunks){
        MusicFile mergedMf = new MusicFile(musicFilesChunks.get(0));

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            for (MusicFile mf : musicFilesChunks) {
                bos.write(mf.getMusicFileExtract());
            }

            mergedMf.setMusicFileExtract(bos.toByteArray());
        }
        catch (IOException ex) {}

        return mergedMf;
    }
}
