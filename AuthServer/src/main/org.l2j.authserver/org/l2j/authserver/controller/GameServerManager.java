package org.l2j.authserver.controller;

import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.CHashIntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;
import org.l2j.authserver.data.database.dao.GameserverDAO;
import org.l2j.authserver.data.xml.ServerNameReader;
import org.l2j.authserver.network.GameServerInfo;
import org.l2j.commons.util.Rnd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
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

    private static IntObjectMap<String> serverNames = new HashIntObjectMap<>();

    private final IntObjectMap<GameServerInfo> gameservers = new CHashIntObjectMap<>();
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
            LOGGER.error(e.getLocalizedMessage(), e);
        }
    }

    private void loadServerNames() {
        try {
            var serverNameReader = new ServerNameReader();
            var f = new File("servername.xml");
            serverNameReader.read(f);
            serverNames = serverNameReader.getServerNames();
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

    public IntObjectMap<GameServerInfo> getRegisteredGameServers() {
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