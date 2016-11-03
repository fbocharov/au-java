package ru.spbau.bocharov.torrent.protocol.requests;

import ru.spbau.bocharov.torrent.protocol.BaseRequestTest;
import ru.spbau.bocharov.torrent.protocol.RequestType;

import static org.junit.Assert.*;

public class UploadRequestTest extends BaseRequestTest<UploadRequest> {

    private static final String FILE_NAME = "super-file1.txt";
    private static final long SIZE = 12345;

    @Override
    protected UploadRequest createFilledRequest() {
        return new UploadRequest(FILE_NAME, SIZE);
    }

    @Override
    protected UploadRequest createEmptyRequest() {
        return new UploadRequest();
    }

    @Override
    protected void checkInvariants(UploadRequest request) {
        assertEquals(RequestType.UPLOAD, request.getType());
        assertEquals(FILE_NAME, request.getFileName());
        assertEquals(SIZE, request.getSize());
    }
}