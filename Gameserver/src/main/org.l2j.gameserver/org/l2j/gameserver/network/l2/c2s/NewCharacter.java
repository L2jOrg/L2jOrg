package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.network.l2.s2c.NewCharacterSuccessPacket;

import java.nio.ByteBuffer;

public class NewCharacter extends L2GameClientPacket
{
	@Override
	protected void readImpl(ByteBuffer buffer)
	{}

	@Override
	protected void runImpl()
	{
		sendPacket(NewCharacterSuccessPacket.STATIC);
	}
}