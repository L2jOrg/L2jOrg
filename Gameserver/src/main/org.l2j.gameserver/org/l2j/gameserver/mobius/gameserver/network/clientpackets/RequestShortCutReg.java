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

import org.l2j.commons.network.PacketReader;
import org.l2j.gameserver.mobius.gameserver.enums.ShortcutType;
import org.l2j.gameserver.mobius.gameserver.model.Shortcut;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ShortCutRegister;

public final class RequestShortCutReg extends IClientIncomingPacket
{
	private ShortcutType _type;
	private int _id;
	private int _slot;
	private int _page;
	private int _lvl;
	private int _subLvl;
	private int _characterType; // 1 - player, 2 - pet
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		final int typeId = packet.getInt();
		_type = ShortcutType.values()[(typeId < 1) || (typeId > 6) ? 0 : typeId];
		final int slot = packet.getInt();
		_slot = slot % 12;
		_page = slot / 12;
		_id = packet.getInt();
		_lvl = packet.getShort();
		_subLvl = packet.getShort(); // Sublevel
		_characterType = packet.getInt();
		return true;
	}
	
	@Override
	public void runImpl()
	{
		if ((client.getActiveChar() == null) || (_page > 10) || (_page < 0))
		{
			return;
		}
		
		final Shortcut sc = new Shortcut(_slot, _page, _type, _id, _lvl, _subLvl, _characterType);
		client.getActiveChar().registerShortCut(sc);
		client.sendPacket(new ShortCutRegister(sc));
	}
}
