package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;

public class ShowMinimapPacket extends L2GameServerPacket
{
	private int _mapId;

	public ShowMinimapPacket(Player player, int mapId)
	{
		_mapId = mapId;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_mapId);
		writeC(0x00);
	}
}