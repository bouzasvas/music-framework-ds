package gr.aueb.ds.music.android.lalapp.common;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import gr.aueb.ds.music.framework.model.dto.MusicFile;

public class AppFileOperations {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static byte[] getFileBytes(Context context, String fileName) throws IOException {
        File applicationFilesFolder = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(applicationFilesFolder, fileName.concat(".mp3"));

        return Files.readAllBytes(file.toPath());
    }

    public static void saveMusicFileInDevice(Context context, MusicFile musicFile) throws Exception {
        File applicationFilesFolder = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(applicationFilesFolder, musicFile.getTrackName().concat(".mp3"));

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(musicFile.getMusicFileExtract());
        }
        catch (IOException ex) {
            Log.e("AppFileOperations", "sendMusicFileInDevice: ", ex);
            throw new Exception(ex);
        }
    }

    public static void deleteTmpFile(Context context, String fileName) {
        File applicationFilesFolder = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(applicationFilesFolder, fileName.concat(".mp3"));
        file.delete();
    }
}
