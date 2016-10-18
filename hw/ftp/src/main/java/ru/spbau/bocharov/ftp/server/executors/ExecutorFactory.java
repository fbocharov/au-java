package ru.spbau.bocharov.ftp.server.executors;

import ru.spbau.bocharov.ftp.protocol.MessageType;

import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ExecutorFactory {

    private static final ExecutorFactory instance = new ExecutorFactory();

    public static ExecutorFactory getInstance() {
        return instance;
    }

    public RequestExecutor createExecutor(byte type, Socket socket) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, InstantiationException, UnknownExecutorException {
        if (!registry.containsKey(type)) {
            throw new UnknownExecutorException("unknown executor of type: " + type);
        }

        return (RequestExecutor) registry.get(type).getConstructor(Socket.class).newInstance(socket);
    }

    private final Map<Byte, Class> registry = new HashMap<>();
    {
        registry.put(MessageType.LIST, ListRequestExecutor.class);
        registry.put(MessageType.GET, GetRequestExecutor.class);
    }


    private ExecutorFactory() {}
}
