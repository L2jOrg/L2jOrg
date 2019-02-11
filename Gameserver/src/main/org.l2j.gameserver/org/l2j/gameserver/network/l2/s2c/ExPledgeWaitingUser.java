package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * @author GodWorld
 * @reworked by Bonux
**/
public class ExPledgeWaitingUser extends L2GameServerPacket
{
	private final int _charId;
	private final String _desc;

	public ExPledgeWaitingUser(int charId, String desc)
	{
		_charId = charId;
		_desc = desc;
	}

	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_charId);
		writeString(_desc, buffer);
	}
}