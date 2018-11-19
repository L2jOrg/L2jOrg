package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.Request;
import l2s.gameserver.model.Request.L2RequestType;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.L2FriendListPacket;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;

public class RequestFriendAddReply extends L2GameClientPacket
{
	private int _response;

	@Override
	protected void readImpl()
	{
		readC();
		_response = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		Request request = activeChar.getRequest();
		if(request == null || !request.isTypeOf(L2RequestType.FRIEND))
			return;

		if(activeChar.isOutOfControl())
		{
			request.cancel();
			activeChar.sendActionFailed();
			return;
		}

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
			activeChar.sendPacket(SystemMsg.THE_USER_WHO_REQUESTED_TO_BECOME_FRIENDS_IS_NOT_FOUND_IN_THE_GAME);
			activeChar.sendActionFailed();
			return;
		}

		if(requestor.getRequest() != request)
		{
			request.cancel();
			activeChar.sendActionFailed();
			return;
		}

		if(_response == 0)
		{
			request.cancel();
			requestor.sendPacket(SystemMsg.YOU_HAVE_FAILED_TO_ADD_A_FRIEND_TO_YOUR_FRIENDS_LIST);
			activeChar.sendActionFailed();
			return;
		}

		try
		{
			requestor.getFriendList().add(activeChar);
			activeChar.getFriendList().add(requestor);

			requestor.sendPacket(SystemMsg.THAT_PERSON_HAS_BEEN_SUCCESSFULLY_ADDED_TO_YOUR_FRIEND_LIST, new SystemMessagePacket(SystemMsg.S1_HAS_JOINED_AS_A_FRIEND).addName(requestor), new L2FriendListPacket(requestor));
			activeChar.sendPacket(SystemMsg.THAT_PERSON_HAS_BEEN_SUCCESSFULLY_ADDED_TO_YOUR_FRIEND_LIST, new SystemMessagePacket(SystemMsg.S1_HAS_JOINED_AS_A_FRIEND).addName(requestor), new L2FriendListPacket(activeChar));
		}
		finally
		{
			request.done();
		}
	}
}