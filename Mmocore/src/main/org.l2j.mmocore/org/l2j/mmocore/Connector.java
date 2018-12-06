package org.l2j.mmocore;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteOrder;
import java.nio.channels.AsynchronousSocketChannel;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Objects.isNull;

public class Connector<T extends Client<Connection<T>>>  {

    private ConnectionConfig<T> config;

    public static <T extends Client<Connection<T>>> Connector<T> create(ClientFactory<T> clientFactory, PacketHandler<T> packetHandler, PacketExecutor<T> executor)  {
        Connector<T> builder = new Connector<>();
        builder.config = new ConnectionConfig<>(null, clientFactory, new ReadHandler<>(packetHandler, executor));
        return builder;
    }

    public Connector<T> bufferDefaultSize(int bufferSize) {
        config.bufferDefaultSize = min(max(bufferSize, config.bufferMinSize), ConnectionConfig.BUFFER_MAX_SIZE);
        return this;
    }

    public Connector<T> bufferMinSize(int bufferSize) {
        config.bufferMinSize = min(max(config.bufferMinSize, bufferSize), ConnectionConfig.BUFFER_MAX_SIZE);
        return this;
    }

    public Connector<T> bufferMediumSize(int bufferSize) {
        config.bufferMediumSize = min(max(bufferSize, config.bufferMinSize), ConnectionConfig.BUFFER_MAX_SIZE);
        return this;
    }

    public Connector<T> bufferLargeSize(int bufferSize) {
        config.bufferLargeSize = min(max(bufferSize, config.bufferMinSize), ConnectionConfig.BUFFER_MAX_SIZE);
        return this;
    }

    public Connector<T> bufferPoolSize(int bufferPoolSize) {
        config.bufferPoolSize = bufferPoolSize;
        return this;
    }

    public Connector<T> bufferMinPoolSize(int bufferPoolSize) {
        config.bufferMinPoolSize = bufferPoolSize;
        return this;
    }

    public Connector<T> bufferMediumPoolSize(int bufferPoolSize) {
        config.bufferMediumPoolSize = bufferPoolSize;
        return this;
    }

    public Connector<T> bufferLargePoolSize(int bufferPoolSize) {
        config.bufferLargePoolSize = bufferPoolSize;
        return this;
    }

    public Connector<T> byteOrder(ByteOrder order) {
        config.byteOrder = order;
        return this;
    }

    public T connect(String host, int port) throws IOException {
        InetSocketAddress socketAddress;
        if(isNull(host) || host.isEmpty()) {
            socketAddress = new InetSocketAddress(port);
        } else {
            socketAddress = new InetSocketAddress(host, port);
        }
        return connect(socketAddress);
    }

    public T connect(InetSocketAddress socketAddress) throws IOException {
        AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
        channel.connect(socketAddress);
        Connection<T> connection = new Connection<T>(channel, config.readHandler, new WriteHandler<>());
        T client = config.clientFactory.create(connection);
        client.setResourcePool(ResourcePool.initialize(config));
        connection.setClient(client);
        connection.read();
        client.onConnected();
        return client;
    }

}
