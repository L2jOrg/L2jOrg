package org.l2j.mmocore;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteOrder;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class ConnectionBuilder<T extends Client<Connection<T>>> {

    private ConnectionConfig<T> config;

    public static <T extends Client<Connection<T>>> ConnectionBuilder<T> create(InetSocketAddress address, ClientFactory<T> clientFactory, PacketHandler<T> packetHandler, PacketExecutor<T> executor)  {
        ConnectionBuilder<T> builder = new ConnectionBuilder<>();
        builder.config = new ConnectionConfig<>(address, clientFactory, new ReadHandler<>(packetHandler, executor));
        return builder;
    }

    public ConnectionBuilder<T> filter(ConnectionFilter filter) {
        config.acceptFilter = filter;
        return this;
    }

    public ConnectionBuilder<T> threadPoolSize(int size) {
        config.threadPoolSize = size;
        return this;
    }

    public ConnectionBuilder<T> useNagle(boolean useNagle) {
        config.useNagle = useNagle;
        return  this;
    }

    public ConnectionBuilder<T> shutdownWaitTime(long waitTime) {
        config.shutdownWaitTime = waitTime;
        return this;
    }
    
    public ConnectionBuilder<T> bufferDefaultSize(int bufferSize) {
        config.bufferDefaultSize = min(max(bufferSize, config.bufferMinSize), ConnectionConfig.BUFFER_MAX_SIZE);
        return this;
    }

    public ConnectionBuilder<T> bufferMinSize(int bufferSize) {
        config.bufferMinSize = min(max(config.bufferMinSize, bufferSize), ConnectionConfig.BUFFER_MAX_SIZE);
        return this;
    }

    public ConnectionBuilder<T> bufferMediumSize(int bufferSize) {
        config.bufferMediumSize = min(max(bufferSize, config.bufferMinSize), ConnectionConfig.BUFFER_MAX_SIZE);
        return this;
    }

    public ConnectionBuilder<T> bufferLargeSize(int bufferSize) {
        config.bufferLargeSize = min(max(bufferSize, config.bufferMinSize), ConnectionConfig.BUFFER_MAX_SIZE);
        return this;
    }
    
    public ConnectionBuilder<T> bufferPoolSize(int bufferPoolSize) {
        config.bufferPoolSize = bufferPoolSize;
        return this;
    }

    public ConnectionBuilder<T> bufferMinPoolSize(int bufferPoolSize) {
        config.bufferMinPoolSize = bufferPoolSize;
        return this;
    }

    public ConnectionBuilder<T> bufferMediumPoolSize(int bufferPoolSize) {
        config.bufferMediumPoolSize = bufferPoolSize;
        return  this;
    }

    public ConnectionBuilder<T> bufferLargePoolSize(int bufferPoolSize) {
        config.bufferLargePoolSize = bufferPoolSize;
        return  this;
    }

    public ConnectionBuilder<T> byteOrder(ByteOrder order) {
        config.byteOrder = order;
        return  this;
    }

    public ConnectionHandler<T> build() throws IOException {
        return new ConnectionHandler<>(config);
    }
}