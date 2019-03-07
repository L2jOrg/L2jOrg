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
package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import com.l2jmobius.commons.network.PacketWriter;
import com.l2jmobius.gameserver.enums.MacroUpdateType;
import com.l2jmobius.gameserver.model.Macro;
import com.l2jmobius.gameserver.model.MacroCmd;
import com.l2jmobius.gameserver.network.OutgoingPackets;

public class SendMacroList implements IClientOutgoingPacket
{
	private final int _count;
	private final Macro _macro;
	private final MacroUpdateType _updateType;
	
	public SendMacroList(int count, Macro macro, MacroUpdateType updateType)
	{
		_count = count;
		_macro = macro;
		_updateType = updateType;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.MACRO_LIST.writeId(packet);
		
		packet.writeC(_updateType.getId());
		packet.writeD(_updateType != MacroUpdateType.LIST ? _macro.getId() : 0x00); // modified, created or deleted macro's id
		packet.writeC(_count); // count of Macros
		packet.writeC(_macro != null ? 1 : 0); // unknown
		
		if ((_macro != null) && (_updateType != MacroUpdateType.DELETE))
		{
			packet.writeD(_macro.getId()); // Macro ID
			packet.writeS(_macro.getName()); // Macro Name
			packet.writeS(_macro.getDescr()); // Desc
			packet.writeS(_macro.getAcronym()); // acronym
			packet.writeD(_macro.getIcon()); // icon
			
			packet.writeC(_macro.getCommands().size()); // count
			
			int i = 1;
			for (MacroCmd cmd : _macro.getCommands())
			{
				packet.writeC(i++); // command count
				packet.writeC(cmd.getType().ordinal()); // type 1 = skill, 3 = action, 4 = shortcut
				packet.writeD(cmd.getD1()); // skill id
				packet.writeC(cmd.getD2()); // shortcut id
				packet.writeS(cmd.getCmd()); // command name
			}
		}
		return true;
	}
}
