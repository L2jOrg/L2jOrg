package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.Config;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.model.pledge.ClanWar;
import l2s.gameserver.model.pledge.ClanWar.ClanWarPeriod;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.tables.ClanTable;

public class RequestStartPledgeWar extends L2GameClientPacket
{
	private String _pledgeName;

	@Override
	protected void readImpl()
	{
		_pledgeName = readS(32);
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		Clan clan = activeChar.getClan();
		if(clan == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		if(!((activeChar.getClanPrivileges() & Clan.CP_CL_CLAN_WAR) == Clan.CP_CL_CLAN_WAR))
		{
			activeChar.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			activeChar.sendActionFailed();
			return;
		}

		if(clan.getLevel() < Config.CLAN_WAR_MINIMUM_CLAN_LEVEL || clan.getAllSize() < Config.CLAN_WAR_MINIMUM_PLAYERS_DECLARE)
		{
			activeChar.sendPacket(SystemMsg.A_CLAN_WAR_CAN_ONLY_BE_DECLARED_IF_THE_CLAN_IS_LEVEL_3_OR_ABOVE_AND_THE_NUMBER_OF_CLAN_MEMBERS_IS_FIFTEEN_OR_GREATER);
			activeChar.sendActionFailed();
			return;
		}

		if(clan.getWarList().size() >= 30)
		{
			activeChar.sendPacket(SystemMsg.A_DECLARATION_OF_WAR_AGAINST_MORE_THAN_30_CLANS_CANT_BE_MADE_AT_THE_SAME_TIME);
			activeChar.sendActionFailed();
			return;
		}

		Clan targetClan = ClanTable.getInstance().getClanByName(_pledgeName);
		if(targetClan == null)
		{
			activeChar.sendPacket(SystemMsg.A_CLAN_WAR_CANNOT_BE_DECLARED_AGAINST_A_CLAN_THAT_DOES_NOT_EXIST);
			activeChar.sendActionFailed();
			return;
		}

		if(clan.equals(targetClan))
		{
			activeChar.sendPacket(SystemMsg.FOOL_YOU_CANNOT_DECLARE_WAR_AGAINST_YOUR_OWN_CLAN);
			activeChar.sendActionFailed();
			return;
		}

		if(clan.getAllyId() != 0 && clan.getAllyId() == targetClan.getAllyId())
		{
			activeChar.sendPacket(SystemMsg.A_DECLARATION_OF_CLAN_WAR_AGAINST_AN_ALLIED_CLAN_CANT_BE_MADE);
			activeChar.sendActionFailed();
			return;
		}

		if(targetClan.getLevel() < Config.CLAN_WAR_MINIMUM_CLAN_LEVEL || targetClan.getAllSize() < Config.CLAN_WAR_MINIMUM_PLAYERS_DECLARE)
		{
			activeChar.sendPacket(SystemMsg.A_CLAN_WAR_CAN_ONLY_BE_DECLARED_IF_THE_CLAN_IS_LEVEL_3_OR_ABOVE_AND_THE_NUMBER_OF_CLAN_MEMBERS_IS_FIFTEEN_OR_GREATER);
			activeChar.sendActionFailed();
			return;
		}

		ClanWar war = clan.getClanWar(targetClan);
		if(war != null)
		{
			if(war.getAttackerClan() == clan)
			{
				activeChar.sendPacket(new SystemMessagePacket(SystemMsg.YOU_HAVE_ALREADY_BEEN_AT_WAR_WITH_THE_S1_CLAN_5_DAYS_MUST_PASS_BEFORE_YOU_CAN_DECLARE_WAR_AGAIN).addString(targetClan.getName()));
				activeChar.sendActionFailed();
				return;
			}

			if(war.getPeriod() == ClanWarPeriod.PEACE)
			{
				activeChar.sendPacket(new SystemMessagePacket(SystemMsg.YOU_HAVE_ALREADY_BEEN_AT_WAR_WITH_THE_S1_CLAN_5_DAYS_MUST_PASS_BEFORE_YOU_CAN_CHALLENGE_THIS_CLAN_AGAIN).addString(targetClan.getName()));
				activeChar.sendActionFailed();
				return;
			}

			war.accept(clan);
		}
		else
			new ClanWar(clan, targetClan, ClanWarPeriod.NEW, (int) (System.currentTimeMillis() / 1000L), 0, 0, 0);
	}
}