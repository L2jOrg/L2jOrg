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

import org.l2j.commons.network.PacketWriter;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.quest.Quest;
import org.l2j.gameserver.mobius.gameserver.model.quest.QuestState;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.util.List;

/**
 * @author Tempy
 */
public class GmViewQuestInfo implements IClientOutgoingPacket
{
	private final L2PcInstance _activeChar;
	private final List<Quest> _questList;
	
	public GmViewQuestInfo(L2PcInstance cha)
	{
		_activeChar = cha;
		_questList = cha.getAllActiveQuests();
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.GM_VIEW_QUEST_INFO.writeId(packet);
		packet.writeS(_activeChar.getName());
		packet.writeH(_questList.size()); // quest count
		
		for (Quest quest : _questList)
		{
			final QuestState qs = _activeChar.getQuestState(quest.getName());
			
			packet.writeD(quest.getId());
			packet.writeD(qs == null ? 0 : qs.getCond());
		}
		packet.writeH(0x00); // some size
		// for size; ddQQ
		return true;
	}
}
