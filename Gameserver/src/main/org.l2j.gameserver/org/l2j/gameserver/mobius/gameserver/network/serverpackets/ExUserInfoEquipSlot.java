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
import org.l2j.gameserver.mobius.gameserver.enums.InventorySlot;
import org.l2j.gameserver.mobius.gameserver.model.VariationInstance;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.itemcontainer.PcInventory;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

/**
 * @author Sdw
 */
public class ExUserInfoEquipSlot extends AbstractMaskPacket<InventorySlot>
{
	private final L2PcInstance _activeChar;
	
	private final byte[] _masks = new byte[]
	{
		(byte) 0x00,
		(byte) 0x00,
		(byte) 0x00,
		(byte) 0x00,
		(byte) 0x00,
		(byte) 0x00, // 152
		(byte) 0x00, // 152
		(byte) 0x00, // 152
	};
	
	public ExUserInfoEquipSlot(L2PcInstance cha)
	{
		this(cha, true);
	}
	
	public ExUserInfoEquipSlot(L2PcInstance cha, boolean addAll)
	{
		_activeChar = cha;
		
		if (addAll)
		{
			addComponentType(InventorySlot.values());
		}
	}
	
	@Override
	protected byte[] getMasks()
	{
		return _masks;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_USER_INFO_EQUIP_SLOT.writeId(packet);
		
		packet.writeD(_activeChar.getObjectId());
		packet.writeH(InventorySlot.values().length); // 152
		packet.writeB(_masks);
		
		final PcInventory inventory = _activeChar.getInventory();
		for (InventorySlot slot : InventorySlot.values())
		{
			if (containsMask(slot))
			{
				final VariationInstance augment = inventory.getPaperdollAugmentation(slot.getSlot());
				packet.writeH(22); // 10 + 4 * 3
				packet.writeD(inventory.getPaperdollObjectId(slot.getSlot()));
				packet.writeD(inventory.getPaperdollItemId(slot.getSlot()));
				packet.writeD(augment != null ? augment.getOption1Id() : 0);
				packet.writeD(augment != null ? augment.getOption2Id() : 0);
				packet.writeD(inventory.getPaperdollItemVisualId(slot.getSlot()));
			}
		}
		return true;
	}
}