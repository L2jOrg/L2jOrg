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
package org.l2j.gameserver.network.authcomm.gs2as;

import org.l2j.gameserver.network.authcomm.AuthServerClient;
import org.l2j.gameserver.network.authcomm.SendablePacket;

/**
 * @author VISTALL
 * @date 21:07/25.03.2011
 */
public class SetAccountInfo extends SendablePacket
{
	private String _account;
	private int _size;

	public SetAccountInfo(String account, int size)
	{
		_account = account;
		_size = size;
	}

	@Override
	protected void writeImpl(AuthServerClient client) {
		writeByte((byte)0x05);
		writeString(_account);
		writeByte((byte)_size);
	}
}
