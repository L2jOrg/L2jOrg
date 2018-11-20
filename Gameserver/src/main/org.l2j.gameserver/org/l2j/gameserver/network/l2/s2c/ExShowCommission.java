package org.l2j.gameserver.network.l2.s2c;

/**
 * @author Bonux
 */
public class ExShowCommission extends L2GameServerPacket
{
	public ExShowCommission()
	{
		//
	}

	@Override
	protected final void writeImpl()
	{
		writeD(0x01); // ??Open??
	}
}