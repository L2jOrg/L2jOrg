package org.l2j.mmocore;

import java.net.SocketAddress;
import java.nio.ByteOrder;

import static java.lang.Math.max;
import static java.lang.Runtime.getRuntime;

class ConnectionConfig<T extends Client<Connection<T>>> {

    static final int BUFFER_MAX_SIZE = 64 * 1024;
    int bufferDefaultSize = 9 * 1024;
    int bufferLargeSize = 4 * 1024;
    int bufferMediumSize =  1024;
    int bufferMinSize = 64;
    int bufferPoolSize = 100;
    int bufferMinPoolSize = 100;
    int bufferMediumPoolSize = 50;
    int bufferLargePoolSize = 10;
    long shutdownWaitTime = 5000;
    ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;
    boolean useNagle;

    ClientFactory<T> clientFactory;
    ConnectionFilter acceptFilter;
    ReadHandler<T> readHandler;
    WriteHandler<T> writeHandler;
    int threadPoolSize;
    SocketAddress address;

    ConnectionConfig(SocketAddress address, ClientFactory<T> factory, ReadHandler<T> readHandler) {
        this.address = address;
        this.clientFactory = factory;
        this.readHandler = readHandler;
        this.writeHandler = new WriteHandler<>();
        threadPoolSize = max(1, getRuntime().availableProcessors() -2);
    }
}
