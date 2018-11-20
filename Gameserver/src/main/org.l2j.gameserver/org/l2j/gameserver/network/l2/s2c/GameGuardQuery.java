package org.l2j.gameserver.network.l2.s2c;

public class GameGuardQuery extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		writeInt(0x00); // ? - Меняется при каждом перезаходе.
		writeInt(0x00); // ? - Меняется при каждом перезаходе.
		writeInt(0x00); // ? - Меняется при каждом перезаходе.
		writeInt(0x00); // ? - Меняется при каждом перезаходе.
	}
}