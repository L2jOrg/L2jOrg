package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.GameTimeController;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;


public class ClientSetTimePacket extends L2GameServerPacket {

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(GameTimeController.getInstance().getGameTime()); // time in client minutes
		buffer.putInt(6); //constant to match the server time( this determines the speed of the client clock)
	}
}