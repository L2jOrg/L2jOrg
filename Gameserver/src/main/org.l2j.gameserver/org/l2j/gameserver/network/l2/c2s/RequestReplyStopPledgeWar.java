package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Request;
import org.l2j.gameserver.model.Request.L2RequestType;
import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.model.pledge.ClanWar;
import org.l2j.gameserver.model.pledge.ClanWar.ClanWarPeriod;
import org.l2j.gameserver.network.l2.components.SystemMsg;

/**
 * @author GodWorld & reworked by Bonux
**/
public final class RequestReplyStopPledgeWar extends L2GameClientPacket
{
	private int _answer;

	@Override
	protected void readImpl()
	{
		/*String _reqName = */readString();
		_answer = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		Request request = activeChar.getRequest();
		if(request == null || !request.isTypeOf(L2RequestType.CLAN_WAR_STOP))
			return;

		if(!request.isInProgress())
		{
			request.cancel();
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.isOutOfControl())
		{
			request.cancel();
			activeChar.sendActionFailed();
			return;
		}

		Player requestor = request.getRequestor();
		if(requestor == null)
		{
			request.cancel();
			activeChar.sendActionFailed();
			return;
		}

		if(requestor.getRequest() != request)
		{
			request.cancel();
			activeChar.sendActionFailed();
			return;
		}

		if(_answer == 1)
		{
			try
			{
				ClanWar war = requestor.getClan().getClanWar(activeChar.getClan());
				if(war != null)
					war.setPeriod(ClanWarPeriod.PEACE);
			}
			finally
			{
				request.done();
			}
		}
		else
		{
			requestor.sendPacket(SystemMsg.REQUEST_TO_END_WAR_HAS_BEEN_DENIED);
			request.cancel();
		}
	}
}