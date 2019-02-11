package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.World;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.L2FriendSayPacket;
import org.l2j.gameserver.utils.Log;

import java.nio.ByteBuffer;

/**
 * Recieve Private (Friend) Message
 * Format: c SS
 * S: Message
 * S: Receiving Player
 */
public class RequestSendL2FriendSay extends L2GameClientPacket
{
	private String _message;
	private String _reciever;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_message = readString(buffer, 2048);
		_reciever = readString(buffer, 16);
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;

		if(activeChar.getNoChannel() != 0)
		{
			if(activeChar.getNoChannelRemained() > 0 || activeChar.getNoChannel() < 0)
			{
				activeChar.sendPacket(SystemMsg.CHATTING_IS_CURRENTLY_PROHIBITED_IF_YOU_TRY_TO_CHAT_BEFORE_THE_PROHIBITION_IS_REMOVED_THE_PROHIBITION_TIME_WILL_INCREASE_EVEN_FURTHER);
				return;
			}
			activeChar.updateNoChannel(0);
		}

		Player targetPlayer = World.getPlayer(_reciever);
		if(targetPlayer == null)
		{
			activeChar.sendPacket(SystemMsg.THAT_PLAYER_IS_NOT_ONLINE);
			return;
		}

		if(targetPlayer.isBlockAll())
		{
			activeChar.sendPacket(SystemMsg.THAT_PERSON_IS_IN_MESSAGE_REFUSAL_MODE);
			return;
		}

		if(!activeChar.getFriendList().contains(targetPlayer.getObjectId()))
			return;

		if(activeChar.canTalkWith(targetPlayer))
		{
			targetPlayer.sendPacket(new L2FriendSayPacket(activeChar.getName(), _reciever, _message));

			Log.LogChat("FRIENDTELL", activeChar.getName(), _reciever, _message);
		}
	}
}