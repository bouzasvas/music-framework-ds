package gr.aueb.ds.music.android.lalapp.request.async;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import gr.aueb.ds.music.framework.model.dto.MusicFile;

public class OfflineTrackAsyncRequest extends MusicFilesManipulationAsync {

    public OfflineTrackAsyncRequest(Context context) {
        super(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected MusicFile doInBackground(MusicFile... musicFiles) {
        MusicFile mf = musicFiles[0];

        // Do Nothing except for passing MusicFile to DataSourceProducer

        return mf;
    }

    @Override
    protected void onPostExecute(MusicFile musicFile) {
        super.onPostExecute(musicFile);
        this.playTrackInPlayerActivity(false, musicFile);
    }
}
