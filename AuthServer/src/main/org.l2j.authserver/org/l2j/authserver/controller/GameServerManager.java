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
import org.l2j.authserver.data.database.ServerData;
import org.l2j.authserver.data.database.dao.GameserverDAO;
import org.l2j.authserver.data.xml.ServerNameReader;
import org.l2j.authserver.network.ClusterServerInfo;
import org.l2j.authserver.network.ServerInfo;
import org.l2j.authserver.network.SingleServerInfo;
import org.l2j.authserver.network.gameserver.ServerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * @author KenM
 * @author JoeAlisson
 */
public class GameServerManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameServerManager.class);

    private final IntMap<ServerInfo> gameservers = new CHashIntMap<>();

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
        for (ServerData serverData : getDAO(GameserverDAO.class).findAll()) {
            SingleServerInfo gsi = new SingleServerInfo(serverData);
            gameservers.put(serverData.getId(), gsi);
        }
        LOGGER.info("Loaded {} registered Game Servers", gameservers.size());
    }

    public IntMap<ServerInfo> getRegisteredGameServers() {
        return gameservers;
    }

    public ServerInfo getRegisteredGameServerById(int id) {
        return gameservers.get(id);
    }

    public SingleServerInfo register(String key, int id, ServerClient client, int type) {
        var gsi = new SingleServerInfo(key, id, client, type);
        gameservers.put(id, gsi);
        getDAO(GameserverDAO.class).save(id, key, type);
        return gsi;
    }

    public SingleServerInfo registerInCluster(ServerInfo authedServer, ServerClient client) {
        var gsi = new SingleServerInfo(authedServer.key(), authedServer.id(), client, authedServer.type());
        gameservers.put(gsi.id(), ClusterServerInfo.of(authedServer, gsi));
        return gsi;
    }

    public String getServerNameById(int id) {
        return serverNames.get(id);
    }

    public void onDisconnection(SingleServerInfo info, String hostAddress) {
        var serverInfo =  gameservers.get(info.id());
        var name = serverNames.get(info.id());
        if(serverInfo instanceof ClusterServerInfo cluster) {
            info.setDown();
            cluster.out(info);
            LOGGER.info("Server host {} was disconnected from Cluster {}", hostAddress, name);
            if(!cluster.isAuthed()) {
                LOGGER.info("Server [{}] {} is now set as disconnected", info.id(), name);
            }
        } else {
            if (info.isAuthed()) {
                info.setDown();
                LOGGER.info("Server [{}] {} is now set as disconnected", info.id(), name);
            }
        }
    }

    public static GameServerManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final GameServerManager INSTANCE = new GameServerManager();
    }

}