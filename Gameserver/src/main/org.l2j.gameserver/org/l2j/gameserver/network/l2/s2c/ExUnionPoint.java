package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * @author Bonux
**/
public class ExUnionPoint extends L2GameServerPacket
{
	private final int _clanId;

	public ExUnionPoint(int clanId)
	{
		_clanId = clanId;

	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_clanId);
	}
}
