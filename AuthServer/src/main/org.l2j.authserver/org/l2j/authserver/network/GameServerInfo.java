package org.l2j.authserver.network;

import org.l2j.authserver.controller.GameServerManager;
import org.l2j.authserver.data.database.GameServer;
import org.l2j.authserver.network.gameserver.ServerClient;
import org.l2j.authserver.network.gameserver.packet.auth2game.KickPlayer;
import org.l2j.authserver.network.gameserver.packet.auth2game.RequestAccountInfo;
import org.l2j.authserver.network.gameserver.packet.game2auth.ServerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameServerInfo {

    private static final Logger logger = LoggerFactory.getLogger(GameServerInfo.class);
    private final Set<String> accounts = new HashSet<>();

    private int _id;
    private volatile boolean _isAuthed;
    private int _status;

    // network
    private String internalIp;
    private String externalIp;
    private String externalHost;
    private int _port;

    // config
    private boolean isPvp;
    private boolean _isTestServer;
    private boolean _isShowingClock;
    private boolean _isShowingBrackets;
    private int _maxPlayers;
    private int serverType;
    private ServerClient client;
    private byte ageLimit;


    public GameServerInfo(GameServer gameServer) {
        this(gameServer.getId(), null);
    }

    public GameServerInfo(int id,  ServerClient client) {
        _id = id;
       this.client = client;
        _status = ServerStatus.STATUS_DOWN;
    }

    public void setClient(ServerClient client) {
        this.client = client;
    }

    public void setHosts(String externalHost, String internalHost) {
        String oldInternal = getInternalHost();
        String oldExternal = getExternalHost();

        setExternalHost(externalHost);
        setInternalHost(internalHost);

        if (!externalHost.equals("*")) {
            try {
                setExternalIp(InetAddress.getByName(externalHost).getHostAddress());
            } catch (UnknownHostException e) {
                logger.warn("Couldn't resolve hostname {}", externalHost);
            }
        } else {
            setExternalIp(client.getHostAddress());
        }

        if (!internalHost.equals("*")) {
            try {
                setInternalHost(InetAddress.getByName(internalHost).getHostAddress());
            } catch (UnknownHostException e) {
                logger.warn("Couldn't resolve hostname {}", internalHost);
            }
        } else {
            setInternalHost(client.getHostAddress());
        }

        logger.info("Updated Gameserver [{}] {} IP's:", _id, GameServerManager.getInstance().getServerNameById(_id));
        if ((oldInternal == null) || !oldInternal.equalsIgnoreCase(internalHost)) {
            logger.info("InternalIP: {}", internalHost);
        }
        if ((oldExternal == null) || !oldExternal.equalsIgnoreCase(externalHost)) {
            logger.info("ExternalIP: {}", externalHost);
        }
    }

    public int getOnlinePlayersCount() {
        return accounts.size();
    }

    public void setDown() {
        setAuthed(false);
        setPort(0);
        setStatus(ServerStatus.STATUS_DOWN);
    }

    public void sendKickPlayer(String account) {
        removeAccount(account);
        client.sendPacket(new KickPlayer(account));
    }

    public void requestAccountInfo(String account) {
        client.sendPacket(new RequestAccountInfo(account));
    }

    public void removeAccount(String account) {
        accounts.remove(account);
    }

    public boolean accountIsConnected(String account) {
        return accounts.contains(account);
    }

    public void setId(int id) {
        _id = id;
    }

    public int getId() {
        return _id;
    }

    public void setAuthed(boolean isAuthed) {
        _isAuthed = isAuthed;
    }

    public boolean isAuthed() {
        return _isAuthed;
    }

    public void setStatus(int status) {
        _status = status;
    }

    public int getStatus() {
        return _status;
    }

    public void setInternalHost(String internalIp) {
        this.internalIp = internalIp;
    }

    public String getInternalHost() {
        return internalIp;
    }

    public void setExternalIp(String externalIp) {
        this.externalIp = externalIp;
    }

    public String getExternalIp() {
        return externalIp;
    }

    public void setExternalHost(String externalHost) {
        this.externalHost = externalHost;
    }

    public String getExternalHost() {
        return externalHost;
    }

    public int getPort() {
        return _port;
    }

    public void setPort(int port) {
        _port = port;
    }

    public void setMaxPlayers(int maxPlayers) {
        _maxPlayers = maxPlayers;
    }

    public int getMaxPlayers() {
        return _maxPlayers;
    }

    public boolean isPvp() {
        return isPvp;
    }

    public void setTestServer(boolean val) {
        _isTestServer = val;
    }

    public boolean isTestServer() {
        return _isTestServer;
    }

    public void setShowingClock(boolean clock) {
        _isShowingClock = clock;
    }

    public boolean isShowingClock() {
        return _isShowingClock;
    }

    public void setShowingBrackets(boolean val) {
        _isShowingBrackets = val;
    }

    public boolean isShowingBrackets() {
        return _isShowingBrackets;
    }

    public void setServerType(int serverType) {
        this.serverType = serverType;
    }

    public int getServerType() {
        return serverType;
    }


    public void addAccounts(List<String> accounts) {
        this.accounts.addAll(accounts);
    }

    public void setAgeLimit(byte ageLimit) {
        this.ageLimit = ageLimit;
    }

    public void setIsPvp(boolean isPvp) {
        this.isPvp = isPvp;
    }

    public byte getAgeLimit() {
        return ageLimit;
    }
}