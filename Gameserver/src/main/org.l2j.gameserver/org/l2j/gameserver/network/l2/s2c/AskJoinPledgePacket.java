package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class AskJoinPledgePacket extends L2GameServerPacket
{
	private int _requestorId;
	private String _pledgeName;

	public AskJoinPledgePacket(int requestorId, String pledgeName)
	{
		_requestorId = requestorId;
		_pledgeName = pledgeName;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_requestorId);
		writeString("", buffer);
		writeString(_pledgeName, buffer);
		buffer.putInt(0);
		writeString("", buffer);
	}
}