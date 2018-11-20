package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.instances.StaticObjectInstance;

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
	protected final void writeImpl()
	{
		writeD(_objectId);
		writeD(_staticObjectId);
	}
}