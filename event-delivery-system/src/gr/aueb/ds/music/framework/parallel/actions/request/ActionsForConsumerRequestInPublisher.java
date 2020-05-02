package gr.aueb.ds.music.framework.parallel.actions.request;

import gr.aueb.ds.music.framework.helper.FileSystemHelper;
import gr.aueb.ds.music.framework.helper.LogHelper;
import gr.aueb.ds.music.framework.helper.PropertiesHelper;
import gr.aueb.ds.music.framework.model.dto.ArtistName;
import gr.aueb.ds.music.framework.model.dto.MusicFile;
import gr.aueb.ds.music.framework.parallel.actions.ActionImplementation;

import java.io.IOException;
import java.util.List;

public class ActionsForConsumerRequestInPublisher extends ActionImplementation implements RequestAction<ArtistName> {


    public ActionsForConsumerRequestInPublisher(ActionImplementation actionImplementation) {
        super(actionImplementation);
    }

    @Override
    public void handleRequest(ArtistName request) {
        List<MusicFile> musicFiles = FileSystemHelper.getMusicFilesFromFileSystem(this.publisher, request.getArtistName());

        try {
            this.objectOutputStream.writeObject(musicFiles);
        } catch (IOException e) {
            LogHelper.error(this.publisher, PropertiesHelper.getProperty("publisher.music.list.send.error"));
        }
    }
}
