package gr.aueb.ds.music.framework.parallel.actions.node;

import gr.aueb.ds.music.framework.api.nodes.api.Node;

public interface Action<T extends Node> {
    void act (T node);
}
