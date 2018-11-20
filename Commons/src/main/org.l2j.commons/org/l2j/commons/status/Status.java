package org.l2j.commons.status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static java.lang.System.currentTimeMillis;

public abstract class Status extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(Status.class);

    private final ServerSocket statusServerSocket;

    protected final long _uptime;
    protected final int _statusPort;
    protected String _statusPw;

    @Override
    public void run() {
        while (true) {
            try {
                Socket connection = statusServerSocket.accept();

                startStatusThread(connection);

                if (isInterrupted()) {
                    try {
                        statusServerSocket.close();
                    } catch (IOException io) {
                        logger.error(io.getLocalizedMessage(),io);
                    }
                    break;
                }
            } catch (IOException e) {
                if (isInterrupted()) {
                    try {
                        statusServerSocket.close();
                    } catch (IOException io) {
                        logger.error(io.getLocalizedMessage(),io);
                    }
                    break;
                }
            }
        }
    }

    protected abstract void startStatusThread(Socket connection) throws IOException;

    public Status(int port, String pwd) throws IOException {
        super("Status");

        _statusPort = port;
        _statusPw = pwd;

        logger.info("StatusServer Started! - Listening on Port: {}", _statusPort);
        logger.debug("Password Has Been Set To: ", _statusPw);

        statusServerSocket = new ServerSocket(_statusPort);
        _uptime = currentTimeMillis();
    }

    public abstract void sendMessageToTelnets(String msg);

}
