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

import org.l2j.authserver.controller.AuthController;
import org.l2j.authserver.network.gameserver.packet.auth2game.PlayerAuthResponse;
import org.l2j.commons.network.SessionKey;

import java.util.Objects;

public class PlayerAuthRequest extends GameserverReadablePacket {
	
	private  String account;
	private int sessionId;
	private int serverAccountId;
	private int authAccountId;
	private int authKey;

	@Override
	protected void readImpl() {
		account = readString();
		sessionId = readInt();
		serverAccountId = readInt();
		authAccountId = readInt();
		authKey = readInt();
	}

	@Override
	protected void runImpl()  {
		var sessionKey = new SessionKey(authAccountId, authKey, sessionId, serverAccountId);
		var authedkey = AuthController.getInstance().getKeyForAccount(account);

		PlayerAuthResponse authResponse;
		if(Objects.equals(sessionKey, authedkey)) {
			authResponse = new PlayerAuthResponse(account, true);
		} else {
			authResponse = new PlayerAuthResponse(account, false);
		}
		client.sendPacket(authResponse);
	}
}