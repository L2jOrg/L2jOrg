package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.ai.PlayableAI;
import org.l2j.gameserver.geodata.GeoEngine;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Request;
import org.l2j.gameserver.model.Request.L2RequestType;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.SystemMessagePacket;
import org.l2j.gameserver.utils.Location;

import java.nio.ByteBuffer;

public class AnswerCoupleAction extends L2GameClientPacket
{
	private int _charObjId;
	private int _actionId;
	private int _answer;

	@Override
	protected void readImpl(ByteBuffer buffer) {
		_actionId = buffer.getInt();
		_answer = buffer.getInt();
		_charObjId = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;

		Request request = activeChar.getRequest();
		if(request == null || !request.isTypeOf(L2RequestType.COUPLE_ACTION))
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

		if(requestor.getObjectId() != _charObjId || requestor.getRequest() != request)
		{
			request.cancel();
			activeChar.sendActionFailed();
			return;
		}

		switch(_answer)
		{
			case -1: // refused
				requestor.sendPacket(new SystemMessagePacket(SystemMsg.C1_IS_SET_TO_REFUSE_COUPLE_ACTIONS_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION).addName(activeChar));
				request.cancel();
				break;
			case 0: // cancel
				activeChar.sendPacket(SystemMsg.THE_COUPLE_ACTION_WAS_DENIED);
				requestor.sendPacket(SystemMsg.THE_COUPLE_ACTION_WAS_CANCELLED);
				requestor.sendActionFailed();
				request.cancel();
				break;
			case 1: // ok
				try
				{
					if(!checkCondition(activeChar, requestor) || !checkCondition(requestor, activeChar))
						return;

					Location loc = requestor.applyOffset(activeChar.getLoc(), 25);
					loc = GeoEngine.moveCheck(requestor.getX(), requestor.getY(), requestor.getZ(), loc.x, loc.y, requestor.getGeoIndex());
					requestor.moveToLocation(loc, 0, false);
					requestor.getAI().setNextAction(PlayableAI.AINextAction.COUPLE_ACTION, activeChar, _actionId, true, false);
				}
				finally
				{
					request.done();
				}
				break;
		}
	}

	private static boolean checkCondition(Player activeChar, Player requestor)
	{
		if(!activeChar.isInRange(requestor, 300) || activeChar.isInRange(requestor, 25) || !GeoEngine.canSeeTarget(activeChar, requestor, false))
		{
			activeChar.sendPacket(SystemMsg.THE_REQUEST_CANNOT_BE_COMPLETED_BECAUSE_THE_TARGET_DOES_NOT_MEET_LOCATION_REQUIREMENTS);
			return false;
		}
		return activeChar.checkCoupleAction(requestor);
	}
}
