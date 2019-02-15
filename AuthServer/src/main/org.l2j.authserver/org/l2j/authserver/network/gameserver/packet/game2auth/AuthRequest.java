package org.l2j.authserver.network.gameserver.packet.game2auth;

import org.l2j.authserver.GameServerInfo;
import org.l2j.authserver.controller.GameServerManager;
import org.l2j.authserver.network.gameserver.ServerClientState;
import org.l2j.authserver.network.gameserver.packet.auth2game.AuthResponse;

import java.nio.ByteBuffer;

import static java.util.Objects.nonNull;
import static org.l2j.authserver.network.gameserver.packet.auth2game.LoginGameServerFail.*;
import static org.l2j.authserver.settings.AuthServerSettings.acceptNewGameServerEnabled;

public class AuthRequest extends GameserverReadablePacket {

	private  int desiredId;
    private  boolean acceptAlternativeId;
	private  int maxPlayers;
	private  int port;
	private  String externalHost;
	private  String internalHost;
	private  int serverType;
    private byte ageLimit;
    private boolean gmOnly;
    private boolean showBrackets;
    private boolean isPvp;

    @Override
	protected void readImpl(ByteBuffer buffer) {
		desiredId = buffer.get();
        acceptAlternativeId = buffer.get() == 0x01;
        internalHost = readString(buffer);
        externalHost = readString(buffer);
        port = buffer.getShort();
        serverType = buffer.getInt();
        ageLimit = buffer.get();
        gmOnly = buffer.get() == 0x01;
        showBrackets = buffer.get() == 0x01;
        isPvp = buffer.get() == 0x01;
        maxPlayers = buffer.getInt();
    }

	@Override
	protected void runImpl()  {
        GameServerManager gameServerManager = GameServerManager.getInstance();
        GameServerInfo gsi = gameServerManager.getRegisteredGameServerById(desiredId);

        if (nonNull(gsi)) {
            authenticGameServer(gsi);
        } else {
            gsi = processNewGameServer(gameServerManager);
        }

        if(nonNull(gsi) && gsi.isAuthed()) {
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

    private GameServerInfo processNewGameServer(GameServerManager gameServerManager) {
        if (acceptNewGameServerEnabled() && acceptAlternativeId) {
            GameServerInfo gsi = new GameServerInfo(desiredId, client);
            if (gameServerManager.registerWithFirstAvaliableId(gsi)) {
               updateGameServerInfo(gsi);
                gameServerManager.registerServerOnDB(gsi);
                return  gsi;
            } else {
                client.close(REASON_NO_FREE_ID);
            }
        } else {
            client.close(NOT_AUTHED);
        }
        return null;
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
        gsi.setAgeLimit(ageLimit);
        gsi.setShowingBrackets(showBrackets);
        gsi.setIsPvp(isPvp);
        gsi.setStatus(gmOnly ? ServerStatus.STATUS_GM_ONLY : ServerStatus.STATUS_AUTO);
    }
}
