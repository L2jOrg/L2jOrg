/*
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.authserver.controller;

import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.authserver.data.database.ServerInfo;
import org.l2j.authserver.data.database.dao.GameserverDAO;
import org.l2j.authserver.data.xml.ServerNameReader;
import org.l2j.authserver.network.GameServerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * @author KenM
 */
public class GameServerManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameServerManager.class);

    private final IntMap<GameServerInfo> gameservers = new CHashIntMap<>();

    private IntMap<String> serverNames = new HashIntMap<>();

    private GameServerManager() {
        load();
    }

    public void load()  {
        loadServerNames();
        loadRegisteredGameServers();
    }

    private void loadServerNames() {
        try {
            var serverNameReader = new ServerNameReader();
            serverNames = serverNameReader.getServerNames();
            serverNameReader.cleanUp();
            LOGGER.info("Loaded {} server names", serverNames.size());
        } catch (Exception e) {
            LOGGER.warn("servername.xml could not be loaded.", e);
        }
    }

    private void loadRegisteredGameServers() {
        for (ServerInfo serverInfo : getDAO(GameserverDAO.class).findAll()) {
            GameServerInfo gsi = new GameServerInfo(serverInfo);
            gameservers.put(serverInfo.getId(), gsi);
        }
        LOGGER.info("Loaded {} registered Game Servers", gameservers.size());
    }

    public IntMap<GameServerInfo> getRegisteredGameServers() {
        return gameservers;
    }

    public GameServerInfo getRegisteredGameServerById(int id) {
        return gameservers.get(id);
    }

    public boolean registerWithFirstAvaliableId(GameServerInfo gsi) {
        // avoid two servers registering with the same "free" id
        synchronized (gameservers) {
            for (var entry : serverNames.entrySet()) {
                if (!gameservers.containsKey(entry.getKey())) {
                    gameservers.put(entry.getKey(), gsi);
                    gsi.setId(entry.getKey());
                    return true;
                }
            }
        }
        return false;
    }

    public void registerServerOnDB(GameServerInfo gsi) {
        registerServerOnDB(gsi.getId(), gsi.getServerHost(), gsi.getServerType());
    }

    public void registerServerOnDB(int id, String externalHost, int serverType) {
        getDAO(GameserverDAO.class).save(id, externalHost, serverType);
    }

    public String getServerNameById(int id) {
        return serverNames.get(id);
    }

    public static GameServerManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final GameServerManager INSTANCE = new GameServerManager();
    }

}