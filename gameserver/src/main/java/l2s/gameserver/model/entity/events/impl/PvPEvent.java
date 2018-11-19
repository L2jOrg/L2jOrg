package l2s.gameserver.model.entity.events.impl;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicBoolean;

import l2s.commons.collections.MultiValueSet;
import l2s.commons.time.cron.SchedulingPattern;
import l2s.commons.util.Rnd;
import l2s.gameserver.Announcements;
import l2s.gameserver.Config;
import l2s.gameserver.listener.actor.player.impl.EventAnswerListner;
import l2s.gameserver.listener.hooks.ListenerHookType;
import l2s.gameserver.model.*;
import l2s.gameserver.model.base.RestartType;
import l2s.gameserver.model.base.TeamType;
import l2s.gameserver.model.entity.events.EventType;
import l2s.gameserver.model.entity.events.hooks.PvPEventHook;
import l2s.gameserver.model.entity.events.objects.PvPEventArenaObject;
import l2s.gameserver.model.entity.events.objects.PvPEventPlayerObject;
import l2s.gameserver.model.entity.olympiad.Olympiad;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ConfirmDlgPacket;
import l2s.gameserver.skills.AbnormalEffect;
import l2s.gameserver.stats.conditions.Condition;
import l2s.gameserver.stats.conditions.ConditionPlayerOlympiad;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.velocity.VelocityUtils;

public class PvPEvent extends SingleMatchEvent
{
	private AtomicBoolean isEventActive = new AtomicBoolean(false);
	protected AtomicBoolean isRegActive = new AtomicBoolean(false);
	protected AtomicBoolean isBattleActive = new AtomicBoolean(false);

	private final Calendar _calendar = Calendar.getInstance();
	private SchedulingPattern datePattern;

	private final CustomMessage _name;
	private final int _minLevel;
	private final int _maxLevel;
	private final int _minPlayers;
	private final int _teams;
	private final int _countDieFromExit;
	private final int _minKillFromReward;
	private final int _minKillTeamFromReward;
	private final boolean _hideNick;
	private final boolean _incPvp;
	private final int _modRewardForPremium;
	private final int[][] _buffs;
	private final boolean _disableHeroAndClanSkills;
	private final boolean _resetSkills;
	private final boolean _enableHeroCond;
	private final boolean _addHeroLastPlayer;

	public PvPEvent(MultiValueSet<String> set)
	{
		super(set);

		if(set.getBool("enabled", false))
		{
			String cron = set.getString("start_time", "");
			if(!cron.isEmpty())
				datePattern = new SchedulingPattern(cron);
		}
		_minLevel = Math.max(1, set.getInteger("min_level", 1));
		_maxLevel = Math.min(Config.ALT_MAX_LEVEL, set.getInteger("max_level", Config.ALT_MAX_LEVEL));

		VelocityUtils.GLOBAL_VARIABLES.put("PVP_EVENT_" + getId() + "_MIN_LEVEL", _minLevel);
		VelocityUtils.GLOBAL_VARIABLES.put("PVP_EVENT_" + getId() + "_MAX_LEVEL", _maxLevel);

		_name = new CustomMessage("l2s.gameserver.model.entity.events.impl.PvPEvent.eventname." + getId());
		_minPlayers = set.getInteger("min_players", 1);
		_teams = set.getInteger("teams");
		_countDieFromExit = set.getInteger("count_die_from_exit");
		_minKillFromReward = set.getInteger("min_kill_from_reward", 0);
		_minKillTeamFromReward = set.getInteger("min_kill_team_from_reward", 0);
		_hideNick = set.getBool("hide_nick", false);
		_incPvp = set.getBool("inc_pvp", false);
		_modRewardForPremium = set.getInteger("mod_reward_for_premium", 1);
		_buffs = parseBuffs(set.getString("buffs", ""));
		_disableHeroAndClanSkills = set.getBool("disable_hero_and_clan_skills", true);
		_resetSkills = set.getBool("reset_skills", true);
		_enableHeroCond = set.getBool("enable_hero_cond", true);
		_addHeroLastPlayer = set.getBool("add_hero_last_player", false);
	}

	public CustomMessage getEventName()
	{
		return _name;
	}

	public int getMinLevel()
	{
		return _minLevel;
	}

	public int getMaxLevel()
	{
		return _maxLevel;
	}

	public int getTeams()
	{
		return _teams;
	}

	public int getCountDieFromExit()
	{
		return _countDieFromExit;
	}

	public int getMinKillFromReward()
	{
		return _minKillFromReward;
	}

	public int getMinKillTeamFromReward()
	{
		return _minKillTeamFromReward;
	}

	public boolean isHideNick()
	{
		return _hideNick;
	}

	public boolean isIncPvP()
	{
		return _incPvp;
	}

	public int getModRewardForPremium()
	{
		return _modRewardForPremium;
	}

	public int[][] getBuffs()
	{
		return _buffs;
	}

	public boolean isDisableHeroAndClanSkills()
	{
		return _disableHeroAndClanSkills;
	}

	public boolean isResetSkills()
	{
		return _resetSkills;
	}

	public boolean isAddHeroLastPlayer()
	{
		return _addHeroLastPlayer;
	}

	public boolean isRegActive()
	{
		return isRegActive.get();
	}

	public boolean isBattleActive()
	{
		return isBattleActive.get();
	}

	@Override
	public void reCalcNextTime(boolean onInit)
	{
		if(datePattern != null)
		{
			clearActions();
			_calendar.setTimeInMillis(datePattern.next(System.currentTimeMillis()));
			registerActions();

			if(!onInit)
				printInfo();
		}
	}

	@Override
	public EventType getType()
	{
		return EventType.CUSTOM_PVP_EVENT;
	}

	@Override
	protected long startTimeMillis()
	{
		return _calendar.getTimeInMillis();
	}

	@Override
	public boolean isInProgress()
	{
		return isEventActive.get();
	}

	@Override
	public void startEvent()
	{
		if(!isEventActive.compareAndSet(false, true))
			return;

		super.startEvent();
		clearActions();
		_calendar.setTimeInMillis(System.currentTimeMillis() + 1000);
		registerActions();
	}

	@Override
	public void stopEvent(boolean force)
	{
		if(force)
			action("battle", false);

		isEventActive.set(false);
		removeObjects("registered_players");

		if(!force)
			reCalcNextTime(false);
	}

	public void action(String name, boolean start)
	{
		switch(name)
		{
			case "registration":
			{
				if(start)
				{
					isRegActive.set(true);
					Announcements.announceToAllFromStringHolder("l2s.gameserver.model.entity.events.impl.PvPEvent.registration.start", getEventName());

					CustomMessage askMessage = new CustomMessage("l2s.gameserver.model.entity.events.impl.PvPEvent.registration.ask").addCustomMessage(getEventName());

					int count = 0;
					for(Player player : GameObjectsStorage.getPlayers())
					{
						if(player.getLevel() >= getMinLevel() && player.getLevel() <= getMaxLevel())
							player.ask(new ConfirmDlgPacket(SystemMsg.S1, 60000).addString(askMessage.toString(player)), new EventAnswerListner(player, getId()));
					}
				}
				else
				{
					isRegActive.set(false);

					List<Player> registeredPlayers = getObjects("registered_players");
					if(registeredPlayers.size() < _minPlayers)
					{
						Announcements.announceToAllFromStringHolder("l2s.gameserver.model.entity.events.impl.PvPEvent.registration.cancel", getEventName());
						stopEvent(false);
					}
					else
						Announcements.announceToAllFromStringHolder("l2s.gameserver.model.entity.events.impl.PvPEvent.registration.stop", getEventName());
				}
			}
			break;
			case "sort":
			{
				PvPEventArenaObject arena = new PvPEventArenaObject(this, _teams);
				addObject("arenas", arena);
				List<Player> registeredPlayers = getObjects("registered_players");
				arena.sortPlayers(registeredPlayers);
				removeObjects("registered_players");
			}
			break;
			case "teleport":
			{
				isBattleActive.set(true);
				List<PvPEventArenaObject> arenas = getObjects("arenas");
				for(PvPEventArenaObject arena : arenas)
					arena.teleportPlayers();
			}
			break;
			case "battle":
			{
				List<PvPEventArenaObject> arenas = getObjects("arenas");
				for(PvPEventArenaObject arena : arenas)
				{
					if(start)
						arena.startBattle();
					else
						arena.stopBattle();
				}
				if(!start)
				{
					isBattleActive.set(false);
					stopEvent(false);
				}
			}
			break;
			default:
				super.action(name, start);
		}
	}

	private boolean checkReg(Player player)
	{
		if(player.getLevel() > getMaxLevel() || player.getLevel() < getMinLevel())
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

		if(!player.getReflection().isMain())
			return false;

		if(player.isInZone(Zone.ZoneType.epic))
			return false;

		return true;
	}

	public void showReg()
	{
		for(Player player : GameObjectsStorage.getPlayers())
		{
			if(isRegActive())
			{
				if(isRegistered(player))
					Functions.show("events/event_yesreg.htm", player);
				else if(checkReg(player))
					Functions.show("events/event_" + getId() + ".htm", player);
			}
			else
				Functions.show("events/event_noreg.htm", player);
		}
	}

	public void reg(Player player)
	{
		if(isRegActive.get() && checkReg(player))
		{
			addObject("registered_players", player);
			player.addListenerHook(ListenerHookType.PLAYER_QUIT_GAME, PvPEventHook.getInstance());
			player.sendMessage(new CustomMessage("l2s.gameserver.model.entity.events.impl.PvPEvent.registration.success").addCustomMessage(getEventName()));
		}
	}

	public void regCustom(Player player, String command)
	{
		//
	}

	public Location getLocation(String teleportWho)
	{
		List<Location> teleportList = getObjects(teleportWho);
		return teleportList.get(Rnd.get(0, teleportList.size() - 1));
	}

	public void abnormals(Player player, boolean start)
	{
		PvPEventPlayerObject member = getParticipant(player);
		if(member == null)
			return;

		int teamId = member.getTeam();
		if(teamId == -1)
			teamId = 0;

		List<AbnormalEffect> abnormalEffects = getObjects("abnormal" + teamId);
		for(AbnormalEffect abnormalEffect : abnormalEffects)
		{
			if(start)
				player.startAbnormalEffect(abnormalEffect);
			else
				player.stopAbnormalEffect(abnormalEffect);
		}
	}

	@Override
	public SystemMsg checkForAttack(Creature target, Creature attacker, Skill skill, boolean force)
	{
		if(!isEnemy(target, attacker))
			return SystemMsg.INVALID_TARGET;
		return null;
	}

	@Override
	public boolean canAttack(Creature target, Creature attacker, Skill skill, boolean force, boolean nextAttackCheck)
	{
		return isEnemy(target, attacker);
	}

	private boolean isEnemy(Creature target, Creature attacker)
	{
		PvPEventPlayerObject attackerMember = getParticipant(attacker.getPlayer());
		if(attackerMember == null)
			return false;
		PvPEventPlayerObject targetMember = getParticipant(target.getPlayer());
		if(targetMember == null)
			return false;
		return attackerMember.getTeam() == -1 || attackerMember.getTeam() != targetMember.getTeam();
	}

	public boolean isRegistered(Player player)
	{
		List<Player> players = getObjects("registered_players");
		for(Player temp : players)
		{
			if(player.equals(temp))
				return true;
		}
		return false;
	}

	public PvPEventArenaObject getArena(Player player)
	{
		List<PvPEventArenaObject> arenas = getObjects("arenas");
		for(PvPEventArenaObject arena : arenas)
		{
			if(arena.getParticipant(player) != null)
				return arena;
		}
		return null;
	}

	public PvPEventPlayerObject getParticipant(Player player)
	{
		PvPEventArenaObject arena = getArena(player);
		if(arena != null)
			return arena.getParticipant(player);
		return null;
	}

	@Override
	public String getVisibleName(Player player, Player observer)
	{
		if(player != observer)
		{
			if(isBattleActive() && isHideNick())
				return new CustomMessage("l2s.gameserver.model.entity.events.impl.PvPEvent.playername").toString(observer);
		}
		return null;
	}

	@Override
	public String getVisibleTitle(Player player, Player observer)
	{
		if(player != observer)
		{
			if(isBattleActive() && isHideNick())
				return "";
		}
		return null;
	}

	@Override
	public Integer getVisibleNameColor(Player player, Player observer)
	{
		if(player != observer)
		{
			if(isBattleActive() && isHideNick())
				return Player.DEFAULT_NAME_COLOR;
		}
		return null;
	}

	@Override
	public Integer getVisibleTitleColor(Player player, Player observer)
	{
		if(player != observer)
		{
			if(isBattleActive() && isHideNick())
				return Player.DEFAULT_TITLE_COLOR;
		}
		return null;
	}

	@Override
	public boolean isPledgeVisible(Player player, Player observer)
	{
		if(player != observer)
		{
			if(isBattleActive() && isHideNick())
				return false;
		}
		return true;
	}

	@Override
	public boolean checkCondition(Creature creature, Class<? extends Condition> conditionClass)
	{
		if(isBattleActive() && _enableHeroCond)
		{
			if(conditionClass == ConditionPlayerOlympiad.class)
				return false;
			if(conditionClass.isAssignableFrom(ConditionPlayerOlympiad.class))
				return false;
		}
		return true;
	}

	@Override
	public boolean isInZoneBattle(Creature creature)
	{
		return isBattleActive();
	}

	public boolean checkStop()
	{
		return true;
	}

	public boolean canJoinParty(Player player1, Player player2)
	{
		return false;
	}

	private int[][] parseBuffs(String buffs)
	{
		if(buffs == null || buffs.isEmpty())
			return new int[][] {};

		StringTokenizer st = new StringTokenizer(buffs, ";");
		int[][] realBuffs = new int[st.countTokens()][2];
		int index = 0;
		while(st.hasMoreTokens())
		{
			String[] skillLevel = st.nextToken().split(",");
			int[] realHourMin = {Integer.parseInt(skillLevel[0]), Integer.parseInt(skillLevel[1])};
			realBuffs[index] = realHourMin;
			index++;
		}
		return realBuffs;
	}

	@Override
	public void checkRestartLocs(Player player, Map<RestartType, Boolean> r)
	{
		r.clear();
	}
}