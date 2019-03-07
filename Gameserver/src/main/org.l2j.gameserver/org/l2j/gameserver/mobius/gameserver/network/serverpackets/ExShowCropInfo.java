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
import com.l2jmobius.gameserver.instancemanager.CastleManorManager;
import com.l2jmobius.gameserver.model.CropProcure;
import com.l2jmobius.gameserver.model.L2Seed;
import com.l2jmobius.gameserver.network.OutgoingPackets;

import java.util.List;

/**
 * @author l3x
 */
public class ExShowCropInfo implements IClientOutgoingPacket
{
	private final List<CropProcure> _crops;
	private final int _manorId;
	private final boolean _hideButtons;
	
	public ExShowCropInfo(int manorId, boolean nextPeriod, boolean hideButtons)
	{
		_manorId = manorId;
		_hideButtons = hideButtons;
		
		final CastleManorManager manor = CastleManorManager.getInstance();
		_crops = (nextPeriod && !manor.isManorApproved()) ? null : manor.getCropProcure(manorId, nextPeriod);
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_SHOW_CROP_INFO.writeId(packet);
		
		packet.writeC(_hideButtons ? 0x01 : 0x00); // Hide "Crop Sales" button
		packet.writeD(_manorId); // Manor ID
		packet.writeD(0x00);
		if (_crops != null)
		{
			packet.writeD(_crops.size());
			for (CropProcure crop : _crops)
			{
				packet.writeD(crop.getId()); // Crop id
				packet.writeQ(crop.getAmount()); // Buy residual
				packet.writeQ(crop.getStartAmount()); // Buy
				packet.writeQ(crop.getPrice()); // Buy price
				packet.writeC(crop.getReward()); // Reward
				final L2Seed seed = CastleManorManager.getInstance().getSeedByCrop(crop.getId());
				if (seed == null)
				{
					packet.writeD(0); // Seed level
					packet.writeC(0x01); // Reward 1
					packet.writeD(0); // Reward 1 - item id
					packet.writeC(0x01); // Reward 2
					packet.writeD(0); // Reward 2 - item id
				}
				else
				{
					packet.writeD(seed.getLevel()); // Seed level
					packet.writeC(0x01); // Reward 1
					packet.writeD(seed.getReward(1)); // Reward 1 - item id
					packet.writeC(0x01); // Reward 2
					packet.writeD(seed.getReward(2)); // Reward 2 - item id
				}
			}
		}
		return true;
	}
}