package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.ExPledgeBonusOpen;
import org.l2j.gameserver.utils.PledgeBonusUtils;

import java.nio.ByteBuffer;

public class RequestPledgeBonusReward extends L2GameClientPacket
{
	private int _type;

	@Override
	protected void readImpl(ByteBuffer buffer) throws Exception
	{
		_type = buffer.get();
	}

	@Override
	protected void runImpl() throws Exception
	{
		if(!Config.EX_USE_PLEDGE_BONUS)
			return;

		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;

		if(PledgeBonusUtils.tryReceiveReward(_type, activeChar))
			activeChar.sendPacket(new ExPledgeBonusOpen(activeChar));
	}
}