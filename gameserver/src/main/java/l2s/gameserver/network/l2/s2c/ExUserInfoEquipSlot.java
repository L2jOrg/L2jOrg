package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.PcInventory;
import l2s.gameserver.network.l2.s2c.updatetype.InventorySlot;

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
		writeD(_player.getObjectId());
		writeH(InventorySlot.VALUES.length);
		writeB(_masks);

		PcInventory inventory = _player.getInventory();
		for(InventorySlot slot : InventorySlot.VALUES)
		{
			if(containsMask(slot))
			{
				writeH(22); // size
				writeD(inventory.getPaperdollObjectId(slot.getSlot()));
				writeD(inventory.getPaperdollItemId(slot.getSlot()));
				writeD(inventory.getPaperdollVariation1Id(slot.getSlot()));
				writeD(inventory.getPaperdollVariation2Id(slot.getSlot()));
				writeD(inventory.getPaperdollVisualId(slot.getSlot()));
			}
		}
	}
}