package org.l2j.mmocore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.CompletionHandler;

class WriteHandler<T extends Client<Connection<T>>> implements CompletionHandler<Integer, T> {

    private  static final Logger logger = LoggerFactory.getLogger(WriteHandler.class);

    @Override
    public void completed(Integer result, T client) {
        if(result < 0) {
            logger.warn("Couldn't send data to client {}", client);
            client.disconnect();
            return;
        }

        Connection connection = client.getConnection();

        if(result < client.getDataSentSize()) {
            logger.debug("Still data to send. Trying to send");
            client.resumeSend(result);
        } else {
            connection.releaseWritingBuffer();
            client.tryWriteNextPacket();
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
