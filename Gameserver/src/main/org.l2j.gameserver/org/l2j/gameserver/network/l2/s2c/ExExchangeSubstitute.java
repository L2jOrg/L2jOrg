package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 *
 * @author monithly
 * TODO this
 */
public class ExExchangeSubstitute extends L2GameServerPacket
{
	public ExExchangeSubstitute(Player pl, Player pl2)
	{
		//
	}
	
	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(0x00);
		buffer.putLong(3000000L);
		buffer.putInt(0x00);
	}
}
