package gr.aueb.ds.music.framework.parallel.actions.network;

import gr.aueb.ds.music.framework.model.network.ObjectOverNetwork;

public interface NetworkActions<T extends ObjectOverNetwork> {
    void act(T objectOverNetwork);
}
