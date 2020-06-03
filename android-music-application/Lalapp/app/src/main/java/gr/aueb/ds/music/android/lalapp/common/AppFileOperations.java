package gr.aueb.ds.music.android.lalapp.common;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.Mp3File;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import gr.aueb.ds.music.framework.model.dto.MusicFile;

public class AppFileOperations {

    private static final String CHUNK_SUFFIX = "_chunk";
    public static final String MP3_FORMAT_SUFFIX = ".mp3";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<MusicFile> getDownloadedFiles(Context context) throws Exception {
        List<MusicFile> downloadedMusicFiles;

        try {
            List<File> files = getStoredMusicFiles(context);
            downloadedMusicFiles = mapFilesToMusicFiles(files);
        } catch (IOException | RuntimeException ex) {
            Log.e("AppFileOperations", "getDownloadedFiles", ex);
            throw new Exception(ex);
        }

        return downloadedMusicFiles;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean musicFileExists(Context context, MusicFile mf) {
        try {
            return getStoredMusicFiles(context)
                    .stream()
                    .anyMatch(f -> getFilenameWithoutSuffix(f.getName()).equals(mf.getTrackName()));
        } catch (IOException ex) {
            Log.e("AppFileOperations", "findTrackFromStorage", ex);
            return false;
        }
    }

    public static File getMusicFileFromName(Context context, String musicFileName) {
        File applicationFilesFolder = getApplicationFilesFolder(context);
        return new File(applicationFilesFolder, musicFileName.concat(MP3_FORMAT_SUFFIX));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static byte[] getFileBytes(Context context, String fileName) throws IOException {
        File musicFile = getMusicFileFromName(context, fileName);

        return Files.readAllBytes(musicFile.toPath());
    }

    public static void saveMusicFileInDevice(Context context, MusicFile musicFile) throws Exception {
        File applicationFilesFolder = getApplicationFilesFolder(context);
        File file = new File(applicationFilesFolder, musicFile.getTrackName().concat(MP3_FORMAT_SUFFIX));

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(musicFile.getMusicFileExtract());
        } catch (IOException ex) {
            Log.e("AppFileOperations", "sendMusicFileInDevice: ", ex);
            throw new Exception(ex);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void deleteChunks(Context context) {
        File musicFilesDir = getApplicationFilesFolder(context);

        try {
            Files.walk(musicFilesDir.toPath())
                    .map(Path::toFile)
                    .map(File::getName)
                    .filter(mf -> mf.contains(CHUNK_SUFFIX))
                    .map(mf -> mf.substring(0, mf.indexOf(MP3_FORMAT_SUFFIX)))
                    .forEach(mf -> deleteTmpFile(context, mf));
        }
        catch (IOException ex) {
            Log.e(AppFileOperations.class.getSimpleName(), "deleteChunks", ex);
        }
    }

    public static void deleteTmpFile(Context context, String fileName) {
        File applicationFilesFolder = getApplicationFilesFolder(context);
        File file = new File(applicationFilesFolder, fileName.concat(MP3_FORMAT_SUFFIX));
        file.delete();
    }

    public static File getApplicationFilesFolder(Context context) {
        return context.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static List<File> getStoredMusicFiles(Context context) throws IOException {
        List<File> musicFiles = new ArrayList<>();

        File applicationFilesFolder = getApplicationFilesFolder(context);
        try (Stream<Path> musicFilesStream = Files.walk(applicationFilesFolder.toPath())) {
            musicFiles = musicFilesStream
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .filter(f -> f.getName().endsWith(MP3_FORMAT_SUFFIX))
                    .collect(Collectors.toList());
        } catch (IOException ex) {
            Log.e("AppFileOperations", "getStoredMusicFiles: ", ex);
            throw ex;
        }

        return musicFiles;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static List<MusicFile> mapFilesToMusicFiles(List<File> files) {
        return files
                .stream()
                .map(AppFileOperations::mapFileToMusicFile)
                .collect(Collectors.toList());
    }

    public static MusicFile mapFileToMusicFile(File file) throws RuntimeException {
        MusicFile mf = new MusicFile();

        try {
            String fileName = file.getName();
            Mp3File mp3File = new Mp3File(file);

            ID3v1 id3Tag = getRightId3Tag(mp3File);

            mf.setTrackName(getFilenameWithoutSuffix(fileName));
            mf.setArtistName(id3Tag.getArtist());
            mf.setAlbumInfo(id3Tag.getAlbum());
            mf.setGenre(id3Tag.getGenreDescription());
        } catch (Exception ex) {
            Log.e("AppFileOperations", "mapFileToMusicFile: ", ex);
            throw new RuntimeException(ex);
        }

        return mf;
    }

    public static void copyMp3FileMetadata(File src, File dst) throws RuntimeException {
        try {
            Mp3File mp3FileSrc = new Mp3File(src);
            Mp3File mp3FileDst = new Mp3File(dst);

            mp3FileDst.setId3v1Tag(mp3FileSrc.getId3v1Tag());
            mp3FileDst.save(dst.getAbsolutePath());
        } catch (Exception ex) {
            Log.e("AppFileOperations", "getMp3File: ", ex);
            throw new RuntimeException(ex);
        }
    }

    private static ID3v1 getRightId3Tag(Mp3File song) {
        ID3v1 id3Tag;
        if (song.hasId3v2Tag()) {
            id3Tag = song.getId3v2Tag();
        } else {
            id3Tag = song.getId3v1Tag();
        }

        return id3Tag;
    }

    private static String getFilenameWithoutSuffix(String fileName) {
        return fileName.substring(0, fileName.indexOf(MP3_FORMAT_SUFFIX));
    }
}
