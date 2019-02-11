package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * @author Bonux
**/
public class FriendRemove extends L2GameServerPacket
{
	private final String _friendName;

	public FriendRemove(String name)
	{
		_friendName = name;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(1); //UNK
		writeString(_friendName, buffer); //FriendName
	}
}
