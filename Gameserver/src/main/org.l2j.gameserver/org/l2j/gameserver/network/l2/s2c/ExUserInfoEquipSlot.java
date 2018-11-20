package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.PcInventory;
import org.l2j.gameserver.network.l2.s2c.updatetype.InventorySlot;

public class ExUserInfoEquipSlot extends AbstractMaskPacket<InventorySlot>
{
	private final Player _player;
	private final byte[] _masks = new byte[]
	{
		(byte) 0x00,
		(byte) 0x00,
		(byte) 0x00,
		(byte) 0x00,
		(byte) 0x00
	};

	@Override
	protected byte[] getMasks()
	{
		return _masks;
	}

	@Override
	protected void onNewMaskAdded(InventorySlot component)
	{
	}

	public ExUserInfoEquipSlot(Player player)
	{
		_player = player;
		addComponentType(InventorySlot.VALUES);
	}

	public ExUserInfoEquipSlot(Player player, int slot)
	{
		_player = player;
		addComponentType(InventorySlot.valueOf(slot));
	}

	@Override
	protected final void writeImpl()
	{
		writeInt(_player.getObjectId());
		writeShort(InventorySlot.VALUES.length);
		writeB(_masks);

		PcInventory inventory = _player.getInventory();
		for(InventorySlot slot : InventorySlot.VALUES)
		{
			if(containsMask(slot))
			{
				writeShort(22); // size
				writeInt(inventory.getPaperdollObjectId(slot.getSlot()));
				writeInt(inventory.getPaperdollItemId(slot.getSlot()));
				writeInt(inventory.getPaperdollVariation1Id(slot.getSlot()));
				writeInt(inventory.getPaperdollVariation2Id(slot.getSlot()));
				writeInt(inventory.getPaperdollVisualId(slot.getSlot()));
			}
		}
	}
}