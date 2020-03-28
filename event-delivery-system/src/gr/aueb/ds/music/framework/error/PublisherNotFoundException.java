package gr.aueb.ds.music.framework.error;

import gr.aueb.ds.music.framework.helper.PropertiesHelper;

public class PublisherNotFoundException extends Exception {

    public PublisherNotFoundException() {
        this(PropertiesHelper.getProperty("publisher.not.found.for.artist"));
    }

    public PublisherNotFoundException(String message) {
        super(message);
    }
}
