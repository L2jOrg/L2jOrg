package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class FriendAddRequestResult extends L2GameServerPacket
{
	public FriendAddRequestResult()
	{}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		// TODO: when implementing, consult an up-to-date packets_game_server.xml and/or savormix
		buffer.putInt(0); // Accepted
		buffer.putInt(0); // Character ID
		writeString("", buffer); // Name
		buffer.putInt(0); // Online
		buffer.putInt(0); // Friend OID
		buffer.putInt(0); // Level
		buffer.putInt(0); // Class
		buffer.putShort((short) 0); // ??? 0
	}
}
