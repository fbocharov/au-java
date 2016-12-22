package ru.spbau.bocharov.serverbench.client.ui;

import lombok.Getter;
import ru.spbau.bocharov.serverbench.common.ServerType;

public class ServerDescriptor {

    @Getter
    private final ServerType type;
    @Getter
    private final String description;

    public ServerDescriptor(ServerType t, String descr) {
        type = t;
        description = descr;
    }

    @Override
    public String toString() {
        return description;
    }
}
