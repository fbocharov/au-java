package ru.spbau.bocharov.torrent.protocol.requests;

import ru.spbau.bocharov.torrent.protocol.BaseRequestTest;
import ru.spbau.bocharov.torrent.protocol.RequestType;

import static org.junit.Assert.*;

public class GetRequestTest extends BaseRequestTest<GetRequest> {

    private static final int PART_ID = 22;
    private static final int FILE_ID = 8;

    @Override
    protected GetRequest createFilledRequest() {
        return new GetRequest(FILE_ID, PART_ID);
    }

    @Override
    protected GetRequest createEmptyRequest() {
        return new GetRequest();
    }

    @Override
    protected void checkInvariants(GetRequest request) {
        assertEquals(RequestType.GET, request.getType());
        assertEquals(FILE_ID, request.getFileId());
        assertEquals(PART_ID, request.getPartId());
    }
}