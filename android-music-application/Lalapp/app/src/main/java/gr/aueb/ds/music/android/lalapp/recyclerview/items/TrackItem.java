package gr.aueb.ds.music.android.lalapp.recyclerview.items;

import gr.aueb.ds.music.framework.model.dto.MusicFile;

public class TrackItem extends GenericItem {

    private MusicFile musicFile;

    public TrackItem(MusicFile musicFiles) {
        this.musicFile = musicFiles;
    }

    public MusicFile getMusicFile() {
        return musicFile;
    }

    public void setMusicFile(MusicFile musicFile) {
        this.musicFile = musicFile;
    }

    @Override
    public int getType() {
        return TYPE_TRACK;
    }
}
