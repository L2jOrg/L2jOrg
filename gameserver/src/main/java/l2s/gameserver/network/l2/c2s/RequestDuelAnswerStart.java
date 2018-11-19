package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.data.xml.holder.EventHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Request;
import l2s.gameserver.model.Request.L2RequestType;
import l2s.gameserver.model.entity.events.EventType;
import l2s.gameserver.model.entity.events.impl.AbstractDuelEvent;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;

public class RequestDuelAnswerStart extends L2GameClientPacket
{
	private int _response;
	private int _duelType;

	@Override
	protected void readImpl()
	{
		_duelType = readD();
		readD(); // 1 посылается если ниже  -1(при включеной опции клиента Отменять дуели)
		_response = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		Request request = activeChar.getRequest();
		if(request == null || !request.isTypeOf(L2RequestType.DUEL))
			return;

		if(!request.isInProgress())
		{
			request.cancel();
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.isActionsDisabled())
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

		if(_duelType != request.getInteger("duelType"))
		{
			request.cancel();
			activeChar.sendActionFailed();
			return;
		}

		AbstractDuelEvent duelEvent = EventHolder.getInstance().getEvent(EventType.PVP_EVENT, request.getInteger("eventId", _duelType));

		switch(_response)
		{
			case 0:
				request.cancel();
				if(_duelType == 1)
					requestor.sendPacket(SystemMsg.THE_OPPOSING_PARTY_HAS_DECLINED_YOUR_CHALLENGE_TO_A_DUEL);
				else
					requestor.sendPacket(new SystemMessagePacket(SystemMsg.C1_HAS_DECLINED_YOUR_CHALLENGE_TO_A_PARTY_DUEL).addName(activeChar));
				break;
			case -1:
				request.cancel();
				requestor.sendPacket(new SystemMessagePacket(SystemMsg.C1_IS_SET_TO_REFUSE_DUEL_REQUESTS_AND_CANNOT_RECEIVE_A_DUEL_REQUEST).addName(activeChar));
				break;
			case 1:
				if(!duelEvent.canDuel(requestor, activeChar, false))
				{
					request.cancel();
					return;
				}

				SystemMessagePacket msg1,
				msg2;
				if(_duelType == 1)
				{
					msg1 = new SystemMessagePacket(SystemMsg.YOU_HAVE_ACCEPTED_C1S_CHALLENGE_TO_A_PARTY_DUEL);
					msg2 = new SystemMessagePacket(SystemMsg.S1_HAS_ACCEPTED_YOUR_CHALLENGE_TO_DUEL_AGAINST_THEIR_PARTY);
				}
				else
				{
					msg1 = new SystemMessagePacket(SystemMsg.YOU_HAVE_ACCEPTED_C1S_CHALLENGE_A_DUEL);
					msg2 = new SystemMessagePacket(SystemMsg.C1_HAS_ACCEPTED_YOUR_CHALLENGE_TO_A_DUEL);
				}

				activeChar.sendPacket(msg1.addName(requestor));
				requestor.sendPacket(msg2.addName(activeChar));

				try
				{
					duelEvent.createDuel(requestor, activeChar, request.getInteger("arenaId", 0));
				}
				finally
				{
					request.done();
				}
				break;
		}
	}
}