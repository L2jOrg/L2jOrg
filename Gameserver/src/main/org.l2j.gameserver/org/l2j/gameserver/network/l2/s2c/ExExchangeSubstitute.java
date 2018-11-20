package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;

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
	protected void writeImpl()
	{
		writeInt(0x00);
		writeLong(3000000L);
		writeInt(0x00);
	}
}
