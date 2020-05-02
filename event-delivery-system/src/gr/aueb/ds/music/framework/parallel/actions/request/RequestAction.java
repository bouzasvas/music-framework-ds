package gr.aueb.ds.music.framework.parallel.actions.request;

import gr.aueb.ds.music.framework.model.dto.MusicData;

public interface RequestAction<T extends MusicData<T>> {

    void handleRequest(T request);

}
