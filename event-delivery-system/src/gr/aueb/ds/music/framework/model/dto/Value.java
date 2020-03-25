package gr.aueb.ds.music.framework.model.dto;

import gr.aueb.ds.music.framework.model.network.ObjectOverNetwork;

import java.io.Serializable;
import java.util.Objects;

public class Value implements MusicData<MusicFile> {
    private static final long serialVersionUID = 3514995194910638315L;

    private MusicFile musicFile;

    public Value() {
    }

    public Value(MusicFile musicFile) {
        this.musicFile = musicFile;
    }

    public MusicFile getMusicFile() {
        return musicFile;
    }

    public void setMusicFile(MusicFile musicFile) {
        this.musicFile = musicFile;
    }

    @Override
    public MusicFile get() {
        return this.musicFile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Value value = (Value) o;
        return Objects.equals(musicFile, value.musicFile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(musicFile);
    }

    @Override
    public String toString() {
        return "Value : [musicFile] = " + this.musicFile.toString();
    }
}
