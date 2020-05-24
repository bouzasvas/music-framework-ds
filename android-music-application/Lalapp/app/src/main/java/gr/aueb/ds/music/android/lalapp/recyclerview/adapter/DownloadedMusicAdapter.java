package gr.aueb.ds.music.android.lalapp.recyclerview.adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import gr.aueb.ds.music.android.lalapp.R;
import gr.aueb.ds.music.android.lalapp.recyclerview.items.ArtistItem;
import gr.aueb.ds.music.android.lalapp.recyclerview.items.GenericItem;
import gr.aueb.ds.music.android.lalapp.recyclerview.items.TrackItem;

public class DownloadedMusicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<GenericItem> musicItems;

    public DownloadedMusicAdapter(List<GenericItem> downloadedMusicItems) {
        this.musicItems = downloadedMusicItems;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,  int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {

            case GenericItem.TYPE_ARTIST:
                View v2 = inflater.inflate(R.layout.recycler_view_artist_item, parent, false);
                viewHolder = new ArtistNameViewHolder(v2);
                break;

            case GenericItem.TYPE_TRACK:
                View v1 = inflater.inflate(R.layout.recycler_view_track_item, parent, false);
                viewHolder = new RecyclerViewAdapter.MusicFileViewHolder(v1);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        switch (viewHolder.getItemViewType()) {

            case GenericItem.TYPE_ARTIST:
                ArtistItem artistItem = (ArtistItem) musicItems.get(position);
                ArtistNameViewHolder artistNameViewHolder = (ArtistNameViewHolder) viewHolder;

                artistNameViewHolder.artistName.setText(artistItem.getArtistName().getArtistName());

                break;

            case GenericItem.TYPE_TRACK:

                TrackItem trackItem = (TrackItem) musicItems.get(position);
                RecyclerViewAdapter.MusicFileViewHolder holder = (RecyclerViewAdapter.MusicFileViewHolder) viewHolder;

                if ((position -1) % 2 == 0)
                    holder.itemView.setBackgroundColor(Color.parseColor("#f2ffe5"));
                else
                    holder.itemView.setBackgroundColor(Color.parseColor("#f7f5ed"));

                holder.songName.setText(trackItem.getMusicFile().getTrackName());
                holder.artistName.setText(trackItem.getMusicFile().getArtistName());
                holder.album.setText(trackItem.getMusicFile().getAlbumInfo());

                break;
        }
    }


    // ViewHolder for date row item
    static class ArtistNameViewHolder extends RecyclerView.ViewHolder {
        TextView artistName;

        ArtistNameViewHolder(View v) {
            super(v);
            this.artistName = v.findViewById(R.id.artist_name);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return musicItems.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return musicItems != null ? musicItems.size() : 0;
    }
}
