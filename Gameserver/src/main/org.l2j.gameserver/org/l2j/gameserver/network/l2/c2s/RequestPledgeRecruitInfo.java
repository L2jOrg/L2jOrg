package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.network.l2.s2c.ExPledgeRecruitInfo;
import org.l2j.gameserver.tables.ClanTable;

/**
 * @author GodWorld
 * @reworked by Bonux
**/
public class RequestPledgeRecruitInfo extends L2GameClientPacket
{
	private int _clanId;

	@Override
	protected void readImpl()
	{
		_clanId = readInt();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		Clan clan = ClanTable.getInstance().getClan(_clanId);
		if(clan == null)
			return;

		activeChar.sendPacket(new ExPledgeRecruitInfo(clan));
	}
}