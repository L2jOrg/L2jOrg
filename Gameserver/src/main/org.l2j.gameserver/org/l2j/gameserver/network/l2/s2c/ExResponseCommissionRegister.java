package org.l2j.gameserver.network.l2.s2c;

public class ExResponseCommissionRegister extends L2GameServerPacket
{
	protected void writeImpl()
	{
		writeInt(0x00);
		writeInt(0x00);
		writeLong(0x00);
	}
}
