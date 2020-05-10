package gr.aueb.ds.music.android.lalapp.request.async;

import android.content.Context;

import gr.aueb.ds.music.android.lalapp.R;
import gr.aueb.ds.music.android.lalapp.helpers.NotificationsHelper;
import gr.aueb.ds.music.android.lalapp.request.ConsumerImplementation;
import gr.aueb.ds.music.framework.model.dto.MusicFile;
import gr.aueb.ds.music.framework.model.dto.Value;
import gr.aueb.ds.music.framework.nodes.api.Consumer;

public class TrackAsyncRequest extends MusicFilesManipulationAsync {

    private Consumer consumer;
    private boolean onlineMode;

    public TrackAsyncRequest(Context context, Consumer consumer, boolean onlineMode) {
        super(context);
        this.consumer = consumer;
        this.onlineMode = onlineMode;
    }

    @Override
    protected MusicFile doInBackground(MusicFile... musicFiles) {
        MusicFile musicFile = ((ConsumerImplementation) this.consumer).downloadSelectedTrack(new Value(musicFiles[0]));

        return musicFile;
    }

    @Override
    protected void onPostExecute(MusicFile musicFile) {
        if (!onlineMode) {
            this.saveFileInDevice(musicFile);
            NotificationsHelper.showToastNotification(context, context.getString(R.string.track_downloaded), musicFile.getTrackName());
        }
        else {
            this.playTrack(musicFile);
        }
    }
}
