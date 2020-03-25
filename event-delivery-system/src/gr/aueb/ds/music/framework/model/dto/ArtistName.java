package gr.aueb.ds.music.framework.model.dto;

import java.io.Serializable;
import java.util.Objects;

public class ArtistName implements MusicData<ArtistName> {
    private static final long serialVersionUID = -5133667647262845405L;

    private String artistName;

    public ArtistName() {
    }

    public ArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    @Override
    public ArtistName get() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArtistName that = (ArtistName) o;
        return Objects.equals(artistName, that.artistName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(artistName);
    }

    @Override
    public String toString() {
        return "ArtistName : [artistName] = " + this.artistName;
    }
}
