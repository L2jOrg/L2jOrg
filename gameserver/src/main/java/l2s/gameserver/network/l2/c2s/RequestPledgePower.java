package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.network.l2.s2c.ManagePledgePowerPacket;

public class RequestPledgePower extends L2GameClientPacket
{
	private int _rank;
	private int _action;
	private int _privs;

	@Override
	protected void readImpl()
	{
		_rank = readD();
		_action = readD();
		if(_action == 2)
			_privs = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
		if(_action == 2)
		{
			if(_rank < Clan.RANK_FIRST || _rank > Clan.RANK_LAST)
				return;
			if(activeChar.getClan() != null && (activeChar.getClanPrivileges() & Clan.CP_CL_MANAGE_RANKS) == Clan.CP_CL_MANAGE_RANKS)
			{
				if(_rank == 9) // Академикам оставляем только перечисленные ниже права
					_privs = (_privs & Clan.CP_CL_WAREHOUSE_SEARCH) + (_privs & Clan.CP_CH_ENTRY_EXIT) + (_privs & Clan.CP_CS_ENTRY_EXIT) + (_privs & Clan.CP_CH_USE_FUNCTIONS) + (_privs & Clan.CP_CS_USE_FUNCTIONS);
				activeChar.getClan().setRankPrivs(_rank, _privs);
				activeChar.getClan().updatePrivsForRank(_rank);
			}
		}
		else if(activeChar.getClan() != null)
			activeChar.sendPacket(new ManagePledgePowerPacket(activeChar, _action, _rank));
		else
			activeChar.sendActionFailed();
	}
}