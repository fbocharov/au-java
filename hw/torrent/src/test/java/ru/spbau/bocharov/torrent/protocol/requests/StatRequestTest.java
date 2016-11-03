package ru.spbau.bocharov.torrent.protocol.requests;

import ru.spbau.bocharov.torrent.protocol.BaseRequestTest;
import ru.spbau.bocharov.torrent.protocol.RequestType;

import static org.junit.Assert.*;

public class StatRequestTest extends BaseRequestTest<StatRequest> {

    private static final int FILE_ID = 123;

    @Override
    protected StatRequest createFilledRequest() {
        return new StatRequest(FILE_ID);
    }

    @Override
    protected StatRequest createEmptyRequest() {
        return new StatRequest();
    }

    @Override
    protected void checkInvariants(StatRequest request) {
        assertEquals(RequestType.STAT, request.getType());
        assertEquals(FILE_ID, request.getFileId());
    }
}