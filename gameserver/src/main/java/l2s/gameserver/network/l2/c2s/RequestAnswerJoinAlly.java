package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.Request;
import l2s.gameserver.model.Request.L2RequestType;
import l2s.gameserver.model.pledge.Alliance;
import l2s.gameserver.network.l2.components.SystemMsg;

/**
 *  format  c(d)
 */
public class RequestAnswerJoinAlly extends L2GameClientPacket
{
	private int _response;

	@Override
	protected void readImpl()
	{
		_response = _buf.remaining() >= 4 ? readD() : 0;
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
		
		Request request = activeChar.getRequest();
		if(request == null || !request.isTypeOf(L2RequestType.ALLY))
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
			activeChar.sendPacket(SystemMsg.THAT_PLAYER_IS_NOT_ONLINE);
			activeChar.sendActionFailed();
			return;
		}

		if(requestor.getRequest() != request)
		{
			request.cancel();
			activeChar.sendActionFailed();
			return;
		}

		if(requestor.getAlliance() == null)
		{
			request.cancel();
			activeChar.sendActionFailed();
			return;
		}

		if(_response == 0)
		{
			request.cancel();
			requestor.sendPacket(SystemMsg.YOU_HAVE_FAILED_TO_INVITE_A_CLAN_INTO_THE_ALLIANCE);
			return;
		}

		try
		{
			Alliance ally = requestor.getAlliance();
			activeChar.sendPacket(SystemMsg.YOU_HAVE_ACCEPTED_THE_ALLIANCE);
			activeChar.getClan().setAllyId(requestor.getAllyId());
			activeChar.getClan().updateClanInDB();
			ally.addAllyMember(activeChar.getClan(), true);
			ally.broadcastAllyStatus();
		}
		finally
		{
			request.done();
		}
	}
}