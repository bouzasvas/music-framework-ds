package gr.aueb.ds.music.android.lalapp.recyclerview.items;

public abstract class GenericItem {

    public static final int TYPE_ARTIST = 0;
    public static final int TYPE_TRACK = 1;

    public abstract int getType();

}
