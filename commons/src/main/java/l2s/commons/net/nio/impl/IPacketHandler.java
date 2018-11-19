package l2s.commons.net.nio.impl;

import java.nio.ByteBuffer;

@SuppressWarnings("rawtypes")
public interface IPacketHandler<T extends MMOClient>
{
	public ReceivablePacket<T> handlePacket(ByteBuffer buf, T client);
}