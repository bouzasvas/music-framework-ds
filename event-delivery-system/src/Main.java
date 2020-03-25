import gr.aueb.ds.music.framework.commons.SystemExitCodes;
import gr.aueb.ds.music.framework.helper.LogHelper;
import gr.aueb.ds.music.framework.helper.PropertiesHelper;
import gr.aueb.ds.music.framework.nodes.api.Broker;
import gr.aueb.ds.music.framework.nodes.api.Consumer;
import gr.aueb.ds.music.framework.nodes.api.Publisher;
import gr.aueb.ds.music.framework.nodes.impl.BrokerImplementation;
import gr.aueb.ds.music.framework.nodes.impl.ConsumerImplementation;
import gr.aueb.ds.music.framework.nodes.impl.PublisherImplementation;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final String EMPTY_STRING = "";

    private final static Scanner userInput = new Scanner(System.in);

    public static void main(String... args) {
        startMenu();
    }

    private static void startMenu() {
        String userChoice = EMPTY_STRING;
        while (!userChoice.equals(PropertiesHelper.getProperty("main.menu.exit.command"))) {
            showMenu();
            userChoice = userInput.nextLine();

            switch (userChoice.toUpperCase()) {
                case "B":
                    initBroker();
                    break;
                case "C":
                    initConsumer();
                    break;
                case "P":
                    initPublisher();
                    break;
                case "E":
                    LogHelper.printMenuItem(PropertiesHelper.getProperty("main.menu.exit.message"), true);
                    System.exit(SystemExitCodes.USER_REQUEST.getCode());
                    break;
                default:
                    LogHelper.printMenuItem(PropertiesHelper.getProperty("main.menu.not.available.option"), true);
                    LogHelper.printMenuItem(PropertiesHelper.getProperty("main.menu.empty.line"), true);
                    break;
            }
        }
    }

    private static void showMenu() {
        StringBuilder sb = new StringBuilder();
        sb
                .append(String.format(PropertiesHelper.getProperty("main.menu.header"), PropertiesHelper.getProperty("main.menu.exit.command")))
                    .append(LINE_SEPARATOR)
                    .append(LINE_SEPARATOR)
                .append(PropertiesHelper.getProperty("main.menu.broker"))
                    .append(LINE_SEPARATOR)
                .append(PropertiesHelper.getProperty("main.menu.consumer"))
                    .append(LINE_SEPARATOR)
                .append(PropertiesHelper.getProperty("main.menu.publisher"))
                    .append(LINE_SEPARATOR)
                    .append(LINE_SEPARATOR);

        LogHelper.printMenuItem(sb.toString(), true);
        LogHelper.printMenuItem((PropertiesHelper.getProperty("main.menu.user.choice")), false);
    }

    private static void initBroker() {
        LogHelper.printMenuItem(PropertiesHelper.getProperty("main.menu.broker.name"), true);
        String brokerName = userInput.nextLine();

        LogHelper.printMenuItem(PropertiesHelper.getProperty("main.menu.broker.port"), true);
        int port = userInput.nextInt();

        Broker broker = null;
        try {
            broker = new BrokerImplementation(brokerName, port);
        }
        catch (IOException ex) {
            System.err.println(String.format(PropertiesHelper.getProperty("main.init.broker.error"), port));
            System.exit(SystemExitCodes.INIT_BROKER_ERROR.getCode());
        }
    }

    private static void initConsumer() {
        LogHelper.printMenuItem(PropertiesHelper.getProperty("main.menu.consumer.name"), false);
        String consumerName = userInput.nextLine();

        Consumer consumer;
        try {
            consumer = new ConsumerImplementation(consumerName);
        } catch (IOException ex){
            System.err.println(PropertiesHelper.getProperty("main.init.consumer.error"));
            System.exit(SystemExitCodes.INIT_CONSUMER_ERROR.getCode());
        }
    }

    private static void initPublisher() {
        LogHelper.printMenuItem(PropertiesHelper.getProperty("main.menu.publisher.name"), true);
        String publisherName = userInput.nextLine();

        LogHelper.printMenuItem(PropertiesHelper.getProperty("main.menu.publisher.port"), true);
        int port = userInput.nextInt();

        Publisher publisher;
        try {
            publisher = new PublisherImplementation(publisherName, port);
        }
        catch (IOException ex) {
            System.err.println(String.format(PropertiesHelper.getProperty("main.init.publisher.error"), port));
            System.exit(SystemExitCodes.INIT_PUBLISHER_ERROR.getCode());
        }
    }
}
