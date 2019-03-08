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
import org.l2j.gameserver.mobius.gameserver.instancemanager.CastleManorManager;
import org.l2j.gameserver.mobius.gameserver.model.L2Seed;
import org.l2j.gameserver.mobius.gameserver.model.SeedProduction;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.util.List;

/**
 * @author l3x
 */
public class ExShowSeedInfo implements IClientOutgoingPacket
{
	private final List<SeedProduction> _seeds;
	private final int _manorId;
	private final boolean _hideButtons;
	
	public ExShowSeedInfo(int manorId, boolean nextPeriod, boolean hideButtons)
	{
		_manorId = manorId;
		_hideButtons = hideButtons;
		
		final CastleManorManager manor = CastleManorManager.getInstance();
		_seeds = (nextPeriod && !manor.isManorApproved()) ? null : manor.getSeedProduction(manorId, nextPeriod);
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_SHOW_SEED_INFO.writeId(packet);
		
		packet.writeC(_hideButtons ? 0x01 : 0x00); // Hide "Seed Purchase" button
		packet.writeD(_manorId); // Manor ID
		packet.writeD(0x00); // Unknown
		if (_seeds == null)
		{
			packet.writeD(0);
			return true;
		}
		
		packet.writeD(_seeds.size());
		for (SeedProduction seed : _seeds)
		{
			packet.writeD(seed.getId()); // Seed id
			packet.writeQ(seed.getAmount()); // Left to buy
			packet.writeQ(seed.getStartAmount()); // Started amount
			packet.writeQ(seed.getPrice()); // Sell Price
			final L2Seed s = CastleManorManager.getInstance().getSeed(seed.getId());
			if (s == null)
			{
				packet.writeD(0); // Seed level
				packet.writeC(0x01); // Reward 1
				packet.writeD(0); // Reward 1 - item id
				packet.writeC(0x01); // Reward 2
				packet.writeD(0); // Reward 2 - item id
			}
			else
			{
				packet.writeD(s.getLevel()); // Seed level
				packet.writeC(0x01); // Reward 1
				packet.writeD(s.getReward(1)); // Reward 1 - item id
				packet.writeC(0x01); // Reward 2
				packet.writeD(s.getReward(2)); // Reward 2 - item id
			}
		}
		return true;
	}
}