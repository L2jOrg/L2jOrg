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

import com.l2jmobius.commons.network.PacketReader;
import com.l2jmobius.gameserver.enums.CharacterDeleteFailType;
import com.l2jmobius.gameserver.model.CharSelectInfoPackage;
import com.l2jmobius.gameserver.model.events.Containers;
import com.l2jmobius.gameserver.model.events.EventDispatcher;
import com.l2jmobius.gameserver.model.events.impl.character.player.OnPlayerDelete;
import com.l2jmobius.gameserver.network.L2GameClient;
import com.l2jmobius.gameserver.network.serverpackets.CharDeleteFail;
import com.l2jmobius.gameserver.network.serverpackets.CharDeleteSuccess;
import com.l2jmobius.gameserver.network.serverpackets.CharSelectionInfo;

import java.util.logging.Level;

/**
 * This class ...
 * @version $Revision: 1.8.2.1.2.3 $ $Date: 2005/03/27 15:29:30 $
 */
public final class CharacterDelete implements IClientIncomingPacket
{
	// cd
	private int _charSlot;
	
	@Override
	public boolean read(L2GameClient client, PacketReader packet)
	{
		_charSlot = packet.readD();
		return true;
	}
	
	@Override
	public void run(L2GameClient client)
	{
		// if (!client.getFloodProtectors().getCharacterSelect().tryPerformAction("CharacterDelete"))
		// {
		// client.sendPacket(new CharDeleteFail(CharacterDeleteFailType.UNKNOWN));
		// return;
		// }
		
		try
		{
			final CharacterDeleteFailType failType = client.markToDeleteChar(_charSlot);
			switch (failType)
			{
				case NONE:// Success!
				{
					client.sendPacket(new CharDeleteSuccess());
					final CharSelectInfoPackage charInfo = client.getCharSelection(_charSlot);
					EventDispatcher.getInstance().notifyEvent(new OnPlayerDelete(charInfo.getObjectId(), charInfo.getName(), client), Containers.Players());
					break;
				}
				default:
				{
					client.sendPacket(new CharDeleteFail(failType));
					break;
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "Error:", e);
		}
		
		final CharSelectionInfo cl = new CharSelectionInfo(client.getAccountName(), client.getSessionId().playOkID1, 0);
		client.sendPacket(cl);
		client.setCharSelection(cl.getCharInfo());
	}
}
