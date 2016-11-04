package ru.spbau.bocharov.torrent.client;

import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbau.bocharov.torrent.client.state.StateManager;
import ru.spbau.bocharov.torrent.client.state.TorrentFile;
import ru.spbau.bocharov.torrent.common.Connection;
import ru.spbau.bocharov.torrent.common.ConnectionHandler;
import ru.spbau.bocharov.torrent.common.FileInfo;
import ru.spbau.bocharov.torrent.common.InvalidHandlerStateException;
import ru.spbau.bocharov.torrent.protocol.Request;
import ru.spbau.bocharov.torrent.protocol.RequestType;
import ru.spbau.bocharov.torrent.protocol.requests.*;
import ru.spbau.bocharov.torrent.util.BadCreatorException;
import ru.spbau.bocharov.torrent.util.ObjectFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public class TorrentClient extends ConnectionHandler {

    private static final Logger log = LogManager.getLogger(TorrentClient.class);

    private final static int SCHEDULE_POOL_SIZE = 4;

    private final static int TRACKER_UPDATE_PERIOD = 60; // in seconds
    private final static int LEECH_PERIOD = 10; // in seconds

    private static final ObjectFactory<Byte, Request> requestFactory = ObjectFactory.<Byte, Request>getInstance();
    static {
        try {
            requestFactory.register(RequestType.STAT, StatRequest.class);
            requestFactory.register(RequestType.GET, GetRequest.class);
        } catch (BadCreatorException e) {
            e.printStackTrace();
        }
    }

    private final short localPort;
    private final InetSocketAddress trackerAddress;
    private final ScheduledExecutorService pool = Executors.newScheduledThreadPool(SCHEDULE_POOL_SIZE);

    private final StateManager stateManager;
    private final Storage storage;

    public TorrentClient(short locPort, InetSocketAddress trackerAddr, Storage stor, String statePath) {
        super(locPort);

        localPort = locPort;
        trackerAddress = trackerAddr;
        storage = stor;
        Path path = Paths.get(statePath);
        boolean needLoad = Files.exists(path) && path.toFile().length() > 0;
        stateManager = new StateManager(statePath, needLoad);
    }

    @Override
    public void start() throws IOException, InvalidHandlerStateException {
        super.start();

        pool.scheduleWithFixedDelay(new UpdateTask(), 0, TRACKER_UPDATE_PERIOD, TimeUnit.SECONDS);
        for (TorrentFile file: stateManager.listFiles()) {
            if (!file.isDownloaded()) {
                runLeech(file);
            }
        }
    }

    @Override
    public void stop() throws InterruptedException, IOException {
        super.stop();
        pool.shutdown();
        pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }

    @Override
    protected BaseResponser createResponser(Connection connection) {
        return new Responser(connection);
    }

    public List<FileInfo> list() throws IOException {
        ListRequest request = new ListRequest();
        List<FileInfo> files = new LinkedList<>();
        try (Connection connection = connectToTracker()) {
            request.send(connection);

            DataInputStream in = connection.getIn();
            int count = in.readInt();
            for (int i = 0; i < count; ++i) {
                FileInfo file = new FileInfo();
                file.deserialize(in);
                files.add(file);
            }
        }

        return files;
    }

    public void get(int fileId) throws IOException {
        FileInfo fileInfo = null;
        for (FileInfo file: list()) {
            if (fileId == file.getFileId()) {
                fileInfo = file;
                break;
            }
        }

        if (fileInfo == null) {
            throw new FileNotFoundException(String.format("file with id %d not found", fileId));
        }
        TorrentFile file = TorrentFile.createEmpty(fileInfo.getFileId(), fileInfo.getFileName(), fileInfo.getSize());

        stateManager.addNewFile(file);

        runLeech(file);
    }

    public int upload(String name) throws IOException {
        if (!storage.exists(name)) {
            throw new FileNotFoundException(String.format("file %s doesn't exists", name));
        }

        try (Connection connection = connectToTracker()) {
            long size = storage.fileSize(name);
            UploadRequest request = new UploadRequest(name, size);
            request.send(connection);

            int fileId = connection.getIn().readInt();
            stateManager.addNewFile(TorrentFile.createFull(fileId, name, size));
            return fileId;
        }
    }


    private final class Responser extends BaseResponser {

        Responser(Connection conn) {
            super(conn);
        }

        @Override
        public void visit(ListRequest request) {
            answerUnsupportedRequestType();
        }

        @Override
        public void visit(SourcesRequest request) {
            answerUnsupportedRequestType();
        }

        @Override
        public void visit(UpdateRequest request) {
            answerUnsupportedRequestType();
        }

        @Override
        public void visit(UploadRequest request) {
            answerUnsupportedRequestType();
        }

        @Override
        public void visit(StatRequest request) {
            TorrentFile file = stateManager.getFile(request.getFileId());
            List<Integer> parts = (file == null) ?
                    new LinkedList<>() :
                    file.getParts();

            DataOutputStream out = connection.getOut();
            try {
                out.writeInt(parts.size());
                for (int part: parts) {
                    out.writeInt(part);
                }
            } catch (IOException e) {
                System.err.println("failed to send parts");
                throw new UncheckedIOException(e);
            }
        }

        @Override
        public void visit(GetRequest request) {
            TorrentFile file = stateManager.getFile(request.getFileId());
            DataOutputStream out = connection.getOut();
            long size = 0;
            if (file != null) {
                size = file.getPartSize(request.getPartId());
            }

            try {
                out.writeLong(size);
                if (size > 0) {
                    long offset = request.getPartId() * TorrentFile.PART_SIZE;
                    storage.copyWithOffset(file.getFileName(), out, offset, size);
                }
            } catch (IOException e) {
                System.err.println("failed to send file");
            }
        }
    }


    private final class UpdateTask implements Runnable {

        @Override
        public void run() {
            try (Connection connection = connectToTracker()) {
                List<Integer> files = new LinkedList<>();
                for (TorrentFile file: stateManager.listFiles()) {
                    files.add(file.getFileId());
                }

                UpdateRequest request = new UpdateRequest(localPort, files);
                request.send(connection);

                if (!connection.getIn().readBoolean()) {
                    System.err.println("failed to receive update ack from tracker");
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }


    private final class LeechTask implements Runnable {

        private final TorrentFile leechFile;
        @Setter
        private Future cancelator;

        LeechTask(TorrentFile file) {
            leechFile = file;
        }

        @Override
        public void run() {
            for (InetSocketAddress source: getSources()) {
                for (int part: getSourceParts(source)) {
                    if (!leechFile.hasPart(part)) {
                        downloadPart(source, part);
                    }
                }
                if (leechFile.isDownloaded()) {
                    cancelator.cancel(false);
                }
            }
        }

        private List<InetSocketAddress> getSources() {
            List<InetSocketAddress> sources = new LinkedList<>();

            try (Connection connection = connectToTracker()) {
                SourcesRequest request = new SourcesRequest(leechFile.getFileId());
                request.send(connection);

                DataInputStream in = connection.getIn();
                int count = in.readInt();
                for (int i = 0; i < count; ++i) {
                    String address = in.readUTF();
                    short port = in.readShort();
                    sources.add(new InetSocketAddress(address, port));
                }
            } catch (IOException e) {
                System.err.println(String.format("failed to connect to tracker: %s", e.toString()));
            }

            return sources;
        }

        private List<Integer> getSourceParts(InetSocketAddress source) {
            List<Integer> parts = new LinkedList<>();
            try (Connection connection = new Connection(new Socket(source.getAddress(), source.getPort()))) {
                StatRequest request = new StatRequest(leechFile.getFileId());
                request.send(connection);

                DataInputStream in = connection.getIn();
                int count = in.readInt();
                for (int i = 0; i < count; ++i) {
                    parts.add(in.readInt());
                }
            } catch (IOException e) {
                System.err.println(String.format("failed to connect to seed at %s", source.toString()));
            }
            return parts;
        }

        private void downloadPart(InetSocketAddress source, int part) {
            try (Connection connection = new Connection(new Socket(source.getAddress(), source.getPort()))) {
                GetRequest request = new GetRequest(leechFile.getFileId(), part);
                request.send(connection);

                DataInputStream in = connection.getIn();
                long size = in.readLong();
                if (size > 0) {
                    // TODO: filename + id if files with same name exists
                    storage.copyWithOffset(in, leechFile.getFileName(), part * TorrentFile.PART_SIZE, size);
                    stateManager.addPart(leechFile.getFileId(), part);
                }
            } catch (IOException e) {
                System.err.println(String.format("failed to download part %d from %s: %s",
                        part, source.toString(), e.getMessage()));
                }
        }
    }


    private Connection connectToTracker() throws IOException {
        return new Connection(new Socket(trackerAddress.getAddress(), trackerAddress.getPort()));
    }

    private void runLeech(TorrentFile file) {
        LeechTask leech = new LeechTask(file);
        Future cancelator = pool.scheduleWithFixedDelay(leech, 0, LEECH_PERIOD, TimeUnit.SECONDS);
        leech.setCancelator(cancelator);
    }
}

