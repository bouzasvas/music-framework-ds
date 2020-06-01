package gr.aueb.ds.music.android.lalapp.request.async;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.IOException;

import gr.aueb.ds.music.android.lalapp.common.AppFileOperations;
import gr.aueb.ds.music.framework.model.dto.MusicFile;

public class OfflineTrackAsyncRequest extends MusicFilesManipulationAsync {

    public OfflineTrackAsyncRequest(Context context) {
        super(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected MusicFile doInBackground(MusicFile... musicFiles) {
        MusicFile mf = musicFiles[0];
        try {
            mf.setMusicFileExtract(AppFileOperations.getFileBytes(this.context, mf.getTrackName()));
        } catch (IOException ex) {
            Log.e(OfflineTrackAsyncRequest.class.getSimpleName(), "doInBackground", ex);
        }

        return mf;
    }

    @Override
    protected void onPostExecute(MusicFile musicFile) {
        super.onPostExecute(musicFile);
        this.playTrackInPlayerActivity(false, musicFile);
    }
}
