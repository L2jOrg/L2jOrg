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
import com.l2jmobius.gameserver.network.OutgoingPackets;

/**
 * @author UnAfraid
 */
public class ExAlterSkillRequest implements IClientOutgoingPacket
{
	private final int _currentSkillId;
	private final int _nextSkillId;
	private final int _alterTime;
	
	public ExAlterSkillRequest(int currentSkill, int nextSkill, int alterTime)
	{
		_currentSkillId = currentSkill;
		_nextSkillId = nextSkill;
		_alterTime = alterTime;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_ALTER_SKILL_REQUEST.writeId(packet);
		packet.writeD(_nextSkillId);
		packet.writeD(_currentSkillId);
		packet.writeD(_alterTime);
		return true;
	}
}
