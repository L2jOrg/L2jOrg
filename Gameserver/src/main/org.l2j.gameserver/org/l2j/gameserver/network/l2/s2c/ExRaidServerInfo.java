package org.l2j.gameserver.network.l2.s2c;

public class ExRaidServerInfo extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		writeByte(0x00);
		writeByte(0x00);
	}
}
