package org.l2j.authserver;

import io.github.joealisson.mmocore.ConnectionBuilder;
import io.github.joealisson.mmocore.ConnectionHandler;
import org.l2j.authserver.controller.AuthController;
import org.l2j.authserver.controller.GameServerManager;
import org.l2j.authserver.network.SelectorHelper;
import org.l2j.authserver.network.client.AuthClient;
import org.l2j.authserver.network.client.AuthPacketHandler;
import org.l2j.authserver.network.gameserver.GameServerPacketHandler;
import org.l2j.authserver.network.gameserver.ServerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

import static java.lang.Runtime.getRuntime;
import static org.l2j.authserver.settings.AuthServerSettings.*;
import static org.l2j.commons.util.Util.isNullOrEmpty;

public class AuthServer {

    private static final String LOG4J_CONFIGURATION = "log4j.configurationFile";
    private static final String HIKARICP_CONFIGURATION_FILE = "hikaricp.configurationFile";

    public static final int PROTOCOL_REV = 0x0102;

    private static AuthServer _instance;
    private static Logger logger;

    private final ConnectionHandler<AuthClient> connectionHandler;
    private final ConnectionHandler<ServerClient>  serverConnectionHandler;

    public AuthServer() throws Exception {
        AuthController.getInstance();
        GameServerManager.getInstance();

        var bindServerListen = gameServerListenHost().equals("*") ? new InetSocketAddress(gameServerListenPort()) : new InetSocketAddress(gameServerListenHost(), gameServerListenPort());
        var gameserverHandler = new GameServerPacketHandler();
        serverConnectionHandler = ConnectionBuilder.create(bindServerListen, ServerClient::new, gameserverHandler, gameserverHandler).threadPoolSize(2).build();
        serverConnectionHandler.start();
        logger.info("Listening for GameServers on {} : {}", gameServerListenHost(), gameServerListenPort());


        var bindAddress = listenHost().equals("*") ? new InetSocketAddress(listenPort()) : new InetSocketAddress(listenHost(), listenPort()) ;
        final AuthPacketHandler lph = new AuthPacketHandler();
        final SelectorHelper sh = new SelectorHelper();
        connectionHandler = ConnectionBuilder.create(bindAddress, AuthClient::new, lph, sh).threadPoolSize(4).build();
        connectionHandler.start();
        logger.info("Login Server ready on {}:{}", bindAddress.getHostString(), listenPort());
    }

    private void shutdown() {
        serverConnectionHandler.shutdown();
        connectionHandler.shutdown();
    }

    public static void main(String[] args) {
        configureLogger();
        configureDatabase();
        try {
            _instance = new AuthServer();
            getRuntime().addShutdownHook(new Thread(() -> _instance.shutdown()));
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    private static void configureDatabase() {
        System.setProperty(HIKARICP_CONFIGURATION_FILE, "config/database.properties");
    }

    private static void configureLogger() {
        var logConfigurationFile = System.getProperty(LOG4J_CONFIGURATION);
        if (isNullOrEmpty(logConfigurationFile)) {
            System.setProperty(LOG4J_CONFIGURATION, "log4j.xml");
        }
        logger = LoggerFactory.getLogger(AuthServer.class);
    }
}
