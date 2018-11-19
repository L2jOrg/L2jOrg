package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.network.l2.s2c.PledgeInfoPacket;
import l2s.gameserver.tables.ClanTable;

public class RequestPledgeInfo extends L2GameClientPacket
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
		if(_clanId < 10000000)
		{
			activeChar.sendActionFailed();
			return;
		}
		Clan clan = ClanTable.getInstance().getClan(_clanId);
		if(clan == null)
		{
			//Util.handleIllegalPlayerAction(activeChar, "RequestPledgeInfo[40]", "Clan data for clanId " + _clanId + " is missing", 1);
			//_log.warn("Host " + getClient().getIpAddr() + " possibly sends fake packets. activeChar: " + activeChar);
			activeChar.sendActionFailed();
			return;
		}

		activeChar.sendPacket(new PledgeInfoPacket(clan));
	}
}