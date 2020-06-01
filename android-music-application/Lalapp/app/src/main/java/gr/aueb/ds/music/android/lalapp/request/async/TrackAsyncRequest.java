package gr.aueb.ds.music.android.lalapp.request.async;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import gr.aueb.ds.music.android.lalapp.R;
import gr.aueb.ds.music.android.lalapp.activities.PlayerActivity;
import gr.aueb.ds.music.android.lalapp.common.AppFileOperations;
import gr.aueb.ds.music.android.lalapp.helpers.NotificationsHelper;
import gr.aueb.ds.music.android.lalapp.player.DataSourceProducer;
import gr.aueb.ds.music.android.lalapp.request.ConsumerImplementation;
import gr.aueb.ds.music.framework.model.dto.MusicFile;
import gr.aueb.ds.music.framework.model.dto.Value;
import gr.aueb.ds.music.framework.nodes.api.Consumer;

public class TrackAsyncRequest extends MusicFilesManipulationAsync {

    public static WeakReference<PlayerActivity> player;

    private ConsumerImplementation consumer;
    private boolean onlineMode;

    public TrackAsyncRequest(Context context, Consumer consumer, boolean onlineMode) {
        super(context);

        TrackAsyncRequest.player = null;
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

            int chunkNo = 1;

            MusicFile mfChunk;
            while ((mfChunk = this.consumer.getMusicFileChunk(musicFileRequest, chunkNo)) != null) {
                try {
                    AppFileOperations.saveMusicFileInDevice(this.context, mfChunk);
                    chunkNo++;

                    publishProgress(mfChunk);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return musicFile;
    }

    @Override
    protected void onProgressUpdate(MusicFile... values) {
        super.onProgressUpdate(values);

        MusicFile mf = values[0];
        boolean firstChunk = mf.getTrackName().endsWith("_chunk1");

        if (firstChunk) {
            this.playTrackInPlayerActivity(true, mf);
        }
        else {
            player.get().addMediaSource(DataSourceProducer.createMediaSource(context, mf.getTrackName()));
        }
    }

    @Override
    protected void onPostExecute(MusicFile musicFile) {
        super.onPostExecute(musicFile);

        if (!onlineMode) {
            this.saveFileInDevice(musicFile);
            NotificationsHelper.showToastNotification(context, context.getString(R.string.track_downloaded), musicFile.getTrackName());
        } else {
//            this.playTrackInPlayerActivity(musicFile);
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
