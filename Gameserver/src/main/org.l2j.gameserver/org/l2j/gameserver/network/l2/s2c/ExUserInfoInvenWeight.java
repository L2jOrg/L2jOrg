package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;

/**
 * @reworked by Bonux
**/
public class ExUserInfoInvenWeight extends L2GameServerPacket
{
	private final int _objectId;
	private final int _currentLoad;
	private final int _maxLoad;

	public ExUserInfoInvenWeight(Player player)
	{
		_objectId = player.getObjectId();
		_currentLoad = player.getCurrentLoad();
		_maxLoad = player.getMaxLoad();
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_objectId);
		writeD(_currentLoad);
		writeD(_maxLoad);
	}
}