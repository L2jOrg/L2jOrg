package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;

import java.nio.ByteBuffer;

public class RequestBlockMemoInfo extends L2GameClientPacket
{
	private String _name;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_name = readString(buffer);
	}

	@Override
	protected void runImpl()
	{
		Player player = client.getActiveChar();
		if(player == null)
			return;
	}
}