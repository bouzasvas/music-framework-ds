package gr.aueb.ds.music.framework.model.enums;

import gr.aueb.ds.music.framework.nodes.api.Node;

public enum NodeType {
    BROKER,
    PUBLISHER,
    CONSUMER;

    public static NodeType mapNodeClassNameToType(Class<?> clazz) {
        NodeType nodeType = null;

        String className = clazz.getSimpleName();
        if (className.equalsIgnoreCase("BrokerImplementation")) {
            nodeType = NodeType.BROKER;
        }
        else if (className.equalsIgnoreCase("PublisherImplementation")) {
            nodeType = NodeType.PUBLISHER;
        }
        else if (className.equalsIgnoreCase("ConsumerImplementation")) {
            nodeType = NodeType.CONSUMER;
        }

        return nodeType;
    }
}
