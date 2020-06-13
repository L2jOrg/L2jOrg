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
package org.l2j.authserver.controller;

import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.authserver.data.database.dao.GameserverDAO;
import org.l2j.authserver.data.xml.ServerNameReader;
import org.l2j.authserver.network.GameServerInfo;
import org.l2j.commons.util.Rnd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.RSAKeyGenParameterSpec;

import static java.util.Objects.isNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * @author KenM
 */
public class GameServerManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameServerManager.class);
    private static final int KEYS_SIZE = 10;

    private static IntMap<String> serverNames = new HashIntMap<>();

    private final IntMap<GameServerInfo> gameservers = new CHashIntMap<>();
    private KeyPair[] _keyPairs;

    private GameServerManager() {
        load();
    }

    public void load()  {
        loadServerNames();
        loadRegisteredGameServers();

        try {
            loadRSAKeys();
        }catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
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
        getDAO(GameserverDAO.class).findAll().forEach(gameServer -> {
            GameServerInfo gsi = new GameServerInfo(gameServer);
            gameservers.put(gameServer.getId(), gsi);
        });
        LOGGER.info("Loaded {} registered Game Servers", gameservers.size());
    }

    private void loadRSAKeys() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(512, RSAKeyGenParameterSpec.F4);
        keyGen.initialize(spec);

        _keyPairs = new KeyPair[KEYS_SIZE];
        for (int i = 0; i < KEYS_SIZE; i++) {
            _keyPairs[i] = keyGen.genKeyPair();
        }
        LOGGER.info("Cached {} RSA keys for Game Server communication.", _keyPairs.length);
    }

    public IntMap<GameServerInfo> getRegisteredGameServers() {
        return gameservers;
    }

    public GameServerInfo getRegisteredGameServerById(int id) {
        return gameservers.get(id);
    }

    public boolean hasRegisteredGameServerOnId(int id) {
        return gameservers.containsKey(id);
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

    public boolean register(int id, GameServerInfo gsi) {
        if(isNull(gameservers.putIfAbsent(id, gsi))) {
            gsi.setId(id);
            return true;
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

    public KeyPair getKeyPair() {
        return _keyPairs[Rnd.get(10)];
    }

    private static class Singleton {
        private static final GameServerManager INSTANCE = new GameServerManager();
    }

}