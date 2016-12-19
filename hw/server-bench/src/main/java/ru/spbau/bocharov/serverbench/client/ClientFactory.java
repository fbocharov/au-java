package ru.spbau.bocharov.serverbench.client;

import ru.spbau.bocharov.serverbench.client.impl.tcp.NewConnectionPerRequestTCPClient;
import ru.spbau.bocharov.serverbench.client.impl.tcp.PermanentConnectionTCPClient;
import ru.spbau.bocharov.serverbench.client.impl.udp.UDPClient;
import ru.spbau.bocharov.serverbench.common.ServerType;
import ru.spbau.bocharov.serverbench.util.FactoryException;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ClientFactory {

    private static final ClientFactory INSTANCE = new ClientFactory();

    public static ClientFactory getInstance() {
        return INSTANCE;
    }

    public BaseClient create(ServerType type, String serverAddress, int serverPort) throws FactoryException {
        if (!registry.containsKey(type)) {
            throw new FactoryException(String.format("unknown creator with key %s", type));
        }

        String error;
        Class cls = registry.get(type);
        try {
            return (BaseClient) cls
                    .getConstructor(String.class, Integer.class)
                    .newInstance(serverAddress, serverPort);
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

    private ClientFactory() {
        // UDP
        registry.put(ServerType.SINGLE_THREAD_UDP_SERVER, UDPClient.class);
        registry.put(ServerType.THREAD_POOL_UDP_SERVER, UDPClient.class);

        // TCP
        registry.put(ServerType.SINGLE_THREAD_TCP_SERVER, NewConnectionPerRequestTCPClient.class);
        registry.put(ServerType.THREAD_PER_CLIENT_TCP_SERVER, PermanentConnectionTCPClient.class);
        registry.put(ServerType.THREAD_CACHING_TCP_SERVER, PermanentConnectionTCPClient.class);
        registry.put(ServerType.NONBLOCKING_TCP_SERVER, PermanentConnectionTCPClient.class);
        registry.put(ServerType.ASYNC_TCP_SERVER, PermanentConnectionTCPClient.class);
    }

}
