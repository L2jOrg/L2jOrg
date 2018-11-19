package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.network.l2.s2c.ExPledgeRecruitInfo;
import l2s.gameserver.tables.ClanTable;

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
		_clanId = readD();
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