package gr.aueb.ds.music.framework.parallel.actions.request;

import gr.aueb.ds.music.framework.helper.FileSystemHelper;
import gr.aueb.ds.music.framework.helper.PropertiesHelper;
import gr.aueb.ds.music.framework.model.dto.MusicFile;
import gr.aueb.ds.music.framework.model.dto.Value;
import gr.aueb.ds.music.framework.parallel.actions.ActionImplementation;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ActionForTrackRequestInPublisher extends ActionImplementation implements RequestAction<Value> {

    private static final int MAX_CHUNK_SIZE = Integer.parseInt(PropertiesHelper.getProperty("music.files.chunk.size")) * 1024;

    public ActionForTrackRequestInPublisher(ActionImplementation actionImplementation) {
        super(actionImplementation);
    }

    @Override
    public void handleRequest(Value request) {
        MusicFile musicFile = FileSystemHelper.retrieveMusicFileFromDisk(this.publisher, request.getMusicFile());
        transmitMusicFilesInChunks(musicFile);
    }

    private void transmitMusicFilesInChunks(MusicFile musicFile) {
        byte[] musicFileBytes   = musicFile.getMusicFileExtract();
        int totalFileLength     = musicFileBytes.length;
        int numberOfChunks = calculateNumberOfChunks(totalFileLength);

        // Init Byte array Input Stream
//        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(musicFileBytes);

        int currentChunk = 0;
        while (currentChunk < numberOfChunks) {
            MusicFile musicChunkFile = new MusicFile(musicFile);

            int offset = currentChunk * MAX_CHUNK_SIZE;
            int length = MAX_CHUNK_SIZE;
            if (currentChunk == numberOfChunks-1) {
                length = totalFileLength - ((numberOfChunks -1) * MAX_CHUNK_SIZE);
            }

            // Create a byte[] with max Length = MAX_CHUNK_SIZE
            byte[] chunkBytes = new byte[length];
            System.arraycopy(musicFileBytes, offset, chunkBytes, 0, length);

            // Read bytes of original byte[] into chunkBytes[]
//            int length = (int) byteArrayInputStream.skip(currentChunk == 0 ? 0 : MAX_CHUNK_SIZE);
//            byteArrayInputStream.read(chunkBytes, 0, currentChunk != 0 ? Math.min(length, MAX_CHUNK_SIZE): MAX_CHUNK_SIZE);

            musicChunkFile.setMusicFileExtract(chunkBytes);

            // Transmit MusicFile with Chunk Byte[] over the Network
            try {
                this.objectOutputStream.writeObject(musicChunkFile);
            } catch (IOException e) {
                e.printStackTrace();
                // TODO -- Logging
            }

            // Next Chunk
            currentChunk++;
        }

        // Send Null to Terminate Operation
        try {
            this.objectOutputStream.writeObject(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int calculateNumberOfChunks(double fileLength) {
        double chunksDecimal = fileLength / MAX_CHUNK_SIZE;
        int numberOfChunks = (int) Math.ceil(chunksDecimal);

        return numberOfChunks;
    }
}
