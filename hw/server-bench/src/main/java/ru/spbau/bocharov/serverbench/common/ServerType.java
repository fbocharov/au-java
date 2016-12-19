package ru.spbau.bocharov.serverbench.common;

public enum ServerType {
    // UDP
    SINGLE_THREAD_UDP_SERVER,
    THREAD_POOL_UDP_SERVER,

    // TCP
    SINGLE_THREAD_TCP_SERVER,
    THREAD_PER_CLIENT_TCP_SERVER,
    THREAD_CACHING_TCP_SERVER,
    NONBLOCKING_TCP_SERVER,
    ASYNC_TCP_SERVER
}
