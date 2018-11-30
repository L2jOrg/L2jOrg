package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.GameTimeController;


public class ClientSetTimePacket extends L2GameServerPacket {

	@Override
	protected final void writeImpl()
	{
		writeInt(GameTimeController.getInstance().getGameTime()); // time in client minutes
		writeInt(6); //constant to match the server time( this determines the speed of the client clock)
	}
}