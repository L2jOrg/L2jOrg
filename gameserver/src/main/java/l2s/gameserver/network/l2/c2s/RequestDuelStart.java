package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.EventHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.World;
import l2s.gameserver.model.entity.events.EventType;
import l2s.gameserver.model.entity.events.impl.DuelEvent;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;

public class RequestDuelStart extends L2GameClientPacket
{
	private String _name;
	private int _duelType;

	@Override
	protected void readImpl()
	{
		_name = readS(Config.CNAME_MAXLEN);
		_duelType = readD();
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if(player == null)
			return;

		if(player.isActionsDisabled())
		{
			player.sendActionFailed();
			return;
		}

		if(player.isProcessingRequest())
		{
			player.sendPacket(SystemMsg.WAITING_FOR_ANOTHER_REPLY);
			return;
		}

		DuelEvent duelEvent = EventHolder.getInstance().getEvent(EventType.PVP_EVENT, _duelType);
		if(duelEvent == null)
			return;

		Player target = World.getPlayer(_name);
		if(target == null || target == player)
		{
			player.sendPacket(SystemMsg.THERE_IS_NO_OPPONENT_TO_RECEIVE_YOUR_CHALLENGE_FOR_A_DUEL);
			return;
		}

		if(!duelEvent.canDuel(player, target, true))
			return;
		
		if(target.isBusy())
		{
			player.sendPacket(new SystemMessagePacket(SystemMsg.C1_IS_ON_ANOTHER_TASK).addName(target));
			return;
		}

		duelEvent.askDuel(player, target, 0);
	}
}