package org.l2j.gameserver.network.l2.s2c;

/**
 * @author VISTALL
 */
public class ExReplyRegisterDominion extends L2GameServerPacket
{
	public ExReplyRegisterDominion()
	{
		//
	}

	@Override
	protected void writeImpl()
	{
		writeInt(0x00);
		writeInt(0x00);
		writeInt(0x00);
		writeInt(0x00);
		writeInt(0x00);
		writeInt(0x00);
	}
}