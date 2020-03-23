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
                    System.out.println(PropertiesHelper.getProperty("main.menu.exit.message"));
                    System.exit(1);
                    break;
                default:
                    System.out.println(PropertiesHelper.getProperty("main.menu.not.available.option"));
                    System.out.println(PropertiesHelper.getProperty("main.menu.empty.line"));
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
                    .append(LINE_SEPARATOR)
                .append(PropertiesHelper.getProperty("main.menu.user.choice"));

        System.out.println(sb.toString());
    }

    private static void initBroker() {
        System.out.print(PropertiesHelper.getProperty("main.menu.broker.name"));
        String brokerName = userInput.nextLine();

        System.out.print(PropertiesHelper.getProperty("main.menu.broker.port"));
        int port = userInput.nextInt();

        Broker broker = null;
        try {
            broker = new BrokerImplementation(brokerName, port);
        }
        catch (IOException ex) {
            System.err.println(String.format(PropertiesHelper.getProperty("main.init.broker.error"), port));
            System.exit(-2);
        }
    }

    private static void initConsumer() {
        System.out.print(PropertiesHelper.getProperty("main.menu.consumer.name"));
        String consumerName = userInput.nextLine();

        Consumer consumer;
        try {
            consumer = new ConsumerImplementation(consumerName);
        } catch (IOException ex){
            System.err.println(PropertiesHelper.getProperty("main.init.consumer.error"));
            System.exit(-3);
        }
    }

    private static void initPublisher() {
        System.out.print(PropertiesHelper.getProperty("main.menu.publisher.name"));
        String publisherName = userInput.nextLine();

        System.out.print(PropertiesHelper.getProperty("main.menu.publisher.port"));
        int port = userInput.nextInt();

        Publisher publisher;
        try {
            publisher = new PublisherImplementation(publisherName, port);
        }
        catch (IOException ex) {
            System.err.println(String.format(PropertiesHelper.getProperty("main.init.publisher.error"), port));
            System.exit(-4);
        }
    }
}
