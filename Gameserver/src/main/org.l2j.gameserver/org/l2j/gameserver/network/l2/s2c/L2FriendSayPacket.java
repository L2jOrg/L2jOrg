package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * Send Private (Friend) Message
 *
 * Format: c dSSS
 *
 * d: Unknown
 * S: Sending Player
 * S: Receiving Player
 * S: Message
 */
public class L2FriendSayPacket extends L2GameServerPacket
{
	private String _sender, _receiver, _message;

	public L2FriendSayPacket(String sender, String reciever, String message)
	{
		_sender = sender;
		_receiver = reciever;
		_message = message;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(0);
		writeString(_receiver, buffer);
		writeString(_sender, buffer);
		writeString(_message, buffer);
	}
}