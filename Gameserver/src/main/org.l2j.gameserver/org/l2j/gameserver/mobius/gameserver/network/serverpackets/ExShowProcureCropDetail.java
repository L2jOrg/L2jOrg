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
import org.l2j.gameserver.mobius.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.mobius.gameserver.instancemanager.CastleManorManager;
import org.l2j.gameserver.mobius.gameserver.model.CropProcure;
import org.l2j.gameserver.mobius.gameserver.model.entity.Castle;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.util.HashMap;
import java.util.Map;

/**
 * @author l3x
 */
public class ExShowProcureCropDetail implements IClientOutgoingPacket
{
	private final int _cropId;
	private final Map<Integer, CropProcure> _castleCrops = new HashMap<>();
	
	public ExShowProcureCropDetail(int cropId)
	{
		_cropId = cropId;
		
		for (Castle c : CastleManager.getInstance().getCastles())
		{
			final CropProcure cropItem = CastleManorManager.getInstance().getCropProcure(c.getResidenceId(), cropId, false);
			if ((cropItem != null) && (cropItem.getAmount() > 0))
			{
				_castleCrops.put(c.getResidenceId(), cropItem);
			}
		}
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_SHOW_PROCURE_CROP_DETAIL.writeId(packet);
		
		packet.writeD(_cropId); // crop id
		packet.writeD(_castleCrops.size()); // size
		
		for (Map.Entry<Integer, CropProcure> entry : _castleCrops.entrySet())
		{
			final CropProcure crop = entry.getValue();
			packet.writeD(entry.getKey()); // manor name
			packet.writeQ(crop.getAmount()); // buy residual
			packet.writeQ(crop.getPrice()); // buy price
			packet.writeC(crop.getReward()); // reward type
		}
		return true;
	}
}
