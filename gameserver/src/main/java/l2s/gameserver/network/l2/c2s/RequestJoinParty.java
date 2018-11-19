package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.Config;
import l2s.gameserver.model.Party;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Request;
import l2s.gameserver.model.Request.L2RequestType;
import l2s.gameserver.model.World;
import l2s.gameserver.model.entity.events.impl.PvPEvent;
import l2s.gameserver.network.l2.components.IBroadcastPacket;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.AskJoinPartyPacket;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;

public class RequestJoinParty extends L2GameClientPacket
{
	private String _name;
	private int _itemDistribution;

	@Override
	protected void readImpl()
	{
		_name = readS(Config.CNAME_MAXLEN);
		_itemDistribution = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(activeChar.isOutOfControl())
		{
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.isProcessingRequest())
		{
			activeChar.sendPacket(SystemMsg.WAITING_FOR_ANOTHER_REPLY);
			return;
		}

		if(activeChar.isPartyBlocked())
		{
			activeChar.sendPacket(SystemMsg.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_PARTICIPATING_IN_A_PARTY_IS_NOT_ALLOWED);
			return;
		}

		Player target = World.getPlayer(_name);
		if(target == null)
		{
			activeChar.sendPacket(SystemMsg.THAT_PLAYER_IS_NOT_ONLINE);
			return;
		}

		if(target == activeChar)
		{
			activeChar.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
			activeChar.sendActionFailed();
			return;
		}

		for(PvPEvent event : activeChar.getEvents(PvPEvent.class))
		{
			if(!event.canJoinParty(activeChar, target))
			{
				activeChar.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
				activeChar.sendActionFailed();
				return;
			}
		}

		if(target.isBusy())
		{
			activeChar.sendPacket(new SystemMessagePacket(SystemMsg.C1_IS_ON_ANOTHER_TASK).addName(target));
			return;
		}

		if(target.isInTrainingCamp())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_REQUEST_TO_A_CHARACTER_WHO_IS_ENTERING_THE_TRAINING_CAMP);
			return;
		}

		IBroadcastPacket problem = target.canJoinParty(activeChar);
		if(problem != null)
		{
			activeChar.sendPacket(problem);
			return;
		}

		if(activeChar.isInParty())
		{
			if(activeChar.getParty().getMemberCount() >= Party.MAX_SIZE)
			{
				activeChar.sendPacket(SystemMsg.THE_PARTY_IS_FULL);
				return;
			}

			// Только Party Leader может приглашать новых членов
			if(Config.PARTY_LEADER_ONLY_CAN_INVITE && !activeChar.getParty().isLeader(activeChar))
			{
				activeChar.sendPacket(SystemMsg.ONLY_THE_LEADER_CAN_GIVE_OUT_INVITATIONS);
				return;
			}
		}

		new Request(L2RequestType.PARTY, activeChar, target).setTimeout(10000L).set("itemDistribution", _itemDistribution);

		target.sendPacket(new AskJoinPartyPacket(activeChar.getName(), _itemDistribution));
		activeChar.sendPacket(new SystemMessagePacket(SystemMsg.C1_HAS_BEEN_INVITED_TO_THE_PARTY).addName(target));
	}
}