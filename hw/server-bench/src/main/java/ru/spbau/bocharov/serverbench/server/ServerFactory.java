package ru.spbau.bocharov.serverbench.server;

import ru.spbau.bocharov.serverbench.common.ServerType;
import ru.spbau.bocharov.serverbench.server.impl.tcp.*;
import ru.spbau.bocharov.serverbench.server.impl.udp.FixedSizeThreadPoolUDPServer;
import ru.spbau.bocharov.serverbench.server.impl.udp.ThreadPerClientUDPServer;
import ru.spbau.bocharov.serverbench.util.FactoryException;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ServerFactory {

    private static final ServerFactory INSTANCE = new ServerFactory();

    public static ServerFactory getInstance() {
        return INSTANCE;
    }

    public BaseServer create(ServerType type, int port) throws FactoryException {
        if (!registry.containsKey(type)) {
            throw new FactoryException(String.format("unknown creator with key %s", type));
        }

        String error;
        Class cls = registry.get(type);
        try {
            return (BaseServer) cls
                    .getConstructor(Integer.class)
                    .newInstance(port);
        } catch (InstantiationException e) {
            error = String.format("failed to instantiate %s: %s", cls.getName(), e.getMessage());
        } catch (IllegalAccessException e) {
            error = String.format("%s doesn't have public constructor", cls.getName());
        } catch (NoSuchMethodException e) {
            error = String.format("%s doesn't have public constructor from String and int", cls.getName());
        } catch (InvocationTargetException e) {
            error = String.format("%s constructor thrown exception: %s", cls.getName(), e.getMessage());
        }
        throw new FactoryException(error);
    }


    private Map<ServerType, Class> registry = new HashMap<>();

    private ServerFactory() {
        // UDP
        registry.put(ServerType.SINGLE_THREAD_UDP_SERVER, ThreadPerClientUDPServer.class);
        registry.put(ServerType.THREAD_POOL_UDP_SERVER, FixedSizeThreadPoolUDPServer.class);

        // TCP
        registry.put(ServerType.SINGLE_THREAD_TCP_SERVER, SingleThreadTCPServer.class);
        registry.put(ServerType.THREAD_PER_CLIENT_TCP_SERVER, ThreadPerClientTCPServer.class);
        registry.put(ServerType.THREAD_CACHING_TCP_SERVER, ThreadCachingTCPServer.class);
        registry.put(ServerType.NONBLOCKING_TCP_SERVER, PollingTCPServer.class);
        registry.put(ServerType.ASYNC_TCP_SERVER, AsyncTCPServer.class);
    }
}
