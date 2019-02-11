package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExBR_GamePointPacket extends L2GameServerPacket
{
	private int _objectId;
	private long _points;

	public ExBR_GamePointPacket(Player player)
	{
		_objectId = player.getObjectId();
		_points = player.getPremiumPoints();
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_objectId);
		buffer.putLong(_points);
		buffer.putInt(0x00); //??
	}
}