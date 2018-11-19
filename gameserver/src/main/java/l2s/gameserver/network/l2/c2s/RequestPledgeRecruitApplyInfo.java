package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.instancemanager.clansearch.ClanSearchManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.ExPledgeRecruitApplyInfo;

/**
 * @author GodWorld
 * @reworked by Bonux
**/
public class RequestPledgeRecruitApplyInfo extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		//
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(activeChar.getClan() != null && activeChar.isClanLeader() && ClanSearchManager.getInstance().isClanRegistered(activeChar.getClanId()))
			activeChar.sendPacket(ExPledgeRecruitApplyInfo.ORDER_LIST);
		else if(activeChar.getClan() == null && ClanSearchManager.getInstance().isWaiterRegistered(activeChar.getObjectId()))
			activeChar.sendPacket(ExPledgeRecruitApplyInfo.WAITING);
		else
			activeChar.sendPacket(ExPledgeRecruitApplyInfo.DEFAULT);
	}
}