package gr.aueb.ds.music.android.lalapp.recyclerview.items;

import gr.aueb.ds.music.framework.model.dto.ArtistName;

public class ArtistItem extends GenericItem {

    private ArtistName artistName;

    public ArtistItem(ArtistName artistName) {
        this.artistName = artistName;
    }

    @Override
    public int getType() {
        return TYPE_ARTIST;
    }

    public ArtistName getArtistName() {
        return artistName;
    }

    public void setArtistName(ArtistName artistName) {
        this.artistName = artistName;
    }
}
