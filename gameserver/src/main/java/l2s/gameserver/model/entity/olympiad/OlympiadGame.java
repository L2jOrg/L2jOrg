package l2s.gameserver.model.entity.olympiad;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import l2s.gameserver.Config;
import l2s.gameserver.model.ObservableArena;
import l2s.commons.util.Rnd;
import l2s.gameserver.data.xml.holder.InstantZoneHolder;
import l2s.gameserver.instancemanager.OlympiadHistoryManager;
import l2s.gameserver.model.ObservePoint;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.TeamType;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.DoorInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.IBroadcastPacket;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.*;
import l2s.gameserver.templates.InstantZone;
import l2s.gameserver.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OlympiadGame extends ObservableArena
{
	private static final Logger _log = LoggerFactory.getLogger(OlympiadGame.class);

	public static final int MAX_POINTS_LOOSE = 10;

	public boolean validated = false;

	private int _winner = 0;
	private int _state = 0;

	private int _id;
	private Reflection _reflection;
	private CompType _type;

	private OlympiadMember _member1;
	private OlympiadMember _member2;

	private long _startTime;

	public OlympiadGame(int id, CompType type, int member1, int member2)
	{
		_type = type;
		_id = id;
		_reflection = new Reflection();
		InstantZone instantZone = InstantZoneHolder.getInstance().getInstantZone(Rnd.get(new int[] { 147, 149, 150 }));
		_reflection.init(instantZone);

		_member1 = new OlympiadMember(member1, this, 1);
		_member2 = new OlympiadMember(member2, this, 2);

		Log.add("Olympiad System: Game - " + id + ": " + _member1.getName() + " vs " + _member2.getName(), "olympiad");
	}

	public void addBuffers()
	{
		_reflection.spawnByGroup("olympiad_" + _reflection.getInstancedZoneId() + "_buffers");
	}

	public void deleteBuffers()
	{
		_reflection.despawnByGroup("olympiad_" + _reflection.getInstancedZoneId() + "_buffers");
	}

	public void managerShout()
	{
		for(NpcInstance npc : Olympiad.getNpcs())
		{
			NpcString npcString;
			switch(_type)
			{
				case CLASSED:
					npcString = NpcString.OLYMPIAD_CLASS_INDIVIDUAL_MATCH_IS_GOING_TO_BEGIN_IN_ARENA_S1_IN_A_MOMENT;
					break;
				case NON_CLASSED:
					npcString = NpcString.OLYMPIAD_CLASSFREE_INDIVIDUAL_MATCH_IS_GOING_TO_BEGIN_IN_ARENA_S1_IN_A_MOMENT;
					break;
				default:
					continue;
			}
			Functions.npcShout(npc, npcString, String.valueOf(_id + 1));
		}
	}

	public void portPlayersToArena()
	{
		_member1.portPlayerToArena();
		_member2.portPlayerToArena();
	}

	public void preparePlayers1()
	{
		_member1.preparePlayer1();
		_member2.preparePlayer1();
	}

	public void preparePlayers2()
	{
		_member1.preparePlayer2();
		_member2.preparePlayer2();
	}

	public void portPlayersBack()
	{
		_member1.portPlayerBack();
		_member2.portPlayerBack();
	}

	public boolean validatePlayers()
	{
		Player player1 = _member1.getPlayer();
		Player player2 = _member2.getPlayer();

		if(!Olympiad.validPlayer(player1, player2, _type, true))
			return false;

		if(!Olympiad.validPlayer(player2, player1, _type, true))
			return false;

		return true;
	}

	public void collapse()
	{
		portPlayersBack();
		clearObservers();
		_reflection.collapse();
	}

	public void validateWinner(boolean aborted) throws Exception
	{
		int state = _state;
		_state = 0;

		if(validated)
		{
			Log.add("Olympiad Result: " + _member1.getName() + " vs " + _member2.getName() + " ... double validate check!!!", "olympiad");
			return;
		}
		validated = true;

		// Если игра закончилась до телепортации на стадион, то забираем очки у вышедших из игры, не засчитывая никому победу
		if(state < 1 && aborted)
		{
			_member1.takePointsForCrash();
			_member2.takePointsForCrash();
			broadcastPacket(SystemMsg.YOUR_OPPONENT_MADE_HASTE_WITH_THEIR_TAIL_BETWEEN_THEIR_LEGS_THE_MATCH_HAS_BEEN_CANCELLED, true, false);
			return;
		}

		boolean member1Check = _member1.checkPlayer();
		boolean member2Check = _member2.checkPlayer();

		if(_winner <= 0)
		{
			if(!member1Check && !member2Check)
				_winner = 0;
			else if(!member2Check)
				_winner = 1; // Выиграла первая команда
			else if(!member1Check)
				_winner = 2; // Выиграла вторая команда
			else if(_member1.getDamage() < _member2.getDamage()) // Вторая команда нанесла вреда меньше, чем первая
				_winner = 1; // Выиграла первая команда
			else if(_member1.getDamage() > _member2.getDamage()) // Вторая команда нанесла вреда больше, чем первая
				_winner = 2; // Выиграла вторая команда
		}

		if(_winner == 1) // Выиграла первая команда
			winGame(_member1, _member2);
		else if(_winner == 2) // Выиграла вторая команда
			winGame(_member2, _member1);
		else
			tie();

		_member1.saveParticipantData();
		_member2.saveParticipantData();

		broadcastRelation();
		broadcastPacket(new SystemMessagePacket(SystemMsg.YOU_WILL_BE_MOVED_BACK_TO_TOWN_IN_S1_SECONDS).addInteger(20), true, true);
	}

	private void winGame(OlympiadMember winnerMember, OlympiadMember looseMember)
	{
		ExReceiveOlympiadPacket.MatchResult packet = new ExReceiveOlympiadPacket.MatchResult(false, winnerMember.getName());

		int pointDiff = 0;
		if(looseMember != null && winnerMember != null)
		{
			winnerMember.incGameCount();
			looseMember.incGameCount();

			int gamePoints = transferPoints(looseMember.getStat(), winnerMember.getStat());

			packet.addPlayer(winnerMember == _member1 ? TeamType.RED : TeamType.BLUE, winnerMember, gamePoints, (int) looseMember.getDamage());
			packet.addPlayer(looseMember == _member1 ? TeamType.RED : TeamType.BLUE, looseMember, -gamePoints, (int) winnerMember.getDamage());

			pointDiff += gamePoints;
		}

		if(_member1 != null && _member2 != null)
		{
			int team = _member1 == winnerMember ? 1 : 2;
			int diff = (int) ((System.currentTimeMillis() - _startTime) / 1000L);
			OlympiadHistory h = new OlympiadHistory(_member1.getObjectId(), _member2.getObjectId(), _member1.getClassId(), _member2.getClassId(), _member1.getName(), _member2.getName(), _startTime, diff, team, _type.ordinal());
			OlympiadHistoryManager.getInstance().saveHistory(h);
		}

		Player winnerPlayer = winnerMember.getPlayer();
		if(winnerPlayer != null)
			ItemFunctions.addItem(winnerPlayer, Config.ALT_OLY_BATTLE_REWARD_ITEM, getType().getWinnerReward());

		Player looserPlayer = looseMember.getPlayer();
		if(looserPlayer != null)
			ItemFunctions.addItem(winnerPlayer, Config.ALT_OLY_BATTLE_REWARD_ITEM, getType().getLooserReward());

		for(OlympiadMember member : getAllMembers())
		{
			Player player = member.getPlayer();
			if(player == null)
				continue;

			player.getListeners().onOlympiadFinishBattle(winnerMember == member);

			for(QuestState qs : player.getAllQuestsStates())
			{
				if(qs.isStarted())
					qs.getQuest().onOlympiadEnd(this, qs);
			}
		}

		broadcastPacket(packet, true, false);

		//FIXME [VISTALL] неверная мессага?
		broadcastPacket(new SystemMessagePacket(SystemMsg.CONGRATULATIONS_C1_YOU_WIN_THE_MATCH).addString(winnerMember.getName()), false, true);

		//FIXME [VISTALL] нужно ли?
		//broadcastPacket(new SystemMessagePacket(SystemMsg.C1_HAS_EARNED_S2_POINTS_IN_THE_GRAND_OLYMPIAD_GAMES).addString(winnerMember.getName()).addInteger(pointDiff), true, false);
		//broadcastPacket(new SystemMessagePacket(SystemMsg.C1_HAS_LOST_S2_POINTS_IN_THE_GRAND_OLYMPIAD_GAMES).addString(looseMember.getName()).addInteger(pointDiff), true, false);

		Log.add("Olympiad Result: " + winnerMember.getName() + " vs " + looseMember.getName() + " ... (" + (int) winnerMember.getDamage() + " vs " + (int) looseMember.getDamage() + ") " + winnerMember.getName() + " win " + pointDiff + " points", "olympiad");
	}

	public void tie()
	{
		ExReceiveOlympiadPacket.MatchResult packet = new ExReceiveOlympiadPacket.MatchResult(true, StringUtils.EMPTY);
		try
		{
			if(_member1 != null)
			{
				_member1.incGameCount();
				OlympiadParticipiantData stat1 = _member1.getStat();
				packet.addPlayer(TeamType.RED, _member1, -2, (int) _member2.getDamage());

				stat1.setPoints(stat1.getPoints() - 2);
			}

			if(_member2 != null)
			{
				_member2.incGameCount();
				OlympiadParticipiantData stat2 = _member2.getStat();
				packet.addPlayer(TeamType.BLUE, _member2, -2, (int) _member1.getDamage());

				stat2.setPoints(stat2.getPoints() - 2);
			}
		}
		catch(Exception e)
		{
			_log.error("OlympiadGame.tie(): " + e, e);
		}

		if(_member1 != null && _member2 != null)
		{
			int diff = (int) ((System.currentTimeMillis() - _startTime) / 1000L);
			OlympiadHistory h = new OlympiadHistory(_member1.getObjectId(), _member2.getObjectId(), _member1.getClassId(), _member2.getClassId(), _member1.getName(), _member2.getName(), _startTime, diff, 0, _type.ordinal());

			OlympiadHistoryManager.getInstance().saveHistory(h);
		}

		for(OlympiadMember member : getAllMembers())
		{
			Player player = member.getPlayer();
			if(player == null)
				continue;

			player.getListeners().onOlympiadFinishBattle(false);
			for(QuestState qs : player.getAllQuestsStates())
			{
				if(qs.isStarted())
					qs.getQuest().onOlympiadEnd(this, qs);
			}
		}

		broadcastPacket(SystemMsg.THERE_IS_NO_VICTOR_THE_MATCH_ENDS_IN_A_TIE, false, true);
		broadcastPacket(packet, true, false);

		Log.add("Olympiad Result: " + _member1.getName() + " vs " + _member2.getName() + " ... tie", "olympiad");
	}

	private int transferPoints(OlympiadParticipiantData from, OlympiadParticipiantData to)
	{
		int fromPoints = from.getPoints();
		int fromLoose = from.getCompLoose();
		int fromPlayed = from.getCompDone();

		int toPoints = to.getPoints();
		int toWin = to.getCompWin();
		int toPlayed = to.getCompDone();

		int pointDiff = Math.max(1, Math.min(fromPoints, toPoints) / getType().getLooseMult());
		pointDiff = pointDiff > OlympiadGame.MAX_POINTS_LOOSE ? OlympiadGame.MAX_POINTS_LOOSE : pointDiff;

		from.setPoints(fromPoints - pointDiff);
		from.setCompLoose(fromLoose + 1);
		from.setCompDone(fromPlayed + 1);

		to.setPoints(toPoints + pointDiff);
		to.setCompWin(toWin + 1);
		to.setCompDone(toPlayed + 1);

		return pointDiff;
	}

	public void openDoors()
	{
		for(DoorInstance door : _reflection.getDoors())
			door.openMe();
	}

	public int getId()
	{
		return _id;
	}

	@Override
	public Reflection getReflection()
	{
		return _reflection;
	}

	@Override
	public Location getObserverEnterPoint(Player player)
	{
		List<Location> spawns = getReflection().getInstancedZone().getTeleportCoords();
		if(spawns.size() < 3)
		{
			Location c1 = spawns.get(0);
			Location c2 = spawns.get(1);
			return new Location((c1.x + c2.x) / 2, (c1.y + c2.y) / 2, (c1.z + c2.z) / 2);
		}

		return spawns.get(2);
	}

	@Override
	public boolean showObservableArenasList(Player player)
	{
		if((!Olympiad.inCompPeriod()) || (Olympiad.isOlympiadEnd()))
		{
			player.sendPacket(SystemMsg.THE_GRAND_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS);
			return false;
		}
		player.sendPacket(new ExReceiveOlympiadPacket.MatchList());
		return true;
	}

	@Override
	public void onAppearObserver(ObservePoint observer)
	{
		broadcastInfo(null, observer.getPlayer(), true);
	}

	@Override
	public void onEnterObserverArena(Player player)
	{
		player.sendPacket(new ExOlympiadModePacket(3));
	}

	@Override
	public void onChangeObserverArena(Player player)
	{
		player.sendPacket(ExOlympiadMatchEndPacket.STATIC);
	}

	@Override
	public void onExitObserverArena(Player player)
	{
		player.sendPacket(new ExOlympiadModePacket(0));
		player.sendPacket(ExOlympiadMatchEndPacket.STATIC);
	}

	public boolean isRegistered(int objId)
	{
		return _member1.getObjectId() == objId || _member2.getObjectId() == objId;
	}

	public void broadcastInfo(Player sender, Player receiver, boolean onlyToObservers)
	{
		// TODO заюзать пакеты:
		// ExEventMatchCreate
		// ExEventMatchFirecracker
		// ExEventMatchManage
		// ExEventMatchMessage
		// ExEventMatchObserver
		// ExEventMatchScore
		// ExEventMatchTeamInfo
		// ExEventMatchTeamUnlocked
		// ExEventMatchUserInfo

		if(sender != null)
			if(receiver != null)
				receiver.sendPacket(new ExOlympiadUserInfoPacket(sender, sender.getOlympiadSide()));
			else
				broadcastPacket(new ExOlympiadUserInfoPacket(sender, sender.getOlympiadSide()), !onlyToObservers, true);
		else
		{
			// Рассылаем информацию о первой команде
			Player player = _member1.getPlayer();
			if(player != null)
			{
				if(receiver != null)
					receiver.sendPacket(new ExOlympiadUserInfoPacket(player, player.getOlympiadSide()));
				else
				{
					broadcastPacket(new ExOlympiadUserInfoPacket(player, player.getOlympiadSide()), !onlyToObservers, true);
					PlayerUtils.updateAttackableFlags(player);
				}
			}

			// Рассылаем информацию о второй команде
			player = _member2.getPlayer();
			if(player != null)
			{
				if(receiver != null)
					receiver.sendPacket(new ExOlympiadUserInfoPacket(player, player.getOlympiadSide()));
				else
				{
					broadcastPacket(new ExOlympiadUserInfoPacket(player, player.getOlympiadSide()), !onlyToObservers, true);
					PlayerUtils.updateAttackableFlags(player);
				}
			}
		}
	}

	public void broadcastRelation()
	{
		for(OlympiadMember member : getAllMembers())
		{
			Player player = member.getPlayer();
			if(player == null)
				continue;

			PlayerUtils.updateAttackableFlags(player);
		}
	}

	public void broadcastPacket(L2GameServerPacket packet, boolean toTeams, boolean toObservers)
	{
		if(toTeams)
		{
			for(OlympiadMember member : getAllMembers())
			{
				Player player = member.getPlayer();
				if(player == null)
					continue;

				player.sendPacket(packet);
			}
		}

		if(toObservers)
		{
			for(ObservePoint observer : getObservers())
				observer.sendPacket(packet);
		}
	}

	public void broadcastPacket(IBroadcastPacket packet, boolean toTeams, boolean toObservers)
	{
		if(toTeams)
		{
			for(OlympiadMember member : getAllMembers())
			{
				Player player = member.getPlayer();
				if(player == null)
					continue;

				player.sendPacket(packet);
			}
		}

		if(toObservers)
		{
			for(ObservePoint observer : getObservers())
				observer.sendPacket(packet);
		}
	}

	public List<Player> getAllPlayers()
	{
		List<Player> result = new ArrayList<Player>();

		Player player = _member1.getPlayer();
		if(player != null)
			result.add(player);

		player = _member2.getPlayer();
		if(player != null)
			result.add(player);

		for(ObservePoint observer : getObservers())
			result.add(observer.getPlayer());

		return result;
	}

	public void setWinner(int val)
	{
		_winner = val;
	}

	public OlympiadMember getWinnerMember()
	{
		if(_winner == 1) // Выиграла первая команда
			return _member1;
		else if(_winner == 2) // Выиграла вторая команда
			return _member2;
		return null;
	}

	public OlympiadMember[] getAllMembers()
	{
		return new OlympiadMember[] { _member1, _member2 };
	}

	public void setState(int val)
	{
		_state = val;
		if(_state == 1)
			_startTime = System.currentTimeMillis();
	}

	public int getState()
	{
		return _state;
	}

	public void addDamage(Player player, double damage)
	{
		if(player.getOlympiadSide() == 1)
			_member1.addDamage(damage);
		else
			_member2.addDamage(damage);
	}

	public boolean checkPlayersOnline()
	{
		return _member1.checkPlayer() && _member2.checkPlayer();
	}

	public void logoutPlayer(Player player)
	{
		if(player != null)
		{
			if(player.getOlympiadSide() == 1)
				_member1.logout();
			else
				_member2.logout();
		}
	}

	OlympiadGameTask _task;
	ScheduledFuture<?> _shedule;

	public synchronized void sheduleTask(OlympiadGameTask task)
	{
		if(_shedule != null)
			_shedule.cancel(false);
		_task = task;
		_shedule = task.shedule();
	}

	public OlympiadGameTask getTask()
	{
		return _task;
	}

	public BattleStatus getStatus()
	{
		if(_task != null)
			return _task.getStatus();
		return BattleStatus.Begining;
	}

	public void endGame(long time, boolean aborted)
	{
		try
		{
			validateWinner(aborted);
		}
		catch(Exception e)
		{
			_log.error("", e);
		}

		sheduleTask(new OlympiadGameTask(this, BattleStatus.Ending, 0, time));
	}

	public CompType getType()
	{
		return _type;
	}

	public String getMemberName1()
	{
		return _member1.getName();
	}

	public String getMemberName2()
	{
		return _member2.getName();
	}
}