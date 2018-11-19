package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.CommandChannel;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Request;
import l2s.gameserver.model.Request.L2RequestType;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;

public class RequestExMPCCAcceptJoin extends L2GameClientPacket
{
	@SuppressWarnings("unused")
	private int _response, _unk;

	/*
	 * format: chdd
	 */
	@Override
	protected void readImpl()
	{
		_response = _buf.hasRemaining() ? readD() : 0;
		_unk = _buf.hasRemaining() ? readD() : 0;
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		Request request = activeChar.getRequest();
		if(request == null || !request.isTypeOf(L2RequestType.CHANNEL))
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

		if(_response == 0)
		{
			request.cancel();
			requestor.sendPacket(new SystemMessagePacket(SystemMsg.C1_HAS_DECLINED_THE_CHANNEL_INVITATION).addName(activeChar));
			return;
		}

		if(!requestor.isInParty() || !activeChar.isInParty() || activeChar.getParty().isInCommandChannel())
		{
			request.cancel();
			requestor.sendPacket(SystemMsg.NO_USER_HAS_BEEN_INVITED_TO_THE_COMMAND_CHANNEL);
			return;
		}

		if(activeChar.isTeleporting())
		{
			request.cancel();
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_JOIN_A_COMMAND_CHANNEL_WHILE_TELEPORTING);
			requestor.sendPacket(SystemMsg.NO_USER_HAS_BEEN_INVITED_TO_THE_COMMAND_CHANNEL);
			return;
		}

		try
		{
			if(requestor.getParty().isInCommandChannel())
				requestor.getParty().getCommandChannel().addParty(activeChar.getParty());
			else if(CommandChannel.checkAuthority(requestor))
			{
				// CC можно создать, если есть клановый скилл Clan Imperium
				boolean haveSkill = requestor.getSkillLevel(CommandChannel.CLAN_IMPERIUM_ID) > 0;
				boolean haveItem = false;
				// Скила нету, придется расходовать предмет, ищем Strategy Guide в инвентаре
				if(!haveSkill)
				{
					if(haveItem = requestor.getInventory().destroyItemByItemId(CommandChannel.STRATEGY_GUIDE_ID, 1))
						requestor.sendPacket(SystemMessagePacket.removeItems(CommandChannel.STRATEGY_GUIDE_ID, 1));
				}

				if(!haveSkill && !haveItem)
				{
					//TODO [G1ta0] сообщение
					return;
				}

				CommandChannel channel = new CommandChannel(requestor); // Создаём Command Channel
				requestor.sendPacket(SystemMsg.THE_COMMAND_CHANNEL_HAS_BEEN_FORMED);
				channel.addParty(activeChar.getParty()); // Добавляем приглашенную партию
			}
		}
		finally
		{
			request.done();
		}
	}
}