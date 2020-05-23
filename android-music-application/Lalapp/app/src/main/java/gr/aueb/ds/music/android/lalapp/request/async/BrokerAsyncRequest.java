package gr.aueb.ds.music.android.lalapp.request.async;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import gr.aueb.ds.music.android.lalapp.R;
import gr.aueb.ds.music.android.lalapp.request.ConsumerImplementation;
import gr.aueb.ds.music.framework.model.dto.ArtistName;
import gr.aueb.ds.music.framework.model.dto.MusicFile;
import gr.aueb.ds.music.framework.nodes.api.Consumer;

public class BrokerAsyncRequest extends AsyncTaskWithDialog<ArtistName, Void, List<MusicFile>> {

    private AsyncTaskProgress asyncTaskProgress;
    private AsyncTaskError error;

    private Consumer consumer;

    public BrokerAsyncRequest(Context context, AsyncTaskProgress asyncTaskProgress, Map<String, ?> applicationSettings) {
        this.context = context;
        this.asyncTaskProgress = asyncTaskProgress;
        this.consumer = new ConsumerImplementation("android-application", applicationSettings);
    }

    @Override
    protected List<MusicFile> doInBackground(ArtistName... artistNames) {
        // Restore Errors Object
        this.error = null;

        try {
            ((ConsumerImplementation)this.consumer).artistName = artistNames[0];
            this.consumer.init();

            return ((ConsumerImplementation) this.consumer).musicFiles;
        } catch (RuntimeException | IOException e) {
            this.error = new AsyncTaskError();
            this.error.setErrorMessage(this.context.getString(R.string.async_task_connection_error));
            this.error.setException(e);
            this.error.setConnectionError(true);

            Log.e(getClass().getSimpleName(), "doInBackground: ", e);
        }

        // Send Request to Master Broker
        return null;
    }

    @Override
    protected void onPostExecute(List<MusicFile> musicFiles) {
        super.onPostExecute(musicFiles);

        // No Music Files found for Artist
        if (musicFiles == null) {
            this.error = new AsyncTaskError();
            this.error.setErrorMessage(this.context.getString(R.string.async_task_no_results));

            this.asyncTaskProgress.onFailedRequest(this.error);
        }

        if (this.error != null) {
            this.asyncTaskProgress.onFailedRequest(this.error);
        }
        else {
            this.asyncTaskProgress.onSuccessfulRequest(musicFiles);
        }
    }

    public Consumer getConsumer() {
        return consumer;
    }
}
