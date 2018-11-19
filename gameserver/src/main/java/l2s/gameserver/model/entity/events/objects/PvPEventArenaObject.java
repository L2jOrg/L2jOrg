package l2s.gameserver.model.entity.events.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import l2s.commons.util.Rnd;
import l2s.gameserver.Announcements;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.listener.hooks.ListenerHookType;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.model.actor.instances.player.Cubic;
import l2s.gameserver.model.base.TeamType;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.entity.events.hooks.PvPEventHook;
import l2s.gameserver.model.entity.events.impl.DuelEvent;
import l2s.gameserver.model.entity.events.impl.PvPEvent;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.skills.TimeStamp;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Location;

public class PvPEventArenaObject extends Reflection
{
	private final PvPEvent _pvpEvent;
	private final List<Set<PvPEventPlayerObject>> _teamsList;

	public PvPEventArenaObject(PvPEvent pvpEvent, int teams)
	{
		_pvpEvent = pvpEvent;
		_teamsList = new ArrayList<Set<PvPEventPlayerObject>>(teams);
		for(int i = 0; i < teams; i++)
			_teamsList.add(new CopyOnWriteArraySet<PvPEventPlayerObject>());
	}

	public void addPlayer(Player player, int teamId)
	{
		switch(teamId)
		{
			case 0:
				player.setTeam(TeamType.BLUE);
				break;
			case 1:
				player.setTeam(TeamType.RED);
				break;
		}

		_teamsList.get(teamId).add(new PvPEventPlayerObject(player, teamId));
	}

	public void sortPlayers(List<Player> players)
	{
		Collections.shuffle(players);
		int teamId = 0;
		for(Player player : players)
		{
			if(_teamsList.size() == 1)
			{
				player.setTeam(TeamType.RED);
				_teamsList.get(0).add(new PvPEventPlayerObject(player, -1));
			}
			else
			{
				switch(teamId)
				{
					case 0:
						player.setTeam(TeamType.BLUE);
						break;
					case 1:
						player.setTeam(TeamType.RED);
						break;
				}

				_teamsList.get(teamId).add(new PvPEventPlayerObject(player, teamId));

				if(++teamId != _teamsList.size())
					continue;

				teamId = 0;
			}
		}
	}

	public void teleportPlayers()
	{
		for(int i = 0; i < _teamsList.size(); ++i)
		{
			for(PvPEventPlayerObject member : _teamsList.get(i))
			{
				Player player = member.getPlayer();
				if(player == null)
					continue;

				player.leaveParty();
				player.setStablePoint(player.getLoc());
				player.addEvent(_pvpEvent);
				teleportPlayer(player, _pvpEvent.getLocation("team" + i));
				block(player);
				_pvpEvent.abnormals(player, true);
				addHook(player);
				player.getInventory().validateItems();
				player.getInventory().validateItemsSkills();
			}
		}
	}

	public void startBattle()
	{
		for(int i = 0; i < _teamsList.size(); ++i)
		{
			for(PvPEventPlayerObject member : _teamsList.get(i))
			{
				Player player = member.getPlayer();
				if(player == null)
					continue;

				unBlock(player);
				buff(player);
				player.sendPacket(new ExShowScreenMessage("В бой !!!", 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));
			}
		}
	}

	public void stopBattle()
	{
		rewardTeams();
		for(int i = 0; i < _teamsList.size(); ++i)
		{
			for(PvPEventPlayerObject member : _teamsList.get(i))
			{
				Player player = member.getPlayer();
				if(player == null)
					continue;

				removePlayer(player);
			}
		}
		_pvpEvent.removeObject("arenas", this);
		collapse();
	}

	public void check()
	{
		if(_teamsList.size() >= 2)
		{
			int emptyTeams = 0;
			for(Set<PvPEventPlayerObject> team : _teamsList)
			{
				if(team.isEmpty())
					emptyTeams++;
			}
			if(_teamsList.size() - emptyTeams <= 1)
				stopBattle();
		}
		else if(_teamsList.get(0).size() <= 1)
		{
			if(_teamsList.get(0).size() == 1)
			{
				PvPEventPlayerObject object = _teamsList.get(0).iterator().next();
				if(_pvpEvent.isAddHeroLastPlayer() && !object.getPlayer().isHero())
					object.getPlayer().setCustomHero(1);

				List<RewardObject> rewards = _pvpEvent.getObjects("reward_for_last_player");
				for(RewardObject reward : rewards)
				{
					if(Rnd.chance(reward.getChance()))
						ItemFunctions.addItem(object.getPlayer(), reward.getItemId(), Rnd.get(reward.getMinCount(), reward.getMaxCount()));
				}
				Announcements.announceToAll("Победитель ивента " + _pvpEvent.getName() + ": " + object.getPlayer().getName());
			}
			stopBattle();
		}
	}

	public void teleportPlayer(Player player, Location location)
	{
		if(player.isTeleporting())
			return;

		if(player.isDead())
		{
			player.doRevive(100);
			player.setCurrentHp(player.getMaxHp(), true);
		}
		else
			player.setCurrentHp(player.getMaxHp(), false);

		player.setCurrentCp(player.getMaxCp());
		player.setCurrentMp(player.getMaxMp());
		if(player.isInObserverMode())
			player.leaveObserverMode();

		if(_pvpEvent.isDisableHeroAndClanSkills())
		{
			// Un activate clan skills
			if(player.getClan() != null)
				player.getClan().disableSkills(player);

			// Деактивируем геройские скиллы.
			player.activateHeroSkills(false);
		}
		player.abortCast(true, true);
		player.abortAttack(true, true);

		// Удаляем баффы
		for(Abnormal abnormal : player.getAbnormalList())
		{
			if(!player.isSpecialAbnormal(abnormal.getSkill()))
				abnormal.exit();
		}

		// Удаляем кубики
		for(Cubic cubic : player.getCubics())
		{
			if(player.getSkillLevel(cubic.getSkill().getId()) <= 0)
				cubic.delete();
		}

		// Remove Servitor's Buffs
		for(Servitor servitor : player.getServitors())
		{
			if(servitor.isPet())
				servitor.unSummon(false);
			else
			{
				servitor.getAbnormalList().stopAll();
				servitor.transferOwnerBuffs();
			}
		}

		// unsummon agathion
		if(player.getAgathionId() > 0)
			player.setAgathion(0);

		if(_pvpEvent.isResetSkills())
		{
			// Сброс кулдауна всех скилов, время отката которых меньше 15 минут
			for(TimeStamp sts : player.getSkillReuses())
			{
				if(sts == null)
					continue;

				Skill skill = SkillHolder.getInstance().getSkill(sts.getId(), sts.getLevel());
				if(skill == null)
					continue;

				if(sts.getReuseBasic() <= 15 * 60001L)
				{
					player.enableSkill(skill);
				}
			}
		}
		// Обновляем скилл лист, после удаления скилов
		player.sendSkillList();
		// Проверяем одетые вещи на возможность ношения.
		player.getInventory().validateItems();
		player.removeAutoShots(true);
		player.broadcastUserInfo(true);
		DuelEvent duel = player.getEvent(DuelEvent.class);
		if(duel != null)
			duel.abortDuel(player);

		if(player.isSitting())
			player.standUp();

		player.setTarget(null);
		player.teleToLocation(location, this);
	}

	public void block(Player player)
	{
		player.getFlags().getImmobilized().start(this);
		player.getFlags().getInvulnerable().start(this);
	}

	public void unBlock(Player player)
	{
		player.getFlags().getImmobilized().stop(this);
		player.getFlags().getInvulnerable().stop(this);
	}

	public void buff(Player player)
	{
		for(Abnormal abnormal : player.getAbnormalList())
		{
			if(!player.isSpecialAbnormal(abnormal.getSkill()))
				abnormal.exit();
		}
		for(int[] skillId : _pvpEvent.getBuffs())
		{
			Skill skill = SkillHolder.getInstance().getSkill(skillId[0], skillId[1]);
			if(skill != null)
				skill.getEffects(player, player);
		}
		Skill skill = SkillHolder.getInstance().getSkill(1323, 1);
		if(skill != null)
			skill.getEffects(player, player);
	}

	public void heal(Player player)
	{
		player.setCurrentHp(player.getMaxHp(), false);
		player.setCurrentCp(player.getMaxCp());
		player.setCurrentMp(player.getMaxMp());
	}

	public void addHook(Player player)
	{
		player.addListenerHook(ListenerHookType.PLAYER_TELEPORT, PvPEventHook.getInstance());
		player.addListenerHook(ListenerHookType.PLAYER_DIE, PvPEventHook.getInstance());
	}

	public void removeHook(Player player)
	{
		player.removeListenerHookType(ListenerHookType.PLAYER_QUIT_GAME, PvPEventHook.getInstance());
		player.removeListenerHookType(ListenerHookType.PLAYER_TELEPORT, PvPEventHook.getInstance());
		player.removeListenerHookType(ListenerHookType.PLAYER_DIE, PvPEventHook.getInstance());
	}

	public void removePlayer(Player player)
	{
		PvPEventPlayerObject member = getParticipant(player);
		if(member == null)
			return;

		_pvpEvent.abnormals(player, false);
		int teamId = member.getTeam();
		if(teamId == -1)
			teamId = 0;

		_teamsList.get(teamId).remove(member);
		removeHook(player);
		unBlock(player);
		player.removeEvent(_pvpEvent);
		player.setTeam(TeamType.NONE);
		player.teleToLocation(player.getStablePoint(), ReflectionManager.MAIN);
		if(player.isDead())
		{
			player.doRevive(100.0);
			player.setCurrentHp(player.getMaxHp(), true);
		}
		else
			player.setCurrentHp(player.getMaxHp(), false);

		player.setCurrentCp(player.getMaxCp());
		player.setCurrentMp(player.getMaxMp());
		if(player.getClan() != null)
			player.getClan().enableSkills(player);

		player.activateHeroSkills(true);
		player.sendSkillList();
	}

	public void rewardTeams()
	{
		_teamsList.sort(new WinComparator());
		Set<PvPEventPlayerObject> teamWin = _teamsList.get(0);
		if(getPointByTeam(teamWin) >= _pvpEvent.getMinKillTeamFromReward())
		{
			List<RewardObject> rewards = _pvpEvent.getObjects("reward_for_win_team");
			for(PvPEventPlayerObject object : teamWin)
			{
				Player player = object.getPlayer();
				player.sendPacket(new ExShowScreenMessage(new CustomMessage("l2s.gameserver.model.entity.events.impl.PvPEvent.win").toString(player), 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));
			}
			takeReward(teamWin, rewards);
		}
		List<RewardObject> rewards = _pvpEvent.getObjects("reward_for_lose_team");
		for(int i = 1; i < _teamsList.size(); ++i)
		{
			Set<PvPEventPlayerObject> teamLose = _teamsList.get(i);
			for(PvPEventPlayerObject object : teamLose)
			{
				Player player = object.getPlayer();
				player.sendPacket(new ExShowScreenMessage(new CustomMessage("l2s.gameserver.model.entity.events.impl.PvPEvent.lose").toString(player), 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));
			}
			if(getPointByTeam(teamLose) >= _pvpEvent.getMinKillTeamFromReward())
				takeReward(teamLose, rewards);
		}
	}

	private static int getPointByTeam(Set<PvPEventPlayerObject> players)
	{
		int points = 0;
		for(PvPEventPlayerObject member : players)
			points += member.getPoints();

		return points;
	}

	private void takeReward(Set<PvPEventPlayerObject> players, List<RewardObject> rewards)
	{
		for(PvPEventPlayerObject member : players)
		{
			if(member.getPoints() < _pvpEvent.getMinKillFromReward())
				continue;

			rewards.stream().filter(reward -> Rnd.chance(reward.getChance())).forEach(reward -> ItemFunctions.addItem(member.getPlayer(), reward.getItemId(), Rnd.get(reward.getMinCount(), reward.getMaxCount())));
		}
	}

	public PvPEventPlayerObject getParticipant(Player player)
	{
		for(int i = 0; i < _teamsList.size(); ++i)
		{
			for(PvPEventPlayerObject member : _teamsList.get(i))
			{
				if(member.getPlayer() == player)
					return member;
			}
		}
		return null;
	}

	public static class WinComparator implements Comparator<Set<PvPEventPlayerObject>>
	{
		@Override
		public int compare(Set<PvPEventPlayerObject> o1, Set<PvPEventPlayerObject> o2)
		{
			return Integer.compare(getPointByTeam(o2), getPointByTeam(o1));
		}
	}
}