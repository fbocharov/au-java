package ru.spbau.bocharov.torrent.tracker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbau.bocharov.torrent.common.Connection;
import ru.spbau.bocharov.torrent.common.ConnectionHandler;
import ru.spbau.bocharov.torrent.common.FileInfo;
import ru.spbau.bocharov.torrent.protocol.Request;
import ru.spbau.bocharov.torrent.protocol.RequestType;
import ru.spbau.bocharov.torrent.protocol.requests.*;
import ru.spbau.bocharov.torrent.tracker.state.StateManager;
import ru.spbau.bocharov.torrent.util.BadCreatorException;
import ru.spbau.bocharov.torrent.util.ObjectFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;


public class TorrentTracker extends ConnectionHandler {

    private static final Logger log = LogManager.getLogger(TorrentTracker.class);

    private static final ObjectFactory<Byte, Request> requestFactory = ObjectFactory.<Byte, Request>getInstance();
    static {
        try {
            requestFactory.register(RequestType.LIST, ListRequest.class);
            requestFactory.register(RequestType.SOURCES, SourcesRequest.class);
            requestFactory.register(RequestType.UPDATE, UpdateRequest.class);
            requestFactory.register(RequestType.UPLOAD, UploadRequest.class);
        } catch (BadCreatorException e) {
            e.printStackTrace();
        }
    }

    private static final short DEFAULT_PORT = 8081;

    private final StateManager stateManager;

    public TorrentTracker(String statePath) {
        super(DEFAULT_PORT);
        Path path = Paths.get(statePath);
        boolean needLoad = Files.exists(path) && path.toFile().length() > 0;
        stateManager = new StateManager(statePath, needLoad);
    }

    @Override
    protected BaseResponser createResponser(Connection connection) {
        return new Responser(connection);
    }

    private class Responser extends BaseResponser {

        Responser(Connection conn) {
            super(conn);
        }

        @Override
        public void visit(ListRequest request) {
            log.info("handling LIST request");

            DataOutputStream out = connection.getOut();
            List<FileInfo> files = stateManager.listFiles();
            try {
                out.writeInt(files.size());
                for (FileInfo file: files) {
                    file.serialize(out);
                }
            } catch (IOException e) {
                log.error("failed to list files");
            }
        }

        @Override
        public void visit(SourcesRequest request) {
            log.info("handling SOURCES request");

            Set<InetSocketAddress> sources = stateManager.getFileSources(request.getFileId());
            DataOutputStream out = connection.getOut();
            try {
                out.writeInt(sources.size());
                for (InetSocketAddress source: sources) {
                    out.writeUTF(source.getAddress().getHostAddress());
                    out.writeShort(source.getPort());
                }
            } catch (IOException e) {
                log.error("failed to send sources");
            }
        }

        @Override
        public void visit(UpdateRequest request) {
            log.info("handling UPDATE request");

            short port = request.getClientPort();
            InetSocketAddress source;
            try {
                source = new InetSocketAddress(
                        InetAddress.getByName(connection.getHostAddress()), port);
            } catch (UnknownHostException e) {
                log.error("failed to create source");
                return;
            }

            for (int id: request.getFileIds()) {
                stateManager.updateSource(id, source);
            }

            try {
                connection.getOut().writeBoolean(true);
            } catch (IOException e) {
                log.error("failed to ack updates");
            }
        }

        @Override
        public void visit(UploadRequest request) {
            log.info("handling UPLOAD request");

            int id = stateManager.generateFileId();
            try {
                connection.getOut().writeInt(id);
            } catch (IOException e) {
                log.error("failed to send file id, file not added");
                return;
            }
            stateManager.addNewFile(id, request.getFileName(), request.getSize());
        }

        @Override
        public void visit(StatRequest request) {
            answerUnsupportedRequestType();
        }

        @Override
        public void visit(GetRequest request) {
            answerUnsupportedRequestType();
        }
    }
}
