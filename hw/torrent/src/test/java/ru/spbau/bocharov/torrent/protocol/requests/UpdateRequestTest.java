package ru.spbau.bocharov.torrent.protocol.requests;

import ru.spbau.bocharov.torrent.protocol.BaseRequestTest;
import ru.spbau.bocharov.torrent.protocol.RequestType;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class UpdateRequestTest extends BaseRequestTest<UpdateRequest> {

    private static final short PORT = 9876;
    private static final List<Integer> FILES = Arrays.asList(1, 2, 3, 4, 5);

    @Override
    protected UpdateRequest createFilledRequest() {
        return new UpdateRequest(PORT, FILES);
    }

    @Override
    protected UpdateRequest createEmptyRequest() {
        return new UpdateRequest();
    }

    @Override
    protected void checkInvariants(UpdateRequest request) {
        assertEquals(RequestType.UPDATE, request.getType());
        assertEquals(PORT, request.getClientPort());
        assertEquals(FILES, request.getFileIds());
    }
}