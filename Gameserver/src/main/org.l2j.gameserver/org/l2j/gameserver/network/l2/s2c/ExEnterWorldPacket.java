package org.l2j.gameserver.network.l2.s2c;

public class ExEnterWorldPacket extends L2GameServerPacket
{
	private final int _serverTime;

	public ExEnterWorldPacket()
	{
		_serverTime = (int) (System.currentTimeMillis() / 1000L);
	}

	@Override
	protected final void writeImpl()
	{
		writeInt(_serverTime);
	}

	@Override
	protected int packetSize() {
		return 9;
	}
}