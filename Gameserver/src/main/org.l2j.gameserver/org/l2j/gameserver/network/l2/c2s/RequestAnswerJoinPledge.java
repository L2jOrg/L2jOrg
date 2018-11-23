package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Request;
import org.l2j.gameserver.model.Request.L2RequestType;
import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.network.l2.components.IBroadcastPacket;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.SystemMessagePacket;

public class RequestAnswerJoinPledge extends L2GameClientPacket
{
	private int _response;

	@Override
	protected void readImpl()
	{
		_response = availableData() >= 4 ? readInt() : 0;
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if(player == null)
			return;

		answerJoinPledge(player, _response != 0);
	}

	public static void answerJoinPledge(Player player, boolean confirm)
	{
		Request request = player.getRequest();
		if(request == null || !request.isTypeOf(L2RequestType.CLAN))
			return;

		if(!request.isInProgress())
		{
			request.cancel();
			player.sendActionFailed();
			return;
		}

		if(player.isOutOfControl())
		{
			request.cancel();
			player.sendActionFailed();
			return;
		}

		Player requestor = request.getRequestor();
		if(requestor == null)
		{
			request.cancel();
			player.sendPacket(SystemMsg.THAT_PLAYER_IS_NOT_ONLINE);
			player.sendActionFailed();
			return;
		}

		if(requestor.getRequest() != request)
		{
			request.cancel();
			player.sendActionFailed();
			return;
		}

		Clan clan = requestor.getClan();
		if(clan == null)
		{
			request.cancel();
			player.sendActionFailed();
			return;
		}

		if(!confirm)
		{
			request.cancel();
			requestor.sendPacket(new SystemMessagePacket(SystemMsg.S1_DECLINED_YOUR_CLAN_INVITATION).addName(player));
			return;
		}

		if(player.isInTrainingCamp())
		{
			request.cancel();
			player.sendPacket(SystemMsg.YOU_CANNOT_JOIN_A_CLAN_WHILE_YOU_ARE_IN_THE_TRAINING_CAMP);
			return;
		}

		if(!player.canJoinClan())
		{
			request.cancel();
			player.sendPacket(SystemMsg.AFTER_LEAVING_OR_HAVING_BEEN_DISMISSED_FROM_A_CLAN_YOU_MUST_WAIT_AT_LEAST_A_DAY_BEFORE_JOINING_ANOTHER_CLAN);
			return;
		}

		int pledgeType = request.getInteger("pledgeType");

		if(clan.getUnitMembersSize(pledgeType) >= clan.getSubPledgeLimit(pledgeType))
		{
			request.cancel();
			player.sendActionFailed();
			return;
		}

		if(!clan.checkJoinPledgeCondition(player, pledgeType))
		{
			request.cancel();
			player.sendActionFailed();
			return;
		}

		try
		{
			clan.joinInPledge(player, pledgeType);
		}
		finally
		{
			request.done();
		}
	}
}