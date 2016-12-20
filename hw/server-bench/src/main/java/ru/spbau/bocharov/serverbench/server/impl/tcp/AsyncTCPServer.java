package ru.spbau.bocharov.serverbench.server.impl.tcp;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbau.bocharov.serverbench.common.ProtocolIO;
import ru.spbau.bocharov.serverbench.server.BaseServer;
import ru.spbau.bocharov.serverbench.server.algo.Sort;
import ru.spbau.bocharov.serverbench.server.util.ServerStatistics;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AsyncTCPServer extends BaseServer {

    private static final Logger log = LogManager.getLogger(AsyncTCPServer.class);

    private volatile AsynchronousServerSocketChannel serverChannel;

    public AsyncTCPServer(int port) {
        super(port);
    }

    @Override
    protected Runnable createServer(int port) {
        return () -> {
            try {
                serverChannel = AsynchronousServerSocketChannel.open();
                serverChannel.bind(new InetSocketAddress(port));
                serverChannel.accept(null, new AsyncAcceptHandler());
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    @Override
    protected void shutdownServer() throws IOException, InterruptedException {
        if (serverChannel != null) {
            serverChannel.close();
        }
        serverChannel = null;
    }

    private static class Context {
        volatile long clientTime;
        volatile long requestStartTime = System.nanoTime();

        final AsynchronousSocketChannel channel;

        final ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
        volatile ByteBuffer request;
        volatile ByteBuffer response;

        Context(AsynchronousSocketChannel c) {
            channel = c;
        }

        void reset() {
            clientTime = 0;
            requestStartTime = 0;

            request = null;
            response = null;
        }
    }

    private abstract class BaseHandler<V> implements CompletionHandler<V, Object> {

        @Override
        public void failed(Throwable exc, Object attachment) {
            if (serverChannel == null && exc instanceof AsynchronousCloseException) {
                // server stopped -> do nothing
                return;
            }

            log.error("async operation failed: " + exc.getMessage());
            System.err.print("async op failed: ");
            exc.printStackTrace();
        }
    }

    private class AsyncAcceptHandler extends BaseHandler<AsynchronousSocketChannel> {

        @Override
        public void completed(AsynchronousSocketChannel channel, Object attachment) {
            serverChannel.accept(null, this);
            try {
                log.info("accepted client from " + channel.getRemoteAddress().toString());

                final Context context = new Context(channel);
                context.requestStartTime = System.nanoTime();
                channel.read(context.sizeBuffer, null, new AsyncReadSizeHandler(context));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class AsyncReadSizeHandler extends BaseHandler<Integer> {

        private final Context context;

        AsyncReadSizeHandler(Context c) {
            context = c;
        }

        @Override
        public void completed(Integer result, Object attachment) {
            if (context.sizeBuffer.hasRemaining()) {
                if (result != -1) {
                    context.channel.read(context.sizeBuffer, null, this);
                }
                return;
            }

            context.sizeBuffer.flip();
            int size = context.sizeBuffer.getInt();
            context.request = ByteBuffer.allocate(size);
            context.channel.read(context.request, null, new AsyncReadRequestHandler(context));
        }
    }

    private class AsyncReadRequestHandler extends BaseHandler<Integer> {

        private final Context context;

        AsyncReadRequestHandler(Context c) {
            context = c;
        }

        @Override
        public void completed(Integer result, Object attachment) {
            if (context.request.hasRemaining()) {
                if (result != -1) {
                    context.channel.read(context.request, null, this);
                }
                return;
            }

            context.request.flip();
            byte[] bytes = context.request.array();

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

                context.channel.write(context.response, null, new AsyncWriteResponseHandler(context));
            } catch (InvalidProtocolBufferException e) {
                log.error("failed to parse proto message: " + e.getMessage());
            }
        }
    }

    private class AsyncWriteResponseHandler extends BaseHandler<Integer> {

        private final Context context;

        AsyncWriteResponseHandler(Context c) {
            context = c;
        }

        @Override
        public void completed(Integer result, Object attachment) {
            if (context.response.hasRemaining()) {
                context.channel.write(context.response, null, this);
                return;
            }

            long requestTime = System.nanoTime() - context.requestStartTime;
            ServerStatistics.getInstance().push(context.clientTime, requestTime);
            context.reset();

            context.channel.read(context.sizeBuffer, null,
                    new AsyncReadSizeHandler(new Context(context.channel)));
        }
    }
}
