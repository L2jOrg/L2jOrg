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
package org.l2j.gameserver.mobius.gameserver.network.serverpackets.ceremonyofchaos;

import com.l2jmobius.commons.network.PacketWriter;
import com.l2jmobius.gameserver.enums.CeremonyOfChaosResult;
import com.l2jmobius.gameserver.instancemanager.CeremonyOfChaosManager;
import com.l2jmobius.gameserver.model.ceremonyofchaos.CeremonyOfChaosEvent;
import com.l2jmobius.gameserver.network.OutgoingPackets;
import com.l2jmobius.gameserver.network.serverpackets.IClientOutgoingPacket;

/**
 * @author Sdw
 */
public class ExCuriousHouseResult implements IClientOutgoingPacket
{
	private final CeremonyOfChaosResult _result;
	private final CeremonyOfChaosEvent _event;
	
	public ExCuriousHouseResult(CeremonyOfChaosResult result, CeremonyOfChaosEvent event)
	{
		_result = result;
		_event = event;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_CURIOUS_HOUSE_RESULT.writeId(packet);
		packet.writeD(_event.getId());
		packet.writeH(_result.ordinal());
		packet.writeD(CeremonyOfChaosManager.getInstance().getMaxPlayersInArena());
		packet.writeD(_event.getMembers().size());
		_event.getMembers().values().forEach(m ->
		{
			packet.writeD(m.getObjectId());
			packet.writeD(m.getPosition());
			packet.writeD(m.getClassId());
			packet.writeD(m.getLifeTime());
			packet.writeD(m.getScore());
		});
		return true;
	}
	
}
