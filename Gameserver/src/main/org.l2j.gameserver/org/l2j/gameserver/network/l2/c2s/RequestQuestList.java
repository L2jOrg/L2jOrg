package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.network.l2.s2c.QuestListPacket;

import java.nio.ByteBuffer;

public class RequestQuestList extends L2GameClientPacket
{
	@Override
	protected void readImpl(ByteBuffer buffer)
	{}

	@Override
	protected void runImpl()
	{
		sendPacket(new QuestListPacket(client.getActiveChar()));
	}
}