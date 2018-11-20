package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.model.pledge.ClanWar;
import org.l2j.gameserver.model.pledge.ClanWar.ClanWarPeriod;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.SystemMessagePacket;
import org.l2j.gameserver.tables.ClanTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author GodWorld & reworked by Bonux
**/
public final class RequestSurrenderPledgeWar extends L2GameClientPacket
{
	private static final Logger _log = LoggerFactory.getLogger(RequestSurrenderPledgeWar.class);

	private String _pledgeName;

	@Override
	protected void readImpl()
	{
		_pledgeName = readString();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		Clan clan = activeChar.getClan();
		if(clan == null)
			return;

		Clan targetClan = ClanTable.getInstance().getClanByName(_pledgeName);
		if(targetClan == null)
		{
			activeChar.sendPacket(SystemMsg.THE_TARGET_FOR_DECLARATION_IS_WRONG);
			activeChar.sendActionFailed();
			return;
		}

		_log.info(getClass().getSimpleName() + ": by " + clan.getName() + " with " + _pledgeName);

		if(!clan.isAtWarWith(targetClan.getClanId()))
		{
			//TODO: activeChar.sendMessage("You aren't at war with this clan.");
			activeChar.sendActionFailed();
			return;
		}

		activeChar.sendPacket(new SystemMessagePacket(SystemMsg.YOU_HAVE_SURRENDERED_TO_THE_S1_CLAN).addString(_pledgeName));
		//activeChar.deathPenalty(false, false, false);

		ClanWar war = clan.getClanWar(targetClan);
		if(war != null)
			war.setPeriod(ClanWarPeriod.PEACE);
	}
}