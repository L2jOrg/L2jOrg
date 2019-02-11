package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.ExListMpccWaiting;

import java.nio.ByteBuffer;

/**
 * @author VISTALL
 */
public class RequestExListMpccWaiting extends L2GameClientPacket
{
	private int _listId;
	private int _locationId;
	private boolean _allLevels;

	@Override
	protected void readImpl(ByteBuffer buffer) throws Exception
	{
		_listId = buffer.getInt();
		_locationId = buffer.getInt();
		_allLevels = buffer.getInt() == 1;
	}

	@Override
	protected void runImpl() throws Exception
	{
		Player player = client.getActiveChar();
		if(player == null)
			return;

		player.sendPacket(new ExListMpccWaiting(player, _listId, _locationId, _allLevels));
	}
}