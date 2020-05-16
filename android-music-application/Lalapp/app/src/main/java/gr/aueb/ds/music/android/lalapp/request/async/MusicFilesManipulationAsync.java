package gr.aueb.ds.music.android.lalapp.request.async;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

import gr.aueb.ds.music.android.lalapp.activities.PlayerActivity;
import gr.aueb.ds.music.android.lalapp.R;
import gr.aueb.ds.music.android.lalapp.common.AppCommon;
import gr.aueb.ds.music.android.lalapp.common.AppFileOperations;
import gr.aueb.ds.music.android.lalapp.helpers.NotificationsHelper;
import gr.aueb.ds.music.framework.model.dto.MusicFile;

public abstract class MusicFilesManipulationAsync extends AsyncTask<MusicFile, Void, MusicFile> {

    @SuppressLint("StaticFieldLeak")
    protected Context context;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        AppCommon.showDialog(this.context, context.getString(R.string.dialog_loading));
    }

    @Override
    protected void onPostExecute(MusicFile musicFile) {
        super.onPostExecute(musicFile);
        AppCommon.dismissDialog();
    }

    public MusicFilesManipulationAsync(Context context) {
        this.context = context;
    }

    protected void playTrackInPlayerActivity(MusicFile musicFile) {
        musicFile.setTrackName("tmp_" + musicFile.getTrackName());
        saveFileInDevice(musicFile);

        Intent playerActivityIntent = new Intent(context, PlayerActivity.class);
        playerActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        playerActivityIntent.putExtra("musicFile", musicFile.getTrackName());

        context.startActivity(playerActivityIntent);
    }

    protected void saveFileInDevice(MusicFile musicFile) {
        try {
            AppFileOperations.saveMusicFileInDevice(this.context, musicFile);
        } catch (Exception ex) {
            Log.e(MusicFilesManipulationAsync.class.getSimpleName(), "saveFileInDevice: ", ex);
            NotificationsHelper.showToastNotification(context, context.getString(R.string.play_track_failure), musicFile.getTrackName());
        }
    }
}
