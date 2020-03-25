package gr.aueb.ds.music.framework.helper;

import gr.aueb.ds.music.framework.commons.ConsoleColors;
import gr.aueb.ds.music.framework.nodes.api.Broker;
import gr.aueb.ds.music.framework.nodes.api.Consumer;
import gr.aueb.ds.music.framework.nodes.api.Publisher;
import gr.aueb.ds.music.framework.nodes.impl.NodeAbstractImplementation;

import java.util.Collections;

public class LogHelper {

    // General
    public static void error(String message) {
        String output = "%s";
        LogHelper.error(null, output, message);
    }

    public static void userInput(String inputMesage) {
        LogHelper.userInputWithColor(ConsoleColors.RESET, inputMesage, false);
    }

    public static void userInputWithColor(String color, String inputMessage) {
        LogHelper.userInputWithColor(color, inputMessage, false);
    }

    public static void userInputWithColor(String color, String inputMessage, boolean newLine) {
        System.out.print(color + inputMessage + (newLine ? "\n" : ""));
    }

    public static void printMenuItem(String message, boolean newLine) {
        LogHelper.userInputWithColor(ConsoleColors.CYAN, message, newLine);
    }

    // Broker
    public static void info(Broker broker, String message) {
        String output = "Broker %s: %s";
        LogHelper.info((NodeAbstractImplementation) broker, output, message);
    }

    public static void error(Broker broker, String message) {
        String output = "Broker %s: %s";
        LogHelper.error((NodeAbstractImplementation) broker, output, message);
    }

    // Publisher
    public static void info(Publisher publisher, String message) {
        String output = "Publisher %s: %s";
        LogHelper.info((NodeAbstractImplementation) publisher, output, message);
    }

    public static void error(Publisher publisher, String message) {
        String output = "Publisher %s: %s";
        LogHelper.error((NodeAbstractImplementation) publisher, output, message);
    }

    // Consumer
    public static void info(Consumer consumer, String message) {
        String output = "Consumer %s: %s";
        LogHelper.info((NodeAbstractImplementation) consumer, output, message);
    }

    public static void error(Consumer consumer, String message) {
        String output = "Consumer %s: %s";
        LogHelper.error((NodeAbstractImplementation) consumer, output, message);
    }



    private static void info(NodeAbstractImplementation node, String pattern, String message) {
        String output = String.format(pattern, node.getNodeDetails().getName(), message);

        System.out.println(String.join("", Collections.nCopies(output.length(), "#")));
        System.out.println(output);
        System.out.println(String.join("", Collections.nCopies(output.length(), "#")));
    }

    private static void error(NodeAbstractImplementation node, String pattern, String message) {
        String output;
        if (node != null) {
             output = String.format(pattern, node.getNodeDetails().getName(), message);
        }
        else {
            output = String.format(pattern, message);
        }

        System.err.println(String.join("", Collections.nCopies(output.length(), "#")));
        System.err.println(output);
        System.err.println(String.join("", Collections.nCopies(output.length(), "#")));
    }
}
