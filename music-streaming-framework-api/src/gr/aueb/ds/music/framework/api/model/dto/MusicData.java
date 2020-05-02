package gr.aueb.ds.music.framework.api.model.dto;

import java.io.Serializable;

public interface MusicData<T> extends Serializable {
    T get();
}
