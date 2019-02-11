package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.network.l2.s2c.CharacterSelectionInfoPacket;

import java.nio.ByteBuffer;

public class GotoLobby extends L2GameClientPacket
{
	@Override
	protected void readImpl(ByteBuffer buffer)
	{}

	@Override
	protected void runImpl()
	{
		sendPacket(new CharacterSelectionInfoPacket(client));
	}
}