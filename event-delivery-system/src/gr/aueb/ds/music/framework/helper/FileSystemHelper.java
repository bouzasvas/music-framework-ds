package gr.aueb.ds.music.framework.helper;

import com.mpatric.mp3agic.*;
import gr.aueb.ds.music.framework.model.dto.MusicFile;
import gr.aueb.ds.music.framework.nodes.api.Publisher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileSystemHelper {

    private static final String MUSIC_FILES_DIR = PropertiesHelper.getProperty("music.files.directory");
    private static final String MUSIC_FILES_OUTPUT_DIR = PropertiesHelper.getProperty("music.files.consumer.dir.downloaded");
    private static final String MUSIC_FILES_CHUNKS_OUTPUT_DIR = PropertiesHelper.getProperty("music.files.consumer.directory");

    private static Predicate<String> isMp3File = (file) -> !file.startsWith(".") && file.endsWith(".mp3");

    public static List<MusicFile> getMusicFilesFromFileSystem(Publisher publisher, String artist) {
        List<MusicFile> musicFiles = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(Paths.get(MUSIC_FILES_DIR))) {
            musicFiles = FileSystemHelper
                    .getFilesFromFileSystem(publisher)
                    .stream()
                    .map(FileSystemHelper::mapToMusicFile)
                    .filter(Objects::nonNull)
                    .filter(musicFile -> musicFile.getArtistName().toLowerCase().contains(artist.toLowerCase()))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            LogHelper.errorWithParams(publisher, PropertiesHelper.getProperty("files.get.music.files.from.disk"), MUSIC_FILES_DIR);
            return Collections.emptyList();
        }

        return musicFiles;
    }

    public static MusicFile retrieveMusicFileFromDisk(Publisher publisher, MusicFile musicFile) {
        List<MusicFile> allMusicFiles = FileSystemHelper.getMusicFilesFromFileSystem(publisher, musicFile.getArtistName());

        Optional<MusicFile> musicFileOptional = allMusicFiles
                .stream()
                .filter(mf -> Objects.equals(mf, musicFile))
                .findFirst();

        musicFileOptional.ifPresent(mf -> {
            byte[] musicFileData = getMusicFileData(mf);
            mf.setMusicFileExtract(musicFileData);
        });

        return musicFileOptional
                .filter(mf -> Objects.nonNull(mf.getMusicFileExtract()))
                .orElse(null);
    }

    public static boolean fileHasDownloaded(MusicFile musicFile) {
        Stream<Path> downloadedFilePaths = null;
        try {
            downloadedFilePaths = Files.walk(Paths.get(MUSIC_FILES_OUTPUT_DIR));
        } catch (IOException e) {
            LogHelper.errorWithParams(PropertiesHelper.getProperty("files.music.file.not.exists"), MUSIC_FILES_OUTPUT_DIR);
            return false;
        }

        return downloadedFilePaths
                .filter(Files::isRegularFile)
                .map(p -> p.getFileName().toString())
                .filter(isMp3File)
                .anyMatch(fileName -> fileName.equals(musicFile.getTrackName().concat(".mp3")));
    }

    public static void saveMusicFileToFileSystem(MusicFile musicFile, boolean download) throws IOException {
        String SAVE_DIR = download ? MUSIC_FILES_OUTPUT_DIR : MUSIC_FILES_CHUNKS_OUTPUT_DIR;
        File outputDir = new File(SAVE_DIR);
        if (!outputDir.exists()) outputDir.mkdirs();

        String fileName = musicFile.getTrackName().concat(".mp3");

        File savedFile = new File(outputDir, fileName);
        try (FileOutputStream fos = new FileOutputStream(savedFile)) {
            fos.write(musicFile.getMusicFileExtract());
            fos.flush();
        }
        catch (IOException ex) {
            throw ex;
        }

        copyMp3MetadataToFile(savedFile, musicFile);
    }

    private static List<String> getFilesFromFileSystem(Publisher publisher) {
        List<String> musicFileNames = null;
        try (Stream<Path> paths = Files.walk(Paths.get(MUSIC_FILES_DIR))) {
            musicFileNames = paths
                    .filter(Files::isRegularFile)
                    .map(p -> p.getFileName().toString())
                    .filter(isMp3File)
                    .collect(Collectors.toList());
        }
        catch (IOException ex) {
            LogHelper.errorWithParams(publisher, PropertiesHelper.getProperty("publisher.music.list.read.file.system"), MUSIC_FILES_DIR);
        }

        return musicFileNames;
    }

    private static MusicFile mapToMusicFile(String songName) {
        MusicFile musicFile = null;
        try {
            Mp3File song = new Mp3File(MUSIC_FILES_DIR + "/" + songName);

            musicFile = new MusicFile();

            ID3v1 id3Tag = getRightId3Tag(song);
            musicFile.setTrackName(songName.substring(0, songName.lastIndexOf(".")));
            musicFile.setArtistName(id3Tag.getArtist());
            musicFile.setGenre(id3Tag.getGenreDescription());
            musicFile.setAlbumInfo(id3Tag.getAlbum());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return musicFile;
    }

    private static void copyMp3MetadataToFile(File file, MusicFile musicFile) {
        try {
            Mp3File mp3File = new Mp3File(file);

            ID3v2 id3v2Tag;
            if (mp3File.hasId3v2Tag()) {
                id3v2Tag =  mp3File.getId3v2Tag();
            } else {
                id3v2Tag = new ID3v24Tag();
                mp3File.setId3v2Tag(id3v2Tag);
            }
            id3v2Tag.setTrack(musicFile.getTrackName());
            id3v2Tag.setArtist(musicFile.getArtistName());
            id3v2Tag.setAlbum(musicFile.getAlbumInfo());
            id3v2Tag.setGenreDescription(musicFile.getGenre() == null ? "Other" : musicFile.getGenre());

//            mp3File.save(fileName);
        } catch (Exception e) {
            LogHelper.error(PropertiesHelper.getProperty("files.copy.metadata..to.file"));
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

    private static byte[] getMusicFileData(MusicFile mf) {
        byte[] musicFileData = null;
        try {
            File file = new File(MUSIC_FILES_DIR + "\\" + mf.getTrackName().concat(".mp3"));
            musicFileData = Files.readAllBytes(file.toPath());
        }
        catch (IOException ex) {
            LogHelper.errorWithParams(PropertiesHelper.getProperty("files.get.music.file.bytes"), mf.getTrackName());
        }

        return musicFileData;
    }
}
