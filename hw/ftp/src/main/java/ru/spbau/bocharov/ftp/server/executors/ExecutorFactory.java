package ru.spbau.bocharov.ftp.server.executors;

import ru.spbau.bocharov.ftp.protocol.MessageType;

import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ExecutorFactory {

    private static final ExecutorFactory INSTANCE = new ExecutorFactory();

    public static ExecutorFactory getInstance() {
        return INSTANCE;
    }

    public RequestExecutor createExecutor(byte type, Socket socket, String baseDir) throws ExecutorFactoryException {
        if (!registry.containsKey(type)) {
            throw new UnknownExecutorException("unknown executor of type: " + type);
        }

        String name = registry.get(type).getName();
        String error;
        try {
            return (RequestExecutor) registry.get(type)
                    .getConstructor(Socket.class, String.class)
                    .newInstance(socket, baseDir);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            error = String.format("%s doesn't have public constructor from Socket and String", name);
        } catch (InstantiationException e) {
            error = String.format("%s cannot be instantiated: %s", name, e.getMessage());
        } catch (InvocationTargetException e) {
            error = String.format("exception during %s instantiation: %s", name, e.getMessage());
        }
        throw new BadExecutorException(error);
    }

    private final Map<Byte, Class> registry = new HashMap<>();

    private ExecutorFactory() {
        registry.put(MessageType.LIST, ListRequestExecutor.class);
        registry.put(MessageType.GET, GetRequestExecutor.class);
    }

}
