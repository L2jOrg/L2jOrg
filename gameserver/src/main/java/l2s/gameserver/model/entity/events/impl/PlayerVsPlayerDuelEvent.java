package l2s.gameserver.model.entity.events.impl;

import java.util.List;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.listener.actor.OnAttackListener;
import l2s.gameserver.listener.actor.OnMoveListener;
import l2s.gameserver.listener.actor.player.OnTeleportedListener;
import l2s.gameserver.model.*;
import l2s.gameserver.model.base.TeamType;
import l2s.gameserver.model.entity.events.objects.DuelSnapshotObject;
import l2s.gameserver.network.l2.components.IBroadcastPacket;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExDuelAskStart;
import l2s.gameserver.network.l2.s2c.ExDuelEnd;
import l2s.gameserver.network.l2.s2c.ExDuelReady;
import l2s.gameserver.network.l2.s2c.SocialActionPacket;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.utils.Location;

/**
 * @author VISTALL
 * @date 3:26/29.06.2011
 */
public class PlayerVsPlayerDuelEvent extends DuelEvent
{
	private class DuelListeners implements OnAttackListener, OnMoveListener, OnTeleportedListener
	{
		@Override
		public void onAttack(Creature actor, Creature target)
		{
            if(!actor.containsEvent(PlayerVsPlayerDuelEvent.this) || !target.containsEvent(PlayerVsPlayerDuelEvent.this))
                stopEvent(false);
		}

		@Override
		public void onMove(Creature actor, Location loc)
		{
            checkMoveZone(actor);
		}

		@Override
        public void onTeleported(Player player)
        {
            checkMoveZone(player);
        }

        private void checkMoveZone(Creature actor)
        {
            if(actor.isInZoneBattle() || actor.isInPeaceZone() || actor.isInWater() || actor.isInSiegeZone() || actor.isInZone(Zone.ZoneType.no_restart))
                stopEvent(false);
            else
            {
                for(DuelSnapshotObject d : PlayerVsPlayerDuelEvent.this)
                {
                    Player player = d.getPlayer();
                    if(player != actor && !actor.isInRangeZ(player, 1200))
                        stopEvent(false);
                }
            }
        }
    }

	private DuelListeners _duelListeners = new DuelListeners();

	public PlayerVsPlayerDuelEvent(MultiValueSet<String> set)
	{
		super(set);
	}

	protected PlayerVsPlayerDuelEvent(int id, String name)
	{
		super(id, name);
	}

	@Override
	public void onAddEvent(GameObject o)
	{
		if(o.isPlayer())
			o.getPlayer().addListener(_duelListeners);
	}

	@Override
	public void onRemoveEvent(GameObject o)
	{
		if(o.isPlayer())
			o.getPlayer().removeListener(_duelListeners);
	}

	@Override
	public boolean canDuel(Player player, Player target, boolean first)
	{
        IBroadcastPacket sm = canDuel0(player, target, false);
		if(sm != null)
		{
			player.sendPacket(sm);
			return false;
		}

		sm = canDuel0(target, player, false);
		if(sm != null)
		{
			player.sendPacket(SystemMsg.YOU_ARE_UNABLE_TO_REQUEST_A_DUEL_AT_THIS_TIME);
			return false;
		}

		return true;
	}

	@Override
	public void askDuel(Player player, Player target, int arenaId)
	{
		Request request = new Request(Request.L2RequestType.DUEL, player, target).setTimeout(10000L);
		request.set("duelType", 0);
		player.setRequest(request);
        target.setRequest(request);

		player.sendPacket(new SystemMessagePacket(SystemMsg.C1_HAS_BEEN_CHALLENGED_TO_A_DUEL).addName(target));
        target.sendPacket(new SystemMessagePacket(SystemMsg.C1_HAS_CHALLENGED_YOU_TO_A_DUEL).addName(player), new ExDuelAskStart(player.getName(), 0));
	}

	@Override
	public void createDuel(Player player, Player target, int arenaId)
	{
		PlayerVsPlayerDuelEvent duelEvent = new PlayerVsPlayerDuelEvent(getDuelType(), player.getObjectId() + "_" + target.getObjectId() + "_duel");
		cloneTo(duelEvent);

        duelEvent.addObject(TeamType.BLUE, new DuelSnapshotObject(player, TeamType.BLUE, true));
        player.addEvent(duelEvent);
        duelEvent.addObject(TeamType.RED, new DuelSnapshotObject(target, TeamType.RED, true));
        target.addEvent(duelEvent);
		duelEvent.sendPacket(new ExDuelReady(this));
		duelEvent.reCalcNextTime(false);
	}

	@Override
	public void stopEvent(boolean force)
	{
		if(!_isInProgress)
			return;

        _isInProgress = false;

		clearActions();

		updatePlayers(false, false);

		for(DuelSnapshotObject d : this)
		{
            d.blockUnblock();

			d.getPlayer().sendPacket(new ExDuelEnd(this));
			GameObject target = d.getPlayer().getTarget();
			if(target != null)
				d.getPlayer().getAI().notifyEvent(CtrlEvent.EVT_FORGET_OBJECT, target);
		}

		switch(_winner)
		{
			case NONE:
				sendPacket(SystemMsg.THE_DUEL_HAS_ENDED_IN_A_TIE);
				break;
			case RED:
			case BLUE:
				List<DuelSnapshotObject> winners = getObjects(_winner);
				List<DuelSnapshotObject> lossers = getObjects(_winner.revert());

				if(winners != null && !winners.isEmpty())
					sendPacket(new SystemMessagePacket(SystemMsg.C1_HAS_WON_THE_DUEL).addName(winners.get(0).getPlayer()));

				for(DuelSnapshotObject d : lossers)
					d.getPlayer().broadcastPacket(new SocialActionPacket(d.getPlayer().getObjectId(), SocialActionPacket.BOW));
				break;
		}

        removeObjects(TeamType.RED);
        removeObjects(TeamType.BLUE);
	}

	@Override
	public void onDie(Player player)
	{
		TeamType team = player.getTeam();
		if(team == TeamType.NONE || _aborted)
			return;

        playerLost(player);
	}

	@Override
	public int getDuelType()
	{
		return 0;
	}

	@Override
	public void packetSurrender(Player player)
	{
        playerLost(player);
	}

	@Override
	protected long startTimeMillis()
	{
		return System.currentTimeMillis() + 5000L;
	}
}