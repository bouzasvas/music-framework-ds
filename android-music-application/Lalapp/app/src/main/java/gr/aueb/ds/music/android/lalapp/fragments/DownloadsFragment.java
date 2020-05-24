package gr.aueb.ds.music.android.lalapp.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import gr.aueb.ds.music.android.lalapp.R;
import gr.aueb.ds.music.android.lalapp.common.AppFileOperations;
import gr.aueb.ds.music.android.lalapp.helpers.NotificationsHelper;
import gr.aueb.ds.music.android.lalapp.recyclerview.adapter.DownloadedMusicAdapter;
import gr.aueb.ds.music.android.lalapp.recyclerview.items.ArtistItem;
import gr.aueb.ds.music.android.lalapp.recyclerview.items.GenericItem;
import gr.aueb.ds.music.android.lalapp.recyclerview.items.TrackItem;
import gr.aueb.ds.music.android.lalapp.recyclerview.listeners.RecyclerItemClickListener;
import gr.aueb.ds.music.android.lalapp.request.async.OfflineTrackAsyncRequest;
import gr.aueb.ds.music.framework.model.dto.ArtistName;
import gr.aueb.ds.music.framework.model.dto.MusicFile;

public class DownloadsFragment extends Fragment implements RecyclerItemClickListener.OnRecyclerClickListener {

    private List<GenericItem> downloadedMusicItems;

    private RecyclerView recyclerView;
    private DownloadedMusicAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        List<MusicFile> downloadedFiles = this.retrieveDownloads();

        View root;
        if (downloadedFiles.isEmpty()) {
            root = inflater.inflate(R.layout.fragment_downloads_no_tracks, container, false);
        }
        else {
            // Inflate the layout for this fragment
            root = inflater.inflate(R.layout.fragment_downloads, container, false);

            this.initRecyclerView(root);
            this.initRecyclerViewAdapter(downloadedFiles);
        }


        return root;
    }

    private void initRecyclerView(View root) {
        this.recyclerView = root.findViewById(R.id.downloads_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(Objects.requireNonNull(getContext())));
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(Objects.requireNonNull(getContext()), recyclerView, this));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private List<MusicFile> retrieveDownloads() {
        List<MusicFile> downloadedMusic = new ArrayList<>();
        try {
            downloadedMusic = AppFileOperations.getDownloadedFiles(Objects.requireNonNull(getContext()));
        } catch (Exception e) {
            NotificationsHelper.showToastNotification(Objects.requireNonNull(getContext()), getString(R.string.loading_local_files_failed));
        }

        return downloadedMusic;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initRecyclerViewAdapter(List<MusicFile> downloadedMusic) {
        prepareGroupedMusicItemsForAdapter(downloadedMusic);

        this.adapter = new DownloadedMusicAdapter(this.downloadedMusicItems);
        this.recyclerView.setAdapter(adapter);

        this.recyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(Objects.requireNonNull(getContext())), DividerItemDecoration.VERTICAL));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void prepareGroupedMusicItemsForAdapter(List<MusicFile> downloadedMusic) {
        Map<ArtistName, List<MusicFile>> groupedByArtistTracks =
                downloadedMusic
                        .stream()
                        .collect(Collectors.groupingBy(mf -> new ArtistName(mf.getArtistName())));

        this.downloadedMusicItems = new ArrayList<>();
        for (ArtistName artistName : groupedByArtistTracks.keySet()) {
            ArtistItem artistItem = new ArtistItem(artistName);
            this.downloadedMusicItems.add(artistItem);

            for (MusicFile mf : Objects.requireNonNull(groupedByArtistTracks.get(artistName))) {
                TrackItem trackItem = new TrackItem(mf);
                this.downloadedMusicItems.add(trackItem);
            }
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        GenericItem item = this.downloadedMusicItems.get(position);

        if (item instanceof ArtistItem) return;;

        MusicFile mf = ((TrackItem) item).getMusicFile();

        OfflineTrackAsyncRequest offlineTrackAsyncRequest = new OfflineTrackAsyncRequest(Objects.requireNonNull(getContext()));
        offlineTrackAsyncRequest.execute(mf);
    }
}
