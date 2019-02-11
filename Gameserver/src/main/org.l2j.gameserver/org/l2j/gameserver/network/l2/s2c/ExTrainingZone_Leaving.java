package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExTrainingZone_Leaving extends L2GameServerPacket
{
	public static ExTrainingZone_Leaving STATIC = new ExTrainingZone_Leaving();

	@Override
	public void writeImpl(GameClient client, ByteBuffer buffer)
	{
	}
}