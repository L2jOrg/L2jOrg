package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.clansearch.ClanSearchClan;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * @author GodWorld
 * @reworked by Bonux
**/
public class ExPledgeRecruitBoardDetail extends L2GameServerPacket
{
	private final ClanSearchClan _clan;

	public ExPledgeRecruitBoardDetail(ClanSearchClan clan)
	{
		_clan = clan;
	}

	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_clan.getClanId());
		buffer.putInt(_clan.getSearchType().ordinal());
		writeString("", buffer);
		writeString(_clan.getDesc(), buffer);
		buffer.putInt(_clan.getApplication());
		buffer.putInt(_clan.getSubUnit());
	}
}