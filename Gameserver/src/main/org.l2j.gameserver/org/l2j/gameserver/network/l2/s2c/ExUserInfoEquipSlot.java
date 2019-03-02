package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.PcInventory;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.network.l2.s2c.updatetype.InventorySlot;

import java.nio.ByteBuffer;

public class ExUserInfoEquipSlot extends AbstractMaskPacket<InventorySlot>
{
	private final Player _player;
	private final byte[] _masks = new byte[]
	{
		(byte) 0x00,
		(byte) 0x00,
		(byte) 0x00,
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
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_player.getObjectId());
		buffer.putShort((short) InventorySlot.VALUES.length);
		buffer.put(_masks);

		PcInventory inventory = _player.getInventory();
		for(InventorySlot slot : InventorySlot.VALUES)
		{
			if(containsMask(slot))
			{
				buffer.putShort((short) 22); // size
				buffer.putInt(inventory.getPaperdollObjectId(slot.getSlot()));
				buffer.putInt(inventory.getPaperdollItemId(slot.getSlot()));
				buffer.putInt(inventory.getPaperdollVariation1Id(slot.getSlot()));
				buffer.putInt(inventory.getPaperdollVariation2Id(slot.getSlot()));
				buffer.putInt(inventory.getPaperdollVisualId(slot.getSlot()));
			}
		}
	}
}