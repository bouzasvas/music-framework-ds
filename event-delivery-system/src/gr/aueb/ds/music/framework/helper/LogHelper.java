package gr.aueb.ds.music.framework.helper;

import gr.aueb.ds.music.framework.nodes.api.Broker;
import gr.aueb.ds.music.framework.nodes.api.Consumer;
import gr.aueb.ds.music.framework.nodes.api.Publisher;
import gr.aueb.ds.music.framework.nodes.impl.BrokerImplementation;
import gr.aueb.ds.music.framework.nodes.impl.NodeAbstractImplementation;

public class LogHelper {

    public static void info(Broker broker, String message) {
        String output = "Broker %s: %s";
        LogHelper.info((NodeAbstractImplementation) broker, output, message);
    }

    public static void info(Publisher publisher, String message) {
        String output = "Publisher %s: %s";
        LogHelper.info((NodeAbstractImplementation) publisher, output, message);
    }

    public static void info(Consumer consumer, String message) {
        String output = "Consumer %s: %s";
        LogHelper.info((NodeAbstractImplementation) consumer, output, message);
    }

    public static void error(Broker broker, String message) {
        String output = "Broker %s: %s";
        LogHelper.error((NodeAbstractImplementation) broker, output, message);
    }

    private static void info(NodeAbstractImplementation node, String pattern, String message) {
        String output = String.format(pattern, node.getNodeDetails().getName(), message);

        System.out.println("#####################################################");
        System.out.println(output);
        System.out.println("#####################################################");
    }

    private static void error(NodeAbstractImplementation node, String pattern, String message) {
        String output = String.format(pattern, node.getNodeDetails().getName(), message);

        System.err.println("#################################################");
        System.err.println(output);
        System.err.println("#################################################");
    }
}
