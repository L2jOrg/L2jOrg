package org.l2j.authserver.network;

import org.l2j.commons.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.authserver.settings.AuthServerSettings.*;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.nonNull;

/**
 * @author -Wooden-
 */
public abstract class FloodProtectedListener extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(FloodProtectedListener.class);
    private final Map<String, ForeignConnection> _floodProtection = new HashMap<>();
    private ServerSocket _serverSocket;

    public FloodProtectedListener(String listenIp, int port) throws IOException {
        if (listenIp.equals("*")) {
            _serverSocket = new ServerSocket(port);
        } else {
            _serverSocket = new ServerSocket(port, 50, InetAddress.getByName(listenIp));
        }
    }

    @Override
    public void run() {
        Socket connection = null;

        while (true) {
            try {

                connection = _serverSocket.accept();
                if (isFloodProtectionEnabled()) {
                    var fConnection = _floodProtection.get(connection.getInetAddress().getHostAddress());

                    if (nonNull(fConnection)) {
                        fConnection.connectionNumber++;
                        long currentTime = currentTimeMillis();
                        if (isPotentialFlood(fConnection, currentTime)) {

                            fConnection.lastConnection = currentTime;
                            connection.close();
                            fConnection.connectionNumber -= 1;
                            if (!fConnection.isFlooding) {
                                logger.warn("Potential Flood from {}", connection.getInetAddress().getHostAddress());
                            }
                            fConnection.isFlooding = true;
                            continue;
                        }

                        // if connection was flooding server but now passed the check
                        if (fConnection.isFlooding) {
                            fConnection.isFlooding = false;
                            logger.info("{} is not considered as flooding anymore.", connection.getInetAddress().getHostAddress() );
                        }
                        fConnection.lastConnection = currentTime;
                    } else {
                        fConnection = new ForeignConnection(currentTimeMillis());
                        _floodProtection.put(connection.getInetAddress().getHostAddress(), fConnection);
                    }
                }
                addClient(connection);
            } catch (Exception e) {
                try {
                    if (nonNull(connection)) {
                        connection.close();
                    }
                } catch (Exception ignored) {
                }
                if (isInterrupted()) {
                    // shutdown?
                    try {
                        _serverSocket.close();
                    } catch (IOException io) {
                        logger.warn(io.getLocalizedMessage(), io);
                    }
                    break;
                }
            }
        }
    }

    private boolean isPotentialFlood(ForeignConnection fConnection, long currentTime) {
        return ((fConnection.connectionNumber > floodFastConnectionLimit()) &&
                ((currentTime - fConnection.lastConnection) < floodNormalConnectionTime())) ||
                ((currentTime - fConnection.lastConnection) < floodFastConnectionTime()) ||
                (fConnection.connectionNumber > maxConnectionPerIP());
    }

    static class ForeignConnection {
        int connectionNumber;
        long lastConnection;
        boolean isFlooding = false;


        ForeignConnection(long time) {
            lastConnection = time;
            connectionNumber = 1;
        }
    }

    public abstract void addClient(Socket s);

    public void removeFloodProtection(String ip) {
        if (!Config.FLOOD_PROTECTION) {
            return;
        }
        ForeignConnection fConnection = _floodProtection.get(ip);
        if (fConnection != null) {
            fConnection.connectionNumber -= 1;
            if (fConnection.connectionNumber == 0) {
                _floodProtection.remove(ip);
            }
        } else {
            logger.warn("Removing a flood protection for a GameServer that was not in the connection map??? :" + ip);
        }
    }

    public void close() {
        try {
            _serverSocket.close();
        } catch (IOException e) {
            logger.warn(e.getLocalizedMessage(), e);
        }
    }
}