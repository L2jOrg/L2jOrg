package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.pledge.Alliance;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.tables.ClanTable;

public class RequestOustAlly extends L2GameClientPacket
{
	private String _clanName;

	@Override
	protected void readImpl()
	{
		_clanName = readS(32);
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		Clan leaderClan = activeChar.getClan();
		if(leaderClan == null)
		{
			activeChar.sendActionFailed();
			return;
		}
		Alliance alliance = leaderClan.getAlliance();
		if(alliance == null)
		{
			activeChar.sendPacket(SystemMsg.YOU_ARE_NOT_CURRENTLY_ALLIED_WITH_ANY_CLANS);
			return;
		}

		Clan clan;

		if(!activeChar.isAllyLeader())
		{
			activeChar.sendPacket(SystemMsg.THIS_FEATURE_IS_ONLY_AVAILABLE_TO_ALLIANCE_LEADERS);
			return;
		}

		if(_clanName == null)
			return;

		clan = ClanTable.getInstance().getClanByName(_clanName);

		if(clan != null)
		{
			if(!alliance.isMember(clan.getClanId()))
			{
				activeChar.sendActionFailed();
				return;
			}

			if(alliance.getLeader().equals(clan))
			{
				activeChar.sendPacket(SystemMsg.YOU_HAVE_FAILED_TO_WITHDRAW_FROM_THE_ALLIANCE);
				return;
			}

			clan.broadcastToOnlineMembers(SystemMsg.A_CLAN_THAT_HAS_WITHDRAWN_OR_BEEN_EXPELLED_CANNOT_ENTER_INTO_AN_ALLIANCE_WITHIN_ONE_DAY_OF_WITHDRAWAL_OR_EXPULSION);
			clan.setAllyId(0);
			clan.setLeavedAlly();
			alliance.broadcastAllyStatus();
			alliance.removeAllyMember(clan.getClanId());
			alliance.setExpelledMember();
			//activeChar.sendMessage(new CustomMessage("l2s.gameserver.network.l2.c2s.RequestOustAlly.ClanDismissed", activeChar).addString(clan.getName()).addString(alliance.getAllyName()));
		}
	}
}