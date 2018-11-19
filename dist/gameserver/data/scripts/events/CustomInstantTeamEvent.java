package events;

import java.util.Iterator;
import java.util.List;

import l2s.commons.collections.JoinedIterator;
import l2s.commons.collections.MultiValueSet;
import l2s.commons.time.cron.SchedulingPattern;
import l2s.commons.util.Rnd;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.data.string.StringsHolder;
import l2s.gameserver.data.xml.holder.InstantZoneHolder;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.listener.PlayerListener;
import l2s.gameserver.listener.actor.player.OnPlayerExitListener;
import l2s.gameserver.listener.actor.player.OnTeleportListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.Zone.ZoneType;
import l2s.gameserver.model.base.TeamType;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.entity.events.EventType;
import l2s.gameserver.model.entity.events.impl.SingleMatchEvent;
import l2s.gameserver.model.entity.events.objects.DuelSnapshotObject;
import l2s.gameserver.model.entity.olympiad.Olympiad;
import l2s.gameserver.network.l2.components.ChatType;
import l2s.gameserver.network.l2.components.HtmlMessage;
import l2s.gameserver.network.l2.components.IBroadcastPacket;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.PlaySoundPacket;
import l2s.gameserver.network.l2.s2c.SayPacket2;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.templates.InstantZone;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Location;

/**
 * @author VISTALL
 * @date 15:10/03.04.2012
 */
public abstract class CustomInstantTeamEvent extends SingleMatchEvent implements Iterable<DuelSnapshotObject>
{
	private class PlayerListeners implements OnPlayerExitListener, OnTeleportListener
	{
		@Override
		public void onPlayerExit(Player player)
		{
			player.setReflection(ReflectionManager.MAIN);

			exitPlayer(player, true);
		}

		@Override
		public void onTeleport(Player player, int x, int y, int z, Reflection reflection)
		{
			if(_state != State.NONE && reflection != _reflection)
				exitPlayer(player, false);
		}

		private void exitPlayer(Player player, boolean exit)
		{
			for(TeamType team : TeamType.VALUES)
			{
				List<DuelSnapshotObject> objects = getObjects(team);

				for(DuelSnapshotObject d : objects)
					if(d.getPlayer() == player)
					{
						if(isInProgress())
							onTeleportOrExit(objects, d, exit);
						else
							objects.remove(d);
						break;
					}
			}

			player.removeListener(_playerListeners);
			player.removeEvent(CustomInstantTeamEvent.this);

			checkForWinner();
		}
	}

	private class EventReflection extends Reflection
	{
		EventReflection(int val)
		{
			super(val);

			init(InstantZoneHolder.getInstance().getInstantZone(getInstantId()));
		}

		@Override
		public void startCollapseTimer(long timeInMillis)
		{}
	}

	public static enum State
	{
		NONE,
		TELEPORT_PLAYERS,
		STARTED
	}

	public static final String REGISTRATION = "registration";

	// times
	private long _startTime;
	private final SchedulingPattern _pattern;
	// rewards
	protected final int _minLevel;
	protected final int _maxLevel;
	protected final boolean _levelMul;
	protected final int[] _rewardItems;
	protected final long[] _rewardCounts;

	private boolean _registrationOver = true;

	protected State _state = State.NONE;
	protected TeamType _winner = TeamType.NONE;
	protected Reflection _reflection = new EventReflection(-getId());

	private PlayerListener _playerListeners = new PlayerListeners();

	protected CustomInstantTeamEvent(MultiValueSet<String> set)
	{
		super(set);
		_pattern = new SchedulingPattern(set.getString("pattern"));
		_minLevel = set.getInteger("min_level");
		_maxLevel = set.getInteger("max_level");
		_rewardItems = set.getIntegerArray("reward_items");
		_rewardCounts = set.getLongArray("reward_counts");
		_levelMul = set.getBool("reward_level_mul", false);
	}

	//region Abstracts
	protected abstract int getInstantId();

	protected abstract Location getTeleportLoc(TeamType team);

	protected abstract void checkForWinner();

	protected abstract boolean canWalkInWaitTime();

	protected abstract void onTeleportOrExit(List<DuelSnapshotObject> objects, DuelSnapshotObject duelSnapshotObject, boolean exit);
	//endregion

	//region Start&Stop and player actions
	@Override
	public void teleportPlayers(String name)
	{
		_registrationOver = true;

		_state = State.TELEPORT_PLAYERS;

		for(TeamType team : TeamType.VALUES)
		{
			List<DuelSnapshotObject> list = getObjects(team);
			for(DuelSnapshotObject object : list)
			{
				Player player = object.getPlayer();
				if(!checkPlayer(player,  false))
				{
					player.sendPacket(new HtmlMessage(0).setFile("events/custom_event_cancel.htm"));
					list.remove(object);
				}
			}
		}

		if(getObjects(TeamType.RED).isEmpty() || getObjects(TeamType.BLUE).isEmpty())
		{
			reCalcNextTime(false);

			announceToPlayersWithValidLevel(getClass().getSimpleName() + ".Cancelled");

			return;
		}

		setRegistrationOver(true); // посылаем мессагу

		for(TeamType team : TeamType.VALUES)
		{
			List<DuelSnapshotObject> objects = getObjects(team);

			for(DuelSnapshotObject object : objects)
			{
				Player player = object.getPlayer();

				if(!canWalkInWaitTime())
					player.getFlags().getFrozen().start();

				object.store();

				player.setStablePoint(object.getReturnLoc());
				player.teleToLocation(getTeleportLoc(team), _reflection);
			}
		}
	}

	@Override
	public void startEvent()
	{
		 _winner = TeamType.NONE;

		for(DuelSnapshotObject object : this)
		{
			Player player = object.getPlayer();
			if(!checkPlayer(player, true))
			{
				removeObject(object.getTeam(), object);

				player.removeEvent(this);

				// если игрок ищо ТПшится - его невозможно ищо раз сТПшить , переносим на арену, ток без отражения
				if(player.isTeleporting())
				{
					//player.setXYZ(object.getLoc().x, object.getLoc().y, object.getLoc().z);  // возможен крит
					player.setReflection(ReflectionManager.MAIN);
					_log.debug("TvT: player teleporting error:", player);
				}
				else
					object.teleportBack();
			}
		}

		if(getObjects(TeamType.RED).isEmpty() || getObjects(TeamType.BLUE).isEmpty())
		{
			_state = State.NONE;

			reCalcNextTime(false);

			announceToPlayersWithValidLevel(getClass().getSimpleName() + ".Cancelled");

			return;
		}

		_state = State.STARTED;

		updatePlayers(true, false);

		sendPackets(PlaySoundPacket.B04_S01, SystemMsg.LET_THE_DUEL_BEGIN);

		super.startEvent();
	}

	@Override
	public void stopEvent(boolean force)
	{
		if(_state != State.STARTED)
			return;

		clearActions();

		_state = State.NONE;

		updatePlayers(false, false);

		switch(_winner)
		{
			case NONE:
				sendPacket(SystemMsg.THE_DUEL_HAS_ENDED_IN_A_TIE);
				break;
			case RED:
			case BLUE:
				List<DuelSnapshotObject> winners = getObjects(_winner);

				sendPacket(new SystemMessagePacket(_winner == TeamType.RED ? SystemMsg.THE_RED_TEAM_IS_VICTORIOUS : SystemMsg.THE_BLUE_TEAM_IS_VICTORIOUS));

				for(DuelSnapshotObject d : winners)
					for(int i = 0; i < _rewardItems.length; i++)
						if(d.getPlayer() != null)
							ItemFunctions.addItem(d.getPlayer(), _rewardItems[i], _levelMul ? _rewardCounts[i] * d.getPlayer().getLevel() : _rewardCounts[i]);
				break;
		}

		updatePlayers(false, true);
		removeObjects(TeamType.RED);
		removeObjects(TeamType.BLUE);

		reCalcNextTime(false);

		super.stopEvent(force);
	}

	protected void updatePlayers(boolean start, boolean teleport)
	{
		for(DuelSnapshotObject $snapshot : this)
		{
			if($snapshot.getPlayer() == null)
				continue;

			if(teleport)
				$snapshot.teleportBack();
			else
			{
				Player $player = $snapshot.getPlayer();
				if(start)
				{
					$player.getFlags().getFrozen().stop();
					$player.setTeam($snapshot.getTeam());

					$player.setCurrentMp($player.getMaxMp());
					$player.setCurrentCp($player.getMaxCp());
					$player.setCurrentHp($player.getMaxHp(), true);
				}
				else
				{
					$player.getFlags().getFrozen().start();
					$player.removeEvent(this);

					GameObject target = $player.getTarget();
					if(target != null)
						$player.getAI().notifyEvent(CtrlEvent.EVT_FORGET_OBJECT, target);

					$snapshot.restore();
					$player.setTeam(TeamType.NONE);
				}

				actionUpdate(start, $player);
			}
		}
	}

	protected void actionUpdate(boolean start, Player player)
	{}
	//endregion

	//region Broadcast
	@Override
	public void sendPacket(IBroadcastPacket packet)
	{
		sendPackets(packet);
	}

	@Override
	public void sendPackets(IBroadcastPacket... packet)
	{
		for(DuelSnapshotObject d : this)
			if(d.getPlayer() != null)
				d.getPlayer().sendPacket(packet);
	}

	public void sendPacket(IBroadcastPacket packet, TeamType... ar)
	{
		for(TeamType a : ar)
		{
			List<DuelSnapshotObject> objs = getObjects(a);

			for(DuelSnapshotObject d : objs)
				if(d.getPlayer() != null)
					d.getPlayer().sendPacket(packet);
		}
	}
	//endregion

	//region Registration
	private boolean checkPlayer(Player player, boolean second)
	{
		if(player.isInOfflineMode())
			return false;

		if(player.getLevel() > _maxLevel || player.getLevel() < _minLevel)
			return false;

		if(player.isMounted() || player.isDead() || player.isInObserverMode())
			return false;

		final SingleMatchEvent evt = player.getEvent(SingleMatchEvent.class);
		if(evt != null && evt != this)
			return false;

		if(player.getTeam() != TeamType.NONE)
			return false;

		if(player.getOlympiadGame() != null || Olympiad.isRegistered(player))
			return false;

		if(player.isTeleporting())
			return false;

		if(!second)
		{
			if(!player.getReflection().isMain())
				return false;

			if(player.isInZone(ZoneType.epic))
				return false;
		}

		return true;
	}

	public boolean registerPlayer(Player player)
	{
		if(_registrationOver)
			return false;
		for(DuelSnapshotObject d : this)
			if(d.getPlayer() == player)
				return false;

		if (!checkPlayer(player, false))
			return false;

		List<DuelSnapshotObject> blue = getObjects(TeamType.BLUE);
		List<DuelSnapshotObject> red = getObjects(TeamType.RED);
		TeamType team;
		if(blue.size() == red.size())
			team = Rnd.get(TeamType.VALUES);
		else if(blue.size() > red.size())
			team = TeamType.RED;
		else
			team = TeamType.BLUE;

		addObject(team, new DuelSnapshotObject(player, team, false));

		player.addEvent(this);
		player.addListener(_playerListeners);
		return true;
	}

	protected void announceToPlayersWithValidLevel(String str)
	{
		for(Player player : GameObjectsStorage.getPlayers())
			if(player.isGM() || player.getLevel() >= _minLevel && player.getLevel() <= _maxLevel)
				player.sendPacket(new SayPacket2(0, ChatType.ANNOUNCEMENT, "", String.format(StringsHolder.getInstance().getString(str, player), _minLevel, _maxLevel)));
	}

	@Override
	public boolean isInProgress()
	{
		return _state == State.STARTED;
	}

	@Override
	public void action(String name, boolean start)
	{
		if(name.equalsIgnoreCase(REGISTRATION))
			setRegistrationOver(!start);
		else
			super.action(name, start);
	}

	public boolean isRegistrationOver()
	{
		return _registrationOver;
	}

	public void setRegistrationOver(boolean registrationOver)
	{
		_registrationOver = registrationOver;

		announceToPlayersWithValidLevel(getClass().getSimpleName() + (_registrationOver ? ".RegistrationIsClose" : ".RegistrationIsOpen"));
	}
	//endregion

	//region Implementation & Override
	@Override
	public void initEvent()
	{
		super.initEvent();

		InstantZone instantZone = InstantZoneHolder.getInstance().getInstantZone(500);

		_reflection.init(instantZone);
	}

	@Override
	public void reCalcNextTime(boolean onInit)
	{
		_reflection.clearVisitors();

		clearActions();

		_startTime = _pattern.next(System.currentTimeMillis() + 60000L);

		registerActions();
	}

	@Override
	public SystemMsg checkForAttack(Creature target, Creature attacker, Skill skill, boolean force)
	{
		if(!canAttack(target, attacker, null, force, false))
			return SystemMsg.INVALID_TARGET;

		return null;
	}

	@Override
	public boolean canAttack(Creature target, Creature attacker, Skill skill, boolean force, boolean nextAttackCheck)
	{
		if(_state != State.STARTED || target.getTeam() == TeamType.NONE || attacker.getTeam() == TeamType.NONE || target.getTeam() == attacker.getTeam())
			return false;

		return true;
	}

	@Override
	public Iterator<DuelSnapshotObject> iterator()
	{
		List<DuelSnapshotObject> blue = getObjects(TeamType.BLUE);
		List<DuelSnapshotObject> red = getObjects(TeamType.RED);
		return new JoinedIterator<DuelSnapshotObject>(blue.iterator(), red.iterator());
	}

	@Override
	protected long startTimeMillis()
	{
		return _startTime;
	}

	@Override
	public EventType getType()
	{
		return EventType.CUSTOM_PVP_EVENT;
	}

	@Override
	public void announce(SystemMsg msgId, int a, int time)
	{
		switch(time)
		{
			case -10:
			case -5:
			case -4:
			case -3:
			case -2:
			case -1:
				sendPacket(new SystemMessagePacket(SystemMsg.THE_DUEL_WILL_BEGIN_IN_S1_SECONDS).addInteger(-time));
				break;
		}
	}

	@Override
	public void onRemoveEvent(GameObject o)
	{
		if(o.isPlayer())
			o.getPlayer().removeListener(_playerListeners);
	}

	@Override
	public Reflection getReflection()
	{
		return _reflection;
	}
	//endregion
}
