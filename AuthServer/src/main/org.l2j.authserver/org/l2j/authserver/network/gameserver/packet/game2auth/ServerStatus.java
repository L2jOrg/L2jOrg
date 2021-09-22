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
package org.l2j.authserver.network.gameserver.packet.game2auth;

import io.github.joealisson.primitive.HashIntIntMap;
import io.github.joealisson.primitive.IntIntMap;

import static java.util.Objects.nonNull;

public class ServerStatus extends GameserverReadablePacket {

	private static final int SERVER_LIST_STATUS = 0x01;
	private static final int SERVER_LIST_SQUARE_BRACKET = 0x03;
	private static final int MAX_PLAYERS = 0x04;
	private static final int SERVER_LIST_TYPE = 0x06;
	
	public static final int STATUS_AUTO = 0x00;
	public static final int STATUS_DOWN = 0x04;
	public static final int STATUS_GM_ONLY = 0x05;
	
	private static final int ON = 0x01;
    IntIntMap status;

	@Override
	protected void readImpl()  {
        int size = readInt();
		status = new HashIntIntMap(size);
        for (int i = 0; i < size; i++) {
            status.put(readInt(), readInt());
        }
	}

	@Override
	protected void runImpl()  {
		final var gameServerInfo = client.getGameServerInfo();
		if (nonNull(gameServerInfo)) {
		    status.forEach((type, value) -> {
                switch (type) {
                    case SERVER_LIST_STATUS:
                        gameServerInfo.setStatus(value);
                        break;
                    case SERVER_LIST_SQUARE_BRACKET:
                        gameServerInfo.setShowingBrackets(value == ON);
                        break;
                    case MAX_PLAYERS:
                        gameServerInfo.setMaxAccounts(value);
                        break;
                    case SERVER_LIST_TYPE:
                        gameServerInfo.setType(value);
                }
            });
		}
	}
}