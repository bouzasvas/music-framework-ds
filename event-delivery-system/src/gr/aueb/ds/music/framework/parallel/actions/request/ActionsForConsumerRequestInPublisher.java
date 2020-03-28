package gr.aueb.ds.music.framework.parallel.actions.request;

import com.mpatric.mp3agic.*;
import gr.aueb.ds.music.framework.helper.LogHelper;
import gr.aueb.ds.music.framework.helper.PropertiesHelper;
import gr.aueb.ds.music.framework.model.dto.ArtistName;
import gr.aueb.ds.music.framework.model.dto.MusicFile;
import gr.aueb.ds.music.framework.parallel.actions.ActionImplementation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ActionsForConsumerRequestInPublisher extends ActionImplementation implements RequestAction<ArtistName> {

    private static final String MUSIC_FILES_DIR = PropertiesHelper.getProperty("music.files.directory");

    public ActionsForConsumerRequestInPublisher(ActionImplementation actionImplementation) {
        super(actionImplementation);
    }

    @Override
    public void handleRequest(ArtistName request) {
        List<MusicFile> musicFiles = this.getMusicFilesFromFileSystem(request.getArtistName());

        try {
            this.objectOutputStream.writeObject(musicFiles);
        } catch (IOException e) {
            LogHelper.error(this.publisher, PropertiesHelper.getProperty("publisher.music.list.send.error"));
        }
    }

    // Iterate over file system
    private List<MusicFile> getMusicFilesFromFileSystem(String artist) {
        List<MusicFile> musicFiles = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(Paths.get(MUSIC_FILES_DIR))) {
            musicFiles = paths
                    .filter(Files::isRegularFile)
                    .map(p -> p.getFileName().toString())
                    .filter(str -> !str.startsWith(".") && str.endsWith(".mp3"))
                    .map(this::mapToMusicFile)
                    .filter(Objects::nonNull)
                    .filter(musicFile -> musicFile.getArtistName().toLowerCase().contains(artist.toLowerCase()))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            LogHelper.errorWithParams(this.publisher, PropertiesHelper.getProperty("publisher.music.list.read.file.system"), MUSIC_FILES_DIR);
        }

        return musicFiles;
    }

    private MusicFile mapToMusicFile(String songName) {
        MusicFile musicFile = null;
        try {
            Mp3File song = new Mp3File(MUSIC_FILES_DIR + "/" + songName);

            musicFile = new MusicFile();

            ID3v1 id3Tag = getRightId3Tag(song);
            musicFile.setTrackName(id3Tag.getTrack());
            musicFile.setArtistName(id3Tag.getArtist());
            musicFile.setGenre(id3Tag.getGenreDescription());
            musicFile.setAlbumInfo(id3Tag.getAlbum());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return musicFile;
    }

    private ID3v1 getRightId3Tag(Mp3File song) {
        ID3v1 id3Tag;
        if (song.hasId3v1Tag() && song.hasId3v2Tag()) {
            id3Tag = song.getId3v2Tag();
        }
        // When song has 2 Tags prefer the Id3v2Tag
        else if (song.hasId3v1Tag()) {
            id3Tag = song.getId3v1Tag();
        }
        else {
            id3Tag = song.getId3v2Tag();
        }

        return id3Tag;
    }
}
