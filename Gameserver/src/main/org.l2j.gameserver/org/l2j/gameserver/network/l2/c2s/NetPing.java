package org.l2j.gameserver.network.l2.c2s;

/**
 * format: ddd
 */
public class NetPing extends L2GameClientPacket
{
	@SuppressWarnings("unused")
	private int unk, unk2, unk3;

	@Override
	protected void runImpl()
	{
		//logger.info.println(getType() + " :: " + unk + " :: " + unk2 + " :: " + unk3);
	}

	@Override
	protected void readImpl()
	{
		unk = readInt();
		unk2 = readInt();
		unk3 = readInt();
	}
}