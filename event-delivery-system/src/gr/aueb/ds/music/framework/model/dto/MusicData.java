package gr.aueb.ds.music.framework.model.dto;

import gr.aueb.ds.music.framework.model.network.ObjectOverNetwork;

import java.io.Serializable;

public interface MusicData<T> extends Serializable {
    T get();
}
