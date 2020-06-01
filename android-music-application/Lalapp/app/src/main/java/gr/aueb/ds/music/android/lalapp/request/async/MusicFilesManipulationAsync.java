package gr.aueb.ds.music.android.lalapp.request.async;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import gr.aueb.ds.music.android.lalapp.R;
import gr.aueb.ds.music.android.lalapp.activities.PlayerActivity;
import gr.aueb.ds.music.android.lalapp.common.AppFileOperations;
import gr.aueb.ds.music.android.lalapp.helpers.NotificationsHelper;
import gr.aueb.ds.music.framework.model.dto.MusicFile;

public abstract class MusicFilesManipulationAsync extends AsyncTaskWithDialog<MusicFile, MusicFile, MusicFile> {


    public MusicFilesManipulationAsync(Context context) {
        this.context = context;
    }

    protected void playTrackInPlayerActivity(boolean onlineMode, MusicFile musicFile) {
        Intent playerActivityIntent = new Intent(context, PlayerActivity.class);
        playerActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        playerActivityIntent.putExtra("musicFile", musicFile.getTrackName());
        playerActivityIntent.putExtra("onlineMode", onlineMode);

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
