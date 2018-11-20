package org.l2j.gameserver.network.l2.s2c;

public class FriendAddRequestResult extends L2GameServerPacket
{
	public FriendAddRequestResult()
	{}

	@Override
	protected final void writeImpl()
	{
		// TODO: when implementing, consult an up-to-date packets_game_server.xml and/or savormix
		writeInt(0); // Accepted
		writeInt(0); // Character ID
		writeString(""); // Name
		writeInt(0); // Online
		writeInt(0); // Friend OID
		writeInt(0); // Level
		writeInt(0); // Class
		writeShort(0); // ??? 0
	}
}
