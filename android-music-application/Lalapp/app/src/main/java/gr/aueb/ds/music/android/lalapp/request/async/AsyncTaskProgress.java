package gr.aueb.ds.music.android.lalapp.request.async;

import java.util.List;

import gr.aueb.ds.music.framework.model.dto.MusicFile;

public interface AsyncTaskProgress {
    void onSuccessfulRequest(List<MusicFile> musicFiles);
    void onFailedRequest();
}
