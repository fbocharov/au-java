package ru.spbau.bocharov.torrent.util;

import java.util.HashMap;
import java.util.Map;

public class ObjectFactory<K, O> {

    private static final ObjectFactory INSTANCE = new ObjectFactory();

    public static ObjectFactory getInstance() {
        return INSTANCE;
    }

    public void register(K key, Class<? extends O> creator) throws BadCreatorException {
        if (registry.containsKey(key)) {
            throw new BadCreatorException(String.format("creator for key %s already exists", key.toString()));
        }

        registry.put(key, creator);
    }

    public O create(K key) throws BadCreatorException {
        if (!registry.containsKey(key)) {
            throw new BadCreatorException(String.format("unknown creator with key %s", key.toString()));
        }

        String error;
        Class cls = registry.get(key);
        try {
            return (O) cls.newInstance();
        } catch (InstantiationException e) {
            error = String.format("failed to instantiate %s: %s", cls.getName(), e.getMessage());
        } catch (IllegalAccessException e) {
            error = String.format("%s doesn't have public constructor without parameters", cls.getName());
        }
        throw new BadCreatorException(error);
    }


    private Map<K, Class> registry = new HashMap<>();

    private ObjectFactory() {}
}
