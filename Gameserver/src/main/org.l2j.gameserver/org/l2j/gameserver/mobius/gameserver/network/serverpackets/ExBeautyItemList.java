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
import org.l2j.gameserver.mobius.gameserver.data.xml.impl.BeautyShopData;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.beautyshop.BeautyData;
import org.l2j.gameserver.mobius.gameserver.model.beautyshop.BeautyItem;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sdw
 */
public class ExBeautyItemList implements IClientOutgoingPacket
{
	private int _colorCount;
	private final BeautyData _beautyData;
	private final Map<Integer, List<BeautyItem>> _colorData = new HashMap<>();
	private static final int HAIR_TYPE = 0;
	private static final int FACE_TYPE = 1;
	private static final int COLOR_TYPE = 2;
	
	public ExBeautyItemList(L2PcInstance activeChar)
	{
		_beautyData = BeautyShopData.getInstance().getBeautyData(activeChar.getRace(), activeChar.getAppearance().getSexType());
		
		for (BeautyItem hair : _beautyData.getHairList().values())
		{
			final List<BeautyItem> colors = new ArrayList<>();
			for (BeautyItem color : hair.getColors().values())
			{
				colors.add(color);
				_colorCount++;
			}
			_colorData.put(hair.getId(), colors);
		}
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_BEAUTY_ITEM_LIST.writeId(packet);
		
		packet.writeD(HAIR_TYPE);
		packet.writeD(_beautyData.getHairList().size());
		for (BeautyItem hair : _beautyData.getHairList().values())
		{
			packet.writeD(0); // ?
			packet.writeD(hair.getId());
			packet.writeD(hair.getAdena());
			packet.writeD(hair.getResetAdena());
			packet.writeD(hair.getBeautyShopTicket());
			packet.writeD(1); // Limit
		}
		
		packet.writeD(FACE_TYPE);
		packet.writeD(_beautyData.getFaceList().size());
		for (BeautyItem face : _beautyData.getFaceList().values())
		{
			packet.writeD(0); // ?
			packet.writeD(face.getId());
			packet.writeD(face.getAdena());
			packet.writeD(face.getResetAdena());
			packet.writeD(face.getBeautyShopTicket());
			packet.writeD(1); // Limit
		}
		
		packet.writeD(COLOR_TYPE);
		packet.writeD(_colorCount);
		for (int hairId : _colorData.keySet())
		{
			for (BeautyItem color : _colorData.get(hairId))
			{
				packet.writeD(hairId);
				packet.writeD(color.getId());
				packet.writeD(color.getAdena());
				packet.writeD(color.getResetAdena());
				packet.writeD(color.getBeautyShopTicket());
				packet.writeD(1);
			}
		}
		return true;
	}
}
