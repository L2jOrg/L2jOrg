package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExUISettingPacket extends L2GameServerPacket
{
	private final byte data[];

	public ExUISettingPacket(Player player)
	{
		data = player.getKeyBindings();
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(data.length);
		buffer.put(data);
	}
}
