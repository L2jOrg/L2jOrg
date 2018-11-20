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
		writeD(0x00);
		writeQ(3000000L);
		writeD(0x00);
	}
}
