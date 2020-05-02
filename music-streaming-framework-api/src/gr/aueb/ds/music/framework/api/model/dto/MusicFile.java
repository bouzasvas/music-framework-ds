package gr.aueb.ds.music.framework.api.model.dto;

import java.io.Serializable;
import java.util.Objects;

public class MusicFile implements Serializable {
    private static final long serialVersionUID = 8811579927563597549L;

    private String trackName;
    private String artistName;
    private String albumInfo;
    private String genre;
    private byte[] musicFileExtract;

    public MusicFile() {
    }

    public MusicFile(MusicFile musicFile) {
        this.trackName  = musicFile.trackName;
        this.artistName = musicFile.artistName;
        this.albumInfo  = musicFile.albumInfo;
        this.genre      = musicFile.genre;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getAlbumInfo() {
        return albumInfo;
    }

    public void setAlbumInfo(String albumInfo) {
        this.albumInfo = albumInfo;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public byte[] getMusicFileExtract() {
        return musicFileExtract;
    }

    public void setMusicFileExtract(byte[] musicFileExtract) {
        this.musicFileExtract = musicFileExtract;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MusicFile musicFile = (MusicFile) o;
        return Objects.equals(trackName, musicFile.trackName) &&
                Objects.equals(artistName, musicFile.artistName) &&
                Objects.equals(albumInfo, musicFile.albumInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trackName, artistName, albumInfo);
    }

    @Override
    public String toString() {
        String pattern = "%s - %s :: (%s)";
        return String.format(pattern, this.artistName, this.trackName, this.albumInfo);
    }
}
