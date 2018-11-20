package org.l2j.mmocore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.CompletionHandler;

import static java.util.Objects.nonNull;

class ReadHandler<T extends Client<Connection<T>>> implements CompletionHandler<Integer, T> {

    private static final Logger logger = LoggerFactory.getLogger(ReadHandler.class);
    static final int HEADER_SIZE = 2;

    private final PacketHandler<T> packetHandler;
    private final PacketExecutor<T> executor;

    ReadHandler(PacketHandler<T> packetHandler, PacketExecutor<T> executor) {
        this.packetHandler = packetHandler;
        this.executor =  executor;
    }

    @Override
    public void completed(Integer bytesRead, T client) {
        if(!client.isConnected()) {
            return;
        }

        logger.debug("Reading {} from {}", bytesRead, client);
        if(bytesRead < 0 ) {
            client.disconnect();
            return;
        }

        Connection<T> connection = client.getConnection();
        var buffer = connection.getReadingBuffer();
        buffer.flip();

        if (buffer.remaining() < HEADER_SIZE){
            logger.debug("Not enough data to read packet header");
            buffer.compact();
            connection.read();
            return;
        }

        int dataSize = Short.toUnsignedInt(buffer.getShort()) - HEADER_SIZE;

        if(dataSize > buffer.remaining()) {
            logger.debug("Not enough data to read. Packet size {}", dataSize);
            buffer.position(buffer.position() - HEADER_SIZE);
            buffer.compact();
            connection.read();
            return;
        }

        try {
            if (dataSize > 0) {
                parseAndExecutePacket(client, buffer, dataSize);
            }
        }catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            buffer.clear();
        } finally {
            if(!buffer.hasRemaining()) {
                buffer.clear();
            } else {
                logger.debug("Still data on packet. Trying to read");
                int remaining = buffer.remaining();
                buffer.compact();
                if(remaining >= HEADER_SIZE) {
                    completed(remaining, client);
                    return;
                }
            }
            connection.read();
        }
    }

    private void parseAndExecutePacket(T client, ByteBuffer buffer, int dataSize) {
        logger.debug("Trying to parse data");
        byte[] data = new byte[dataSize];

        buffer.get(data, 0, dataSize);
        boolean decrypted = client.decrypt(data, 0, dataSize);
        if(decrypted) {
            DataWrapper wrapper = DataWrapper.wrap(data);
            ReadablePacket<T> packet = packetHandler.handlePacket(wrapper, client);
            logger.debug("Parsed data to packet {}", packet);
            execute(client, packet, wrapper);
        }
    }

    private void execute(T client, ReadablePacket<T> packet, DataWrapper wrapper) {
        if(nonNull(packet)) {
            packet.client = client;
            packet.data = wrapper.data;
            packet.dataIndex = wrapper.dataIndex;
            if(packet.read()) {
                logger.debug("packet {} was read from client {}", packet, client);
                executor.execute(packet);
            }
        }
     }

    @Override
    public void failed(Throwable e, T client) {
        if(client.isConnected()) {
            client.disconnect();
        }
        if(! (e instanceof AsynchronousCloseException)) {
            // client just closes the connection, doesn't to be logged
            logger.error(e.getLocalizedMessage(), e);
        }
    }
}