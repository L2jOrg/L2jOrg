package org.l2j.gameserver.network.l2.s2c;

public class ExItemAuctionStatus extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		writeShort(0);
		writeShort(0);
		writeShort(0);
		writeShort(0);
		writeShort(0);
		writeShort(0);
		writeInt(0);
		writeByte(0);
	}
}