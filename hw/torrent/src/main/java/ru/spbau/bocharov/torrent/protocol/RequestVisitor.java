package ru.spbau.bocharov.torrent.protocol;

import ru.spbau.bocharov.torrent.protocol.requests.*;

public interface RequestVisitor {

    // client-server requests

    void visit(ListRequest request);

    void visit(SourcesRequest request);

    void visit(UpdateRequest request);

    void visit(UploadRequest request);

    // client-client requests

    void visit(StatRequest request);

    void visit(GetRequest request);
}
