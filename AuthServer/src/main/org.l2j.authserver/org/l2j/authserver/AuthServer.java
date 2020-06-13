/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.authserver;

import io.github.joealisson.mmocore.ConnectionBuilder;
import io.github.joealisson.mmocore.ConnectionHandler;
import org.l2j.authserver.controller.AuthController;
import org.l2j.authserver.controller.GameServerManager;
import org.l2j.authserver.network.ConnectionHelper;
import org.l2j.authserver.network.client.AuthClient;
import org.l2j.authserver.network.client.AuthPacketHandler;
import org.l2j.authserver.network.gameserver.GameServerPacketHandler;
import org.l2j.authserver.network.gameserver.ServerClient;
import org.l2j.commons.cache.CacheFactory;
import org.l2j.commons.threading.ThreadPool;
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
        serverConnectionHandler = ConnectionBuilder.create(bindServerListen, ServerClient::new, gameserverHandler, gameserverHandler).build();
        serverConnectionHandler.start();
        logger.info("Listening for GameServers on {}", bindServerListen);


        var bindAddress = listenHost().equals("*") ? new InetSocketAddress(listenPort()) : new InetSocketAddress(listenHost(), listenPort()) ;
        final AuthPacketHandler lph = new AuthPacketHandler();
        final ConnectionHelper sh = new ConnectionHelper();
        connectionHandler = ConnectionBuilder.create(bindAddress, AuthClient::new, lph, sh).build();
        connectionHandler.start();
        logger.info("Login Server ready on {}", bindAddress);
    }

    private void shutdown() {
        serverConnectionHandler.shutdown();
        connectionHandler.shutdown();
    }

    public static void main(String[] args) {
        configureLogger();
        configureCaches();
        configureDatabase();
        configureNetworkPackets();
        var processors = Runtime.getRuntime().availableProcessors();
        ThreadPool.init(processors, Math.max(1, processors/2));
        try {
            _instance = new AuthServer();
            getRuntime().addShutdownHook(new Thread(() -> _instance.shutdown()));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static void configureCaches() {
        CacheFactory.getInstance().initialize("config/ehcache.xml");
    }

    private static void configureNetworkPackets() {
        System.setProperty("async-mmocore.configurationFile", "config/async-mmocore.properties");
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
