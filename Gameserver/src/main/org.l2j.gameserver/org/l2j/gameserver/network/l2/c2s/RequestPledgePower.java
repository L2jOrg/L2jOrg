package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.network.l2.s2c.ManagePledgePowerPacket;

import java.nio.ByteBuffer;

public class RequestPledgePower extends L2GameClientPacket
{
	private int _rank;
	private int _action;
	private int _privs;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_rank = buffer.getInt();
		_action = buffer.getInt();
		if(_action == 2)
			_privs = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
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