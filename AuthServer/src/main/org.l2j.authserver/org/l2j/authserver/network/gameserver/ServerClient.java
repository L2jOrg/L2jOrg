package org.l2j.authserver.network.gameserver;

import org.l2j.authserver.network.GameServerInfo;
import org.l2j.authserver.controller.GameServerManager;
import org.l2j.authserver.network.crypt.AuthServerCrypt;
import org.l2j.authserver.network.gameserver.packet.auth2game.GameServerWritablePacket;
import org.l2j.authserver.network.gameserver.packet.auth2game.LoginGameServerFail;
import io.github.joealisson.mmocore.Client;
import io.github.joealisson.mmocore.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import static java.util.Objects.nonNull;
import static org.l2j.authserver.network.gameserver.ServerClientState.AUTHED;
import static org.l2j.authserver.network.gameserver.ServerClientState.CONNECTED;

public final class ServerClient extends Client<Connection<ServerClient>> {

    private static Logger _log = LoggerFactory.getLogger(ServerClient.class);
    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;
    private AuthServerCrypt crypt;
    private ServerClientState state;
    private GameServerInfo gameServerInfo;

    public ServerClient(Connection<ServerClient> con) {
		super(con);
	}

    public RSAPublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getRSAPrivateKey() {
        return privateKey;
    }

    public void setCryptKey(byte[] key) {
        crypt.setKey(key);
    }

    public void setState(ServerClientState state) {
        this.state = state;
    }

    public ServerClientState getState()
    {
        return state;
    }

    public void close(int reason) {
        close(new LoginGameServerFail(reason));
    }

    public void setGameServerInfo(GameServerInfo gsi) {
        this.gameServerInfo = gsi;
    }

    public GameServerInfo getGameServerInfo() {
        return gameServerInfo;
    }


    @Override
    public boolean decrypt(byte[] data, int offset, int size) {
        /*boolean decrypted;
        try  {
             decrypted =  crypt.decrypt(data, offset, size);
            if(!decrypted) {
                disconnect();
            }
            return  decrypted;
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            disconnect();
            return false;
        }*/
        return true;
    }

    @Override
    public int encrypt(byte[] data, int offset, int size) {
        /*try {
            return  crypt.encrypt(data, offset, size);
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            return -1;
        }*/
        return size;
    }

	public void sendPacket(GameServerWritablePacket lsp) {
	    writePacket(lsp);
	}

    @Override
    public void onConnected() {
        setState(CONNECTED);
/*        var pair = GameServerManager.getInstance().getKeyPair();
        privateKey = (RSAPrivateKey) pair.getPrivate();
        publicKey = (RSAPublicKey) pair.getPublic();
        crypt =  new AuthServerCrypt();
        sendPacket(new Protocol());*/
    }

	@Override
	protected void onDisconnection() {

        String serverName = getHostAddress();

        var serverId = nonNull(gameServerInfo) ?  gameServerInfo.getId() : -1;

        if(serverId != -1) {
            serverName = String.format("[%d] %s", serverId, GameServerManager.getInstance().getServerNameById(serverId));
        }
        _log.info("ServerInfo {}: Connection Lost", serverName);

        if (AUTHED == state) {
            gameServerInfo.setDown();
            _log.info("Server [{}] {} is now set as disconnect", serverId, GameServerManager.getInstance().getServerNameById(serverId));
        }
	}
}
