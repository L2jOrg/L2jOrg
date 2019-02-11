package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * Asks the player to join a Command Channel
 */
public class ExAskJoinMPCCPacket extends L2GameServerPacket
{
	private String _requestorName;

	/**
	 * @param String Name of CCLeader
	 */
	public ExAskJoinMPCCPacket(String requestorName)
	{
		_requestorName = requestorName;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		writeString(_requestorName, buffer); // лидер CC
		buffer.putInt(0x00);
	}
}