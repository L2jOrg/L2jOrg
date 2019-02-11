package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.instances.StaticObjectInstance;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * format: d
 */
public class ChairSitPacket extends L2GameServerPacket
{
	private int _objectId;
	private int _staticObjectId;

	public ChairSitPacket(Player player, StaticObjectInstance throne)
	{
		_objectId = player.getObjectId();
		_staticObjectId = throne.getUId();
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_objectId);
		buffer.putInt(_staticObjectId);
	}
}