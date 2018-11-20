package org.l2j.authserver.network.gameserver.packet.game2auth;

import org.l2j.authserver.GameServerInfo;
import org.l2j.authserver.controller.GameServerManager;
import org.l2j.authserver.network.gameserver.ServerClientState;
import org.l2j.authserver.network.gameserver.packet.auth2game.AuthResponse;

import static java.util.Objects.nonNull;
import static org.l2j.authserver.network.gameserver.packet.auth2game.LoginGameServerFail.*;
import static org.l2j.authserver.settings.AuthServerSettings.acceptNewGameServerEnabled;

public class GameServerAuth extends GameserverReadablePacket {

	private  int desiredId;
    private  boolean acceptAlternativeId;
	private  int maxPlayers;
	private  int port;
	private  String externalHost;
	private  String internalHost;
	private  int serverType;

	@Override
	protected void readImpl() {
		desiredId = readByte();
		serverType = readInt();
		acceptAlternativeId = readByte() != 0;
        readByte(); // hostReserved
		externalHost = readString();
		internalHost = readString();
		port = readShort();
		maxPlayers = readInt();
	}

	@Override
	protected void runImpl()  {
        GameServerManager gameServerManager = GameServerManager.getInstance();
        GameServerInfo gsi = gameServerManager.getRegisteredGameServerById(desiredId);

        if (nonNull(gsi)) {
            authenticGameServer(gsi);
        } else {
            processNewGameServer(gameServerManager);
        }

        if(gsi.isAuthed()) {
            client.sendPacket(new AuthResponse(gsi.getId()));
        }
	}

    private void authenticGameServer(GameServerInfo gsi) {
        if (gsi.isAuthed()) {
            client.close(REASON_ALREADY_LOGGED);
        } else {
            updateGameServerInfo(gsi);
        }
    }

    private void processNewGameServer(GameServerManager gameServerManager) {
        GameServerInfo gsi;
        if (acceptNewGameServerEnabled() && acceptAlternativeId) {
            gsi = new GameServerInfo(desiredId, client);
            if (gameServerManager.registerWithFirstAvaliableId(gsi)) {
               updateGameServerInfo(gsi);
                gameServerManager.registerServerOnDB(gsi);
            } else {
                client.close(REASON_NO_FREE_ID);
            }
        } else {
            client.close(NOT_AUTHED);
        }
    }

    private void updateGameServerInfo(GameServerInfo gsi) {
        client.setGameServerInfo(gsi);
        client.setState(ServerClientState.AUTHED);
        gsi.setClient(client);
        gsi.setPort(port);
        gsi.setHosts(externalHost, internalHost);
        gsi.setMaxPlayers(maxPlayers);
        gsi.setAuthed(true);
        gsi.setServerType(serverType);
    }
}
