package gr.aueb.ds.music.framework.helper;

import gr.aueb.ds.music.framework.commons.ConsoleColors;
import gr.aueb.ds.music.framework.nodes.api.Broker;
import gr.aueb.ds.music.framework.nodes.api.Consumer;
import gr.aueb.ds.music.framework.nodes.api.Node;
import gr.aueb.ds.music.framework.nodes.api.Publisher;
import gr.aueb.ds.music.framework.nodes.impl.NodeAbstractImplementation;

import java.util.Collections;

public class LogHelper {

    // General
    public static void info(Node node, String message) {
        if (node instanceof Broker) LogHelper.info((Broker) node, message);
        else if (node instanceof Publisher) LogHelper.info((Publisher) node, message);
        else if (node instanceof Consumer) LogHelper.info((Consumer) node, message);
        else LogHelper.info(message);
    }

    public static void errorWithParams(Node node, String pattern, String... params) {
        if (node instanceof Broker) LogHelper.errorWithParams((Broker) node, pattern, params);
        else if (node instanceof Publisher) LogHelper.errorWithParams((Publisher) node, pattern, params);
        else if (node instanceof Consumer) LogHelper.errorWithParams((Consumer) node, pattern, params);
    }

    public static void info(String message) {
        System.out.println(message);
    }

    public static void error(String message) {
        String output = "%s";
        LogHelper.error(null, output, message);
    }

    public static void errorWithParams(String message, String... params) {
        String output = "%s";
        String formattedMsg = String.format(message, params);
        LogHelper.error(null, output, formattedMsg);
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

    public static void errorWithParams(Broker broker, String pattern, String... params) {
        String formattedMsg = String.format(pattern, params);
        LogHelper.error(broker, formattedMsg);
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

    public static void errorWithParams(Publisher publisher, String pattern, String... params) {
        String formattedMsg = String.format(pattern, params);
        LogHelper.error(publisher, formattedMsg);
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

    public static void errorWithParams(Consumer consumer, String pattern, String... params) {
        String formattedMsg = String.format(pattern, params);
        LogHelper.error(consumer, formattedMsg);
    }

    // For All
    private static void info(NodeAbstractImplementation node, String pattern, String message) {
        String output = String.format(pattern, node.getNodeDetails().getName(), message);

        System.out.println(ConsoleColors.RESET + String.join("", Collections.nCopies(output.length(), "#")));
        System.out.println(ConsoleColors.RESET + output);
        System.out.println(ConsoleColors.RESET + String.join("", Collections.nCopies(output.length(), "#")));
    }

    private static void error(NodeAbstractImplementation node, String pattern, String message) {
        String output;
        if (node != null) {
             output = String.format(pattern, node.getNodeDetails().getName(), message);
        }
        else {
            if (pattern.split("%s").length == 2) {
                output = String.format(pattern, "(null)", message);
            }
            else {
                output = String.format(pattern, message);
            }
        }

        System.err.println(ConsoleColors.RED_BOLD + String.join("", Collections.nCopies(output.length(), "#")));
        System.err.println(ConsoleColors.RED_BOLD + output);
        System.err.println(ConsoleColors.RED_BOLD + String.join("", Collections.nCopies(output.length(), "#")));
    }
}
