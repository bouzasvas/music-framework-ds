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

import java.util.List;
import java.util.Objects;

import gr.aueb.ds.music.android.lalapp.R;
import gr.aueb.ds.music.android.lalapp.RecyclerItemClickListener;
import gr.aueb.ds.music.android.lalapp.RecyclerViewAdapter;
import gr.aueb.ds.music.android.lalapp.common.AppFileOperations;
import gr.aueb.ds.music.android.lalapp.helpers.NotificationsHelper;
import gr.aueb.ds.music.android.lalapp.request.async.OfflineTrackAsyncRequest;
import gr.aueb.ds.music.framework.model.dto.MusicFile;

public class DownloadsFragment extends Fragment implements RecyclerItemClickListener.OnRecyclerClickListener {

    private List<MusicFile> downloadedMusic;

    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_downloads, container, false);

        this.initRecyclerView(root);
        this.retrieveDownloads();

        return root;
    }

    private void initRecyclerView(View root) {
        this.recyclerView = root.findViewById(R.id.downloads_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(Objects.requireNonNull(getContext())));
        recyclerView.addOnItemTouchListener(new gr.aueb.ds.music.android.lalapp.RecyclerItemClickListener(Objects.requireNonNull(getContext()), recyclerView, this));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void retrieveDownloads() {
        // TODO -- Group by Artist

        try {
            this.downloadedMusic = AppFileOperations.getDownloadedFiles(Objects.requireNonNull(getContext()));
            this.adapter = new RecyclerViewAdapter(this.downloadedMusic);
            this.recyclerView.setAdapter(adapter);

            this.recyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(Objects.requireNonNull(getContext())), DividerItemDecoration.VERTICAL));
        } catch (Exception e) {
            NotificationsHelper.showToastNotification(Objects.requireNonNull(getContext()), getString(R.string.loading_local_files_failed));
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        MusicFile mf = this.downloadedMusic.get(position);

        OfflineTrackAsyncRequest offlineTrackAsyncRequest = new OfflineTrackAsyncRequest(Objects.requireNonNull(getContext()));
        offlineTrackAsyncRequest.execute(mf);
    }
}
