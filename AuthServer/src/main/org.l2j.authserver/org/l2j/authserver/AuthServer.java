package org.l2j.authserver;

import org.l2j.authserver.controller.AuthController;
import org.l2j.authserver.controller.GameServerManager;
import org.l2j.authserver.network.SelectorHelper;
import org.l2j.authserver.network.client.AuthClient;
import org.l2j.authserver.network.client.AuthPacketHandler;
import org.l2j.authserver.network.gameserver.GameServerPacketHandler;
import org.l2j.authserver.network.gameserver.ServerClient;
import org.l2j.commons.Config;
import org.l2j.commons.Server;
import org.l2j.mmocore.ConnectionBuilder;
import org.l2j.mmocore.ConnectionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

import static java.lang.Runtime.getRuntime;
import static org.l2j.authserver.settings.AuthServerSettings.*;
import static org.l2j.commons.util.Util.isNullOrEmpty;

public class AuthServer {

    private static final String LOG4J_CONFIGURATION = "log4j.configurationFile";
    public static final int PROTOCOL_REV = 0x0102;

    private static AuthServer _instance;
    private static Logger logger;

    private final ConnectionHandler<AuthClient> connectionHandler;
    private final ConnectionHandler<ServerClient>  serverConnectionHandler;

    public AuthServer() throws Exception {
        AuthController.load();
        GameServerManager.load();

        var bindServerListen = gameServerListenHost().equals("*") ? new InetSocketAddress(gameServerListenPort()) : new InetSocketAddress(gameServerListenHost(), gameServerListenPort());
        var gameserverHandler = new GameServerPacketHandler();
        serverConnectionHandler = ConnectionBuilder.create(bindServerListen, ServerClient::new, gameserverHandler, gameserverHandler).threadPoolSize(4).build();
        serverConnectionHandler.start();
        logger.info("Listening for GameServers on {} : {}", gameServerListenHost(), gameServerListenPort());


        var bindAddress = loginListenHost().equals("*") ? new InetSocketAddress(loginListenPort()) : new InetSocketAddress(loginListenHost(), loginListenPort()) ;
        final AuthPacketHandler lph = new AuthPacketHandler();
        final SelectorHelper sh = new SelectorHelper();
        connectionHandler = ConnectionBuilder.create(bindAddress, AuthClient::new, lph, sh).threadPoolSize(4).build();
        connectionHandler.start();
        logger.info("Login Server ready on {}:{}", bindAddress.getHostString(), loginListenPort());


    }

    private void shutdown(boolean restart) {
        serverConnectionHandler.shutdown();
        connectionHandler.shutdown();
        getRuntime().exit(restart ? 2 : 0);
    }

    public static void main(String[] args) {
        configureLogger();
        Server.serverMode = Server.MODE_LOGINSERVER;
        Config.load();
        try {
            _instance = new AuthServer();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        getRuntime().addShutdownHook(new Thread(() -> _instance.shutdown(false)));
    }

    private static void configureLogger() {
        var logConfigurationFile = System.getProperty(LOG4J_CONFIGURATION);
        if (isNullOrEmpty(logConfigurationFile)) {
            System.setProperty(LOG4J_CONFIGURATION, "log4j.xml");
        }
        logger = LoggerFactory.getLogger(AuthServer.class);
    }
}
