package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * @author Bonux
 */
public class ExFriendNotifyNameChange extends L2GameServerPacket
{
	public ExFriendNotifyNameChange()
	{
		//
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		//TODO: [Bonux]
	}
}
