package gr.aueb.ds.music.framework.parallel.actions.node;

import gr.aueb.ds.music.framework.requests.NodeRequest;

public interface Action<T extends NodeRequest> {
    void act (T node);
}
