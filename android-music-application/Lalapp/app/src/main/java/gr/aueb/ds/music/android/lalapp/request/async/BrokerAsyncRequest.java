package gr.aueb.ds.music.android.lalapp.request.async;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;

import gr.aueb.ds.music.android.lalapp.request.ConsumerImplementation;
import gr.aueb.ds.music.framework.model.dto.ArtistName;
import gr.aueb.ds.music.framework.model.dto.MusicFile;
import gr.aueb.ds.music.framework.nodes.api.Consumer;

public class BrokerAsyncRequest extends AsyncTask<ArtistName, Void, List<MusicFile>> {

    private AsyncTaskProgress asyncTaskProgress;
    private AsyncTaskError error;

    private Consumer consumer;

    public BrokerAsyncRequest(AsyncTaskProgress asyncTaskProgress) {
        this.asyncTaskProgress = asyncTaskProgress;
        this.consumer = new ConsumerImplementation("android-application");
    }

    @Override
    protected List<MusicFile> doInBackground(ArtistName... artistNames) {
        // Restore Errors Object
        this.error = null;

        try {
            ((ConsumerImplementation)this.consumer).artistName = artistNames[0];
            this.consumer.init();

            return ((ConsumerImplementation) this.consumer).musicFiles;
        } catch (IOException e) {

            e.printStackTrace();
        }

        // Send Request to Master Broker
        return null;
    }

    @Override
    protected void onPostExecute(List<MusicFile> musicFiles) {
        super.onPostExecute(musicFiles);

        if (this.error != null) {
            this.asyncTaskProgress.onFailedRequest();
        }

        this.asyncTaskProgress.onSuccessfulRequest(musicFiles);
    }

    // Private Class for Error Handling
    private class AsyncTaskError {
        private String errorMessage;
        private Boolean connectionError;
        private Exception exception;

        public AsyncTaskError() {
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public Boolean getConnectionError() {
            return connectionError;
        }

        public void setConnectionError(Boolean connectionError) {
            this.connectionError = connectionError;
        }

        public Exception getException() {
            return exception;
        }

        public void setException(Exception exception) {
            this.exception = exception;
        }
    }
}
