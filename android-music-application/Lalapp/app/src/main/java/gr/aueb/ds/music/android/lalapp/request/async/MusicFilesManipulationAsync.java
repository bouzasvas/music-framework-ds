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

import gr.aueb.ds.music.android.lalapp.PlayerActivity;
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

    protected void playTrackInPlayerActivity(MusicFile musicFile) {
        Intent playerActivityIntent = new Intent(context, PlayerActivity.class);
        playerActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        playerActivityIntent.putExtra("musicFile", musicFile);

        // TODO -- Vres lysh malaka
        PlayerActivity.musicFile = musicFile;

        context.startActivity(playerActivityIntent);
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
