package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;

/**
 * @author Bonux
**/
public class ExAdenaInvenCount extends L2GameServerPacket
{
	private final long _adena;
	private final int _useInventorySlots;

	public ExAdenaInvenCount(Player player)
	{
		_adena = player.getAdena();
		_useInventorySlots = player.getInventory().getSize();
	}

	@Override
	protected void writeImpl()
	{
		writeQ(_adena);
		writeH(_useInventorySlots);
	}
}