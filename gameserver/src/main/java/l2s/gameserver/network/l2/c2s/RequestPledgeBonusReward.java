package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.Config;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.ExPledgeBonusOpen;
import l2s.gameserver.utils.PledgeBonusUtils;

public class RequestPledgeBonusReward extends L2GameClientPacket
{
	private int _type;

	@Override
	protected void readImpl() throws Exception
	{
		_type = readC();
	}

	@Override
	protected void runImpl() throws Exception
	{
		if(!Config.EX_USE_PLEDGE_BONUS)
			return;

		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(PledgeBonusUtils.tryReceiveReward(_type, activeChar))
			activeChar.sendPacket(new ExPledgeBonusOpen(activeChar));
	}
}