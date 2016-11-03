package ru.spbau.bocharov.torrent.protocol.requests;

import ru.spbau.bocharov.torrent.protocol.BaseRequestTest;
import ru.spbau.bocharov.torrent.protocol.RequestType;

import static org.junit.Assert.*;

public class SourcesRequestTest extends BaseRequestTest<SourcesRequest> {

    private static final int FILE_ID = 604;

    @Override
    protected SourcesRequest createFilledRequest() {
        return new SourcesRequest(FILE_ID);
    }

    @Override
    protected SourcesRequest createEmptyRequest() {
        return new SourcesRequest();
    }

    @Override
    protected void checkInvariants(SourcesRequest request) {
        assertEquals(RequestType.SOURCES, request.getType());
        assertEquals(FILE_ID, request.getFileId());
    }
}