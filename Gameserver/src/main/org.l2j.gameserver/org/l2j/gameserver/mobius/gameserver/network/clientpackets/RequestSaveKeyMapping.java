/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import com.l2jmobius.Config;
import org.l2j.commons.network.PacketReader;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.ConnectionState;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;

/**
 * Request Save Key Mapping client packet.
 * @author Mobius
 */
public class RequestSaveKeyMapping extends IClientIncomingPacket
{
	public static final String UI_KEY_MAPPING_VAR = "UI_KEY_MAPPING";
	public static final String SPLIT_VAR = "	";
	private byte[] _uiKeyMapping;
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		final int dataSize = packet.getInt();
		if (dataSize > 0)
		{
			_uiKeyMapping = packet.get(new byte[dataSize]);
		}
		return true;
	}
	
	@Override
	public void runImpl()
	{
		final L2PcInstance player = client.getActiveChar();
		if (!Config.STORE_UI_SETTINGS || //
			(player == null) || //
			(_uiKeyMapping == null) || //
			(client.getConnectionState() != ConnectionState.IN_GAME))
		{
			return;
		}
		
		String uiKeyMapping = "";
		for (Byte b : _uiKeyMapping)
		{
			uiKeyMapping += b + SPLIT_VAR;
		}
		player.getVariables().set(UI_KEY_MAPPING_VAR, uiKeyMapping);
	}
}
