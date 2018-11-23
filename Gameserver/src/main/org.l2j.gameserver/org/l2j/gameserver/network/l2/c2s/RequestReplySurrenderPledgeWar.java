package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Request;
import org.l2j.gameserver.model.Request.L2RequestType;
import org.l2j.gameserver.model.pledge.ClanWar;
import org.l2j.gameserver.model.pledge.ClanWar.ClanWarPeriod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author GodWorld & reworked by Bonux
**/
public final class RequestReplySurrenderPledgeWar extends L2GameClientPacket
{
	private static final Logger _log = LoggerFactory.getLogger(RequestReplySurrenderPledgeWar.class);

	private String _reqName;
	private int _answer;

	@Override
	protected void readImpl()
	{
		_reqName = readString();
		_answer = readInt();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		Request request = activeChar.getRequest();
		if(request == null || !request.isTypeOf(L2RequestType.CLAN_WAR_SURRENDER))
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
				//requestor.deathPenalty(false, false, false);

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
			_log.warn(getClass().getSimpleName() + ": Missing implementation for answer: " + _answer + " and name: " + _reqName + "!");
			request.cancel();
		}
	}
}