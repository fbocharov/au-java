package ru.spbau.bocharov.serverbench.server.impl.tcp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbau.bocharov.serverbench.common.ProtocolIO;
import ru.spbau.bocharov.serverbench.server.BaseServer;
import ru.spbau.bocharov.serverbench.server.algo.Sort;
import ru.spbau.bocharov.serverbench.server.util.ServerStatistics;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PollingTCPServer extends BaseServer {

    private static final Logger log = LogManager.getLogger(PollingTCPServer.class);
    private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 3;

    private volatile Selector selector;
    private volatile ServerSocketChannel serverChannel;
    private final List<Channel> channels = new LinkedList<>();
    private final ExecutorService pool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    public PollingTCPServer(int port) {
        super(port);
    }

    @Override
    protected Runnable createServer(int port) {
        return () -> {
            try (Selector s = Selector.open()) {
                selector = s;

                serverChannel = createServerSocket(port);
                channels.add(serverChannel);

                while (selector.select() > 0) {
                    Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                    while (it.hasNext()) {
                        SelectionKey key = it.next();
                        it.remove();

                        if (key.isAcceptable()) {
                            accept(key);
                        }

                        if (key.isReadable()) {
                            read(key);
                        }

                        if (key.isWritable()) {
                            write(key);
                        }
                    }
                }
            } catch (IOException e) {
                log.error("io error occured: " + e.getMessage());
                try {
                    closeAll();
                } catch (IOException e1) {
                    log.error("yo dawg i heard yo like exceptions so we throw you exception while yo handling exception: "
                            + e.getMessage());
                    e = e1;
                }
                e.printStackTrace();
            }

        };
    }

    @Override
    protected void shutdownServer() throws IOException, InterruptedException {
        closeAll();
    }

    private static class Context {
        volatile long requestStartTime;
        volatile long clientTime;

        final ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
        volatile ByteBuffer request;
        volatile ByteBuffer response;

        volatile State state = State.READING_SIZE;

        enum State {
            READING_SIZE,
            READING_DATA,
            EXECUTING,
            WRITING_RESPONSE
        }

        void reset() {
            requestStartTime = 0;
            clientTime = 0;

            state = State.READING_SIZE;

            sizeBuffer.clear();
            request = null;
            response = null;
        }
    }

    private void accept(SelectionKey key) throws IOException {
        SocketChannel clientChannel = ((ServerSocketChannel) key.channel()).accept();
        clientChannel.configureBlocking(false);
        clientChannel.socket().setTcpNoDelay(false);
        clientChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE,  new Context());
        channels.add(clientChannel);
    }

    private void read(SelectionKey key) throws IOException {
        Context context = (Context) key.attachment();
        SocketChannel channel = (SocketChannel) key.channel();
        switch (context.state) {
            case READING_SIZE:
                if (context.requestStartTime == 0) {
                    context.requestStartTime = System.nanoTime();
                }

                int readCount = channel.read(context.sizeBuffer);
                if (readCount < 0) {
                    key.cancel();
                    channel.close();
                    return;
                }

                if (context.sizeBuffer.hasRemaining()) {
                    break;
                }

                context.sizeBuffer.flip();
                context.request = ByteBuffer.allocate(context.sizeBuffer.getInt());
            case READING_DATA:
                channel.read(context.request);
                if (context.request.hasRemaining()) {
                    break;
                }

                context.request.flip();
                byte[] bytes =  new byte[context.request.position()];
                context.request.get(bytes);
                context.state = Context.State.EXECUTING;
                pool.execute(() -> {
                    try {
                        int[] array = ProtocolIO.fromRaw(bytes);
                        context.clientTime = System.nanoTime();
                        Sort.insertionSort(array);
                        context.clientTime = System.nanoTime() - context.clientTime;
                        byte[] msgBytes = ProtocolIO.toRaw(array);

                        context.response = ByteBuffer.allocate(4 + msgBytes.length);
                        context.response.putInt(msgBytes.length);
                        context.response.put(msgBytes);
                        context.response.flip();
                        context.state = Context.State.WRITING_RESPONSE;
                    } catch (IOException e) {
                        log.error("io exception occured: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
        }
    }

    private void write(SelectionKey key) throws IOException {
        Context context = (Context) key.attachment();
        if (context.state == Context.State.WRITING_RESPONSE) {
            SocketChannel channel = (SocketChannel) key.channel();
            if (context.response.hasRemaining()) {
                channel.write(context.response);
            }

            if (!context.response.hasRemaining()) {
                long requestTime = System.nanoTime() - context.requestStartTime;
                ServerStatistics.getInstance().push(context.clientTime, requestTime);
                context.reset();
            }
        }
    }

    private void closeAll() throws IOException {
        selector.close();
        for (Channel channel: channels) {
            channel.close();
        }
    }

    private ServerSocketChannel createServerSocket(int port) throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(new InetSocketAddress(port));
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        return serverChannel;
    }
}
