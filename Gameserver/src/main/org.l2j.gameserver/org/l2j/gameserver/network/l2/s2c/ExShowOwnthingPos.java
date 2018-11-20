package org.l2j.gameserver.network.l2.s2c;

/**
 * @author VISTALL
 */
public class ExShowOwnthingPos extends L2GameServerPacket
{
	public ExShowOwnthingPos()
	{
		//
	}

	@Override
	protected void writeImpl()
	{
		writeInt(0x00);
	}
}