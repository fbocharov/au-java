package ru.spbau.bocharov.torrent.protocol.requests;

import ru.spbau.bocharov.torrent.protocol.BaseRequestTest;
import ru.spbau.bocharov.torrent.protocol.RequestType;

import static org.junit.Assert.*;

public class ListRequestTest extends BaseRequestTest<ListRequest> {

    @Override
    protected ListRequest createFilledRequest() {
        return new ListRequest();
    }

    @Override
    protected ListRequest createEmptyRequest() {
        return new ListRequest();
    }

    @Override
    protected void checkInvariants(ListRequest request) {
        assertEquals(RequestType.LIST, request.getType());
    }
}