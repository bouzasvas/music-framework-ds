package gr.aueb.ds.music.android.lalapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import gr.aueb.ds.music.framework.model.dto.MusicFile;

public class RecyclerViewAdapter
        extends RecyclerView.Adapter<RecyclerViewAdapter.MusicFileViewHolder> {
    private List<MusicFile> musicFileList;

    public RecyclerViewAdapter(List<MusicFile> musicFileList) {
        this.musicFileList = musicFileList;
    }

    @NonNull
    @Override
    public MusicFileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);
        return new MusicFileViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return (!musicFileList.isEmpty()) ? musicFileList.size() : 0;
    }

    @Override
    public void onBindViewHolder(@NonNull MusicFileViewHolder holder, int position) {
        holder.songName.setText(this.musicFileList.get(position).getTrackName());
        holder.artistName.setText(this.musicFileList.get(position).getArtistName());
        holder.album.setText(this.musicFileList.get(position).getAlbumInfo());
    }

    static class MusicFileViewHolder extends RecyclerView.ViewHolder {
        TextView songName;
        TextView artistName;
        TextView album;

        MusicFileViewHolder(@NonNull View itemView) {
            super(itemView);
            songName = itemView.findViewById(R.id.songName);
            artistName = itemView.findViewById(R.id.artistName);
            album = itemView.findViewById(R.id.album);
        }
    }
}
