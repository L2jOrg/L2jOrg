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
package org.l2j.authserver.network.gameserver.packet.game2auth;

import org.l2j.authserver.controller.GameServerManager;
import org.l2j.authserver.data.database.dao.GameserverDAO;
import org.l2j.authserver.network.GameServerInfo;
import org.l2j.authserver.network.gameserver.ServerClientState;
import org.l2j.authserver.network.gameserver.packet.auth2game.AuthResponse;

import static java.util.Objects.nonNull;
import static org.l2j.authserver.network.gameserver.packet.auth2game.LoginGameServerFail.*;
import static org.l2j.authserver.settings.AuthServerSettings.acceptNewGameServerEnabled;
import static org.l2j.commons.database.DatabaseAccess.getDAO;

public class AuthRequest extends GameserverReadablePacket {

	private  int desiredId;
    private  boolean acceptAlternativeId;
	private  int maxPlayers;
	private  int port;
	private  String[] hosts;
	private  int serverType;
    private byte ageLimit;
    private boolean showBrackets;
    private boolean isPvp;

    @Override
	protected void readImpl() {

		desiredId = readByte();
        acceptAlternativeId = readByte() == 0x01;
        serverType = readInt();
        maxPlayers = readInt();
        ageLimit = readByte();
        showBrackets = readByte() == 0x01;
        isPvp = readByte() == 0x01;

        hosts = new String[readShort() * 2];
        for (int i = 0; i < hosts.length; i+=2) {
            hosts[i] =  readString();
            hosts[i+1] = readString();
        }

        port = readShort();
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
        gsi.setHosts(hosts);
        gsi.setMaxPlayers(maxPlayers);
        gsi.setAuthed(true);
        gsi.setServerType(serverType);
        getDAO(GameserverDAO.class).updateServerType(gsi.getId(), serverType);
        gsi.setAgeLimit(ageLimit);
        gsi.setShowingBrackets(showBrackets);
        gsi.setIsPvp(isPvp);
        gsi.setStatus(ServerStatus.STATUS_AUTO);
    }
}
