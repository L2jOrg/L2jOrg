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
package org.l2j.authserver.network.client.packet.client2auth;

import org.l2j.authserver.network.client.packet.AuthClientPacket;
import org.l2j.authserver.network.client.packet.auth2client.LoginFail;
import org.l2j.authserver.network.client.packet.auth2client.ServerList;

/**
 * Format: ddc d: fist part of session id d: second part of session id c: list Type
 *
 * TYPE_BARE=0 - Indicates that each game server will have its basic information specified.
 *
 * TYPE_C0=1 - Indicates that each game server will have its additional and dynamic information specified.
 *
 * TYPE_NAMED=2 - Indicates that each game server will have its name specified.
 *
 * TYPE_C1=3 - Indicates that each game server will have its type mask specified.
 *
 * TYPE_C2=4 - Indicates that each game server will have its bracket flag specified.
 *
 * TYPE_FREYA=5 - Indicates that each game server will have reader's character count(s) specified.
 */
public class RequestServerList extends AuthClientPacket
{
	private int accountId;
	private int authId;
    private byte listType;

	@Override
	public boolean readImpl() {
        accountId = readInt();
        authId = readInt();
        listType = readByte();
		return true;
	}
	
	@Override
	public void run()
	{
		if (getClient().getSessionKey().checkLoginPair(accountId, authId))
		{
			getClient().sendPacket(new ServerList(listType));
		}
		else
		{
			getClient().close(LoginFail.LoginFailReason.REASON_ACCESS_FAILED);
		}
	}
}
