package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ShowMinimapPacket extends L2GameServerPacket
{
	private int _mapId;

	public ShowMinimapPacket(Player player, int mapId)
	{
		_mapId = mapId;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_mapId);
		buffer.put((byte)0x00);
	}
}