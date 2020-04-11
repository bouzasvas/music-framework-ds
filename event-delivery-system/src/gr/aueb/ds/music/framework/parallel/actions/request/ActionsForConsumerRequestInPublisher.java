package gr.aueb.ds.music.framework.parallel.actions.request;

import com.mpatric.mp3agic.*;
import gr.aueb.ds.music.framework.helper.FileSystemHelper;
import gr.aueb.ds.music.framework.helper.LogHelper;
import gr.aueb.ds.music.framework.helper.PropertiesHelper;
import gr.aueb.ds.music.framework.model.dto.ArtistName;
import gr.aueb.ds.music.framework.model.dto.MusicFile;
import gr.aueb.ds.music.framework.parallel.actions.ActionImplementation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
