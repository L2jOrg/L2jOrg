package l2s.gameserver.model.instances;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import l2s.commons.collections.MultiValueSet;
import l2s.commons.math.SafeMath;
import l2s.commons.util.Rnd;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.AggroList.HateInfo;
import l2s.gameserver.model.*;
import l2s.gameserver.model.base.Experience;
import l2s.gameserver.model.base.TeamType;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestEventType;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.model.reward.RewardItem;
import l2s.gameserver.model.reward.RewardList;
import l2s.gameserver.model.reward.RewardType;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExMagicAttackInfo;
import l2s.gameserver.network.l2.s2c.SocialActionPacket;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.templates.npc.Faction;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;

/**
 * This class manages all Monsters.
 * <p>
 * L2MonsterInstance :<BR><BR>
 * <li>L2MinionInstance</li>
 * <li>L2RaidBossInstance </li>
 */
public class MonsterInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	private static final int MONSTER_MAINTENANCE_INTERVAL = 1000;

	protected static final class RewardInfo
	{
		protected Creature _attacker;
		protected int _dmg = 0;

		public RewardInfo(final Creature attacker, final int dmg)
		{
			_attacker = attacker;
			_dmg = dmg;
		}

		public void addDamage(int dmg)
		{
			if(dmg < 0)
			{
				dmg = 0;
			}

			_dmg += dmg;
		}

		@Override
		public int hashCode()
		{
			return _attacker.getObjectId();
		}
	}

	private int overhitAttackerId;
	/**
	 * Stores the extra (over-hit) damage done to the L2NpcInstance when the attacker uses an over-hit enabled skill
	 */
	private double _overhitDamage;

	/**
	 * True if a Dwarf has used Spoil on this L2NpcInstance
	 */
	private boolean _isSpoiled;
	private int spoilerId;
	/**
	 * Table containing all Items that a Dwarf can Sweep on this L2NpcInstance
	 */
	private List<RewardItem> _sweepItems;
	private boolean _sweeped;
	private final Lock sweepLock = new ReentrantLock();

	private int _isChampion;

	private final boolean _canMove;

	public MonsterInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);

		_canMove = getParameter("canMove", true);
	}

	@Override
	public boolean isMovementDisabled()
	{
		// Невозможность ходить для этих мобов
		return !_canMove || super.isMovementDisabled();
	}

	@Override
	public boolean isLethalImmune()
	{
		return _isChampion > 0 || super.isLethalImmune();
	}

	@Override
	public boolean isFearImmune()
	{
		return _isChampion > 0 || super.isFearImmune();
	}

	@Override
	public boolean isParalyzeImmune()
	{
		return _isChampion > 0 || super.isParalyzeImmune();
	}

	/**
	 * Return True if the attacker is not another L2MonsterInstance.<BR><BR>
	 */
	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		return attacker.getPlayer() != null || attacker.isDefender();
	}

	public int getChampion()
	{
		return _isChampion;
	}

	public void setChampion()
	{
		if(getReflection().canChampions() && canChampion())
		{
			double random = Rnd.nextDouble();
			if(Config.ALT_CHAMPION_CHANCE2 / 100. >= random)
				setChampion(2);
			else if((Config.ALT_CHAMPION_CHANCE1 + Config.ALT_CHAMPION_CHANCE2) / 100. >= random)
				setChampion(1);
			else
				setChampion(0);
		}
		else
			setChampion(0);
	}

	public void setChampion(int level)
	{
		if(level == 0)
		{
			removeSkillById(4407);
			_isChampion = 0;
		}
		else
		{
			addSkill(SkillHolder.getInstance().getSkillEntry(4407, level));
			_isChampion = level;
		}
	}

	public boolean canChampion()
	{
		return !isMinion() && getTemplate().rewardExp > 0 && getTemplate().level >= Config.ALT_CHAMPION_MIN_LEVEL && getTemplate().level <= Config.ALT_CHAMPION_TOP_LEVEL;
	}

	@Override
	public TeamType getTeam()
	{
		return getChampion() == 2 ? TeamType.RED : getChampion() == 1 ? TeamType.BLUE : TeamType.NONE;
	}

	@Override
	protected void onDespawn()
	{
		setOverhitDamage(0);
		setOverhitAttacker(null);
		clearSweep();

		super.onDespawn();
	}

	@Override
	public void onSpawnMinion(NpcInstance minion)
	{
		if(minion.isMonster())
		{
			if(getChampion() == 2)
				((MonsterInstance) minion).setChampion(1);
			else
				((MonsterInstance) minion).setChampion(0);
		}
		super.onSpawnMinion(minion);
	}

	@Override
	protected void onDeath(Creature killer)
	{
		calculateRewards(killer);

		super.onDeath(killer);
	}

	@Override
	protected void onReduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean isDot)
	{
		if(skill != null && skill.isOverhit())
		{
			// Calculate the over-hit damage
			// Ex: mob had 10 HP left, over-hit skill did 50 damage total, over-hit damage is 40
			double overhitDmg = (getCurrentHp() - damage) * -1;
			if(overhitDmg <= 0)
			{
				setOverhitDamage(0);
				setOverhitAttacker(null);
			}
			else
			{
				setOverhitDamage(overhitDmg);
				setOverhitAttacker(attacker);
			}
		}

		super.onReduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, isDot);
	}

	public void calculateRewards(Creature lastAttacker)
	{
		Creature topDamager = getAggroList().getTopDamager(lastAttacker);
		if(lastAttacker == null || !lastAttacker.isPlayable())
			lastAttacker = topDamager;

		if(lastAttacker == null || !lastAttacker.isPlayable())
			return;

		Player killer = lastAttacker.getPlayer();
		if(killer == null)
			return;

		Map<Playable, HateInfo> aggroMap = getAggroList().getPlayableMap();

		Set<Quest> quests = getTemplate().getEventQuests(QuestEventType.MOB_KILLED_WITH_QUEST);
		if(quests != null && !quests.isEmpty())
		{
			List<Player> players = null; // массив с игроками, которые могут быть заинтересованы в квестах
			if(isRaid() && Config.ALT_NO_LASTHIT) // Для альта на ластхит берем всех игроков вокруг
			{
				players = new ArrayList<Player>();
				for(Playable pl : aggroMap.keySet())
				{
					if(!pl.isDead() && (isInRangeZ(pl, Config.ALT_PARTY_DISTRIBUTION_RANGE) || killer.isInRangeZ(pl, Config.ALT_PARTY_DISTRIBUTION_RANGE)))
						if(!players.contains(pl.getPlayer())) // не добавляем дважды если есть пет
							players.add(pl.getPlayer());
				}
			}
			else if(killer.getParty() != null) // если пати то собираем всех кто подходит
			{
				players = new ArrayList<Player>(killer.getParty().getMemberCount());
				for(Player pl : killer.getParty().getPartyMembers())
					if(!pl.isDead() && (isInRangeZ(pl, Config.ALT_PARTY_DISTRIBUTION_RANGE) || killer.isInRangeZ(pl, Config.ALT_PARTY_DISTRIBUTION_RANGE)))
						players.add(pl);
			}

			for(Quest quest : quests)
			{
				Player toReward = killer;
				if(quest.getPartyType() != Quest.PARTY_NONE && players != null)
				{
					if(isRaid() || quest.getPartyType() == Quest.PARTY_ALL) // если цель рейд или квест для всей пати награждаем всех участников
					{
						for(Player pl : players)
						{
							QuestState qs = pl.getQuestState(quest);
							if(qs != null && !qs.isCompleted())
								quest.notifyKill(this, qs);
						}
						toReward = null;
					}
					else
					{ // иначе выбираем одного
						List<Player> interested = new ArrayList<Player>(players.size());
						for(Player pl : players)
						{
							QuestState qs = pl.getQuestState(quest);
							if(qs != null && !qs.isCompleted()) // из тех, у кого взят квест
								interested.add(pl);
						}

						if(interested.isEmpty())
							continue;

						toReward = interested.get(Rnd.get(interested.size()));
						if(toReward == null)
							toReward = killer;
					}
				}

				if(toReward != null)
				{
					QuestState qs = toReward.getQuestState(quest);
					if(qs != null && !qs.isCompleted())
						quest.notifyKill(this, qs);
				}
			}
		}

		Map<Player, RewardInfo> rewards = new HashMap<Player, RewardInfo>();
		for(HateInfo info : aggroMap.values())
		{
			if(info.damage <= 1)
				continue;

			Playable attacker = (Playable) info.attacker;
			Player player = attacker.getPlayer();
			RewardInfo reward = rewards.get(player);
			if(reward == null)
				rewards.put(player, new RewardInfo(player, info.damage));
			else
				reward.addDamage(info.damage);
		}

		if(topDamager != null && topDamager.isPlayable())
		{
			for(RewardList rewardList : getTemplate().getRewards())
				rollRewards(rewardList, lastAttacker, topDamager);
		}

		Player[] attackers = rewards.keySet().toArray(new Player[rewards.size()]);
		double[] xpsp = new double[2];

		for(Player attacker : attackers)
		{
			if(attacker.isDead())
				continue;

			RewardInfo reward = rewards.get(attacker);

			if(reward == null)
				continue;

			Party party = attacker.getParty();
			int maxHp = getMaxHp();

			xpsp[0] = 0.;
			xpsp[1] = 0.;

			if(party == null)
			{
				int damage = Math.min(reward._dmg, maxHp);
				if(damage > 0)
				{
					if(isInRangeZ(attacker, Config.ALT_PARTY_DISTRIBUTION_RANGE))
						xpsp = calculateExpAndSp(attacker.getLevel(), damage);

					xpsp[0] = applyOverhit(killer, xpsp[0]);

					attacker.addExpAndCheckBonus(this, (long) xpsp[0], (long) xpsp[1]);
				}
				rewards.remove(attacker);
			}
			else
			{
				int partyDmg = 0;
				int partylevel = 1;
				List<Player> rewardedMembers = new ArrayList<Player>();
				for(Player partyMember : party.getPartyMembers())
				{
					RewardInfo ai = rewards.remove(partyMember);
					if(partyMember.isDead() || !isInRangeZ(partyMember, Config.ALT_PARTY_DISTRIBUTION_RANGE))
						continue;

					if(ai != null)
						partyDmg += ai._dmg;

					rewardedMembers.add(partyMember);
					if(partyMember.getLevel() > partylevel)
						partylevel = partyMember.getLevel();
				}
				partyDmg = Math.min(partyDmg, maxHp);
				if(partyDmg > 0)
				{
					xpsp = calculateExpAndSp(partylevel, partyDmg);
					double partyMul = (double) partyDmg / maxHp;
					xpsp[0] *= partyMul;
					xpsp[1] *= partyMul;
					xpsp[0] = applyOverhit(killer, xpsp[0]);
					party.distributeXpAndSp(xpsp[0], xpsp[1], rewardedMembers, lastAttacker, this);
				}
			}
		}
	}

	@Override
	public void onRandomAnimation()
	{
		if(System.currentTimeMillis() - _lastSocialAction > 10000L)
		{
			broadcastPacket(new SocialActionPacket(getObjectId(), 1));
			_lastSocialAction = System.currentTimeMillis();
		}
	}

	@Override
	public void startRandomAnimation()
	{
		//У мобов анимация обрабатывается в AI
	}

	@Override
	public int getKarma()
	{
		return 0;
	}

	/**
	 * Return True if this L2NpcInstance has drops that can be sweeped.<BR><BR>
	 */
	public boolean isSpoiled()
	{
		return _isSpoiled;
	}

	public boolean isSpoiled(Player player)
	{
		if(!isSpoiled()) // если не заспойлен то false
			return false;

		//заспойлен этим игроком, и смерть наступила не более 20 секунд назад
		if(player.getObjectId() == spoilerId && System.currentTimeMillis() - getDeathTime() < 20000L)
			return true;

		if(player.isInParty())
		{
			for(Player pm : player.getParty().getPartyMembers())
				if(pm.getObjectId() == spoilerId && getDistance(pm) < Config.ALT_PARTY_DISTRIBUTION_RANGE)
					return true;
		}

		return false;
	}

	/**
	 * Set the spoil state of this L2NpcInstance.<BR><BR>
	 * @param player
	 */
	public boolean setSpoiled(Player player)
	{
		sweepLock.lock();
		try
		{
			if(isSpoiled())
				return false;

			_isSpoiled = true;
			spoilerId = player.getObjectId();
		}
		finally
		{
			sweepLock.unlock();
		}
		return true;
	}

	/**
	 * Return True if a Dwarf use Sweep on the L2NpcInstance and if item can be spoiled.<BR><BR>
	 */
	public boolean isSweepActive()
	{
		sweepLock.lock();
		try
		{
			return _sweepItems != null && _sweepItems.size() > 0;
		}
		finally
		{
			sweepLock.unlock();
		}
	}

	public boolean takeSweep(final Player player)
	{
		sweepLock.lock();
		try
		{
			_sweeped = true;

			if(_sweepItems == null || _sweepItems.isEmpty())
			{
				clearSweep();
				return false;
			}

			for(RewardItem item : _sweepItems)
			{
				final ItemInstance sweep = ItemFunctions.createItem(item.itemId);
				sweep.setCount(item.count);

				if(player.isInParty() && player.getParty().isDistributeSpoilLoot())
				{
					player.getParty().distributeItem(player, sweep, null);
					continue;
				}

				if(!player.getInventory().validateCapacity(sweep) || !player.getInventory().validateWeight(sweep))
				{
					sweep.dropToTheGround(player, this);
					continue;
				}

				player.getInventory().addItem(sweep);

				SystemMessagePacket smsg;
				if(item.count == 1)
				{
					smsg = new SystemMessagePacket(SystemMsg.YOU_HAVE_OBTAINED_S1);
					smsg.addItemName(item.itemId);
					player.sendPacket(smsg);
				}
				else
				{
					smsg = new SystemMessagePacket(SystemMsg.YOU_HAVE_OBTAINED_S2_S1);
					smsg.addItemName(item.itemId);
					smsg.addLong(item.count);
					player.sendPacket(smsg);
				}

				if(player.isInParty())
				{
					if(item.count == 1)
					{
						smsg = new SystemMessagePacket(SystemMsg.C1_HAS_OBTAINED_S2_BY_USING_SWEEPER);
						smsg.addName(player);
						smsg.addItemName(item.itemId);
						player.getParty().getPartyLeader().sendPacket(smsg);
					}
					else
					{
						smsg = new SystemMessagePacket(SystemMsg.C1_HAS_OBTAINED_S3_S2_BY_USING_SWEEPER);
						smsg.addName(player);
						smsg.addItemName(item.itemId);
						smsg.addLong(item.count);
						player.getParty().getPartyLeader().sendPacket(smsg);
					}
				}
			}
			clearSweep();
			return true;
		}
		finally
		{
			sweepLock.unlock();
		}
	}

	public boolean isSweeped()
	{
		return _sweeped;
	}

	public void clearSweep()
	{
		sweepLock.lock();
		try
		{
			_isSpoiled = false;
			spoilerId = 0;
			_sweepItems = null;
		}
		finally
		{
			sweepLock.unlock();
		}
	}

	public void rollRewards(RewardList list, final Creature lastAttacker, Creature topDamager)
	{
		RewardType type = list.getType();
		if(type == RewardType.SWEEP && !isSpoiled())
			return;

		final Creature activeChar = type == RewardType.SWEEP ? lastAttacker : topDamager;
		final Player activePlayer = activeChar.getPlayer();

		if(activePlayer == null)
			return;

		double penaltyMod = Experience.penaltyModifier(calculateLevelDiffForDrop(topDamager.getLevel()), 9);

		List<RewardItem> rewardItems = list.roll(activePlayer, penaltyMod, this);
		switch(type)
		{
			case SWEEP:
				_sweepItems = rewardItems;
				break;
			default:
				for(RewardItem drop : rewardItems)
				{
					if(!Config.DROP_ONLY_THIS.isEmpty() && !Config.DROP_ONLY_THIS.contains(drop.itemId))
					{
						if(!Config.INCLUDE_RAID_DROP || !isRaid())
							return;
					}
					dropItem(activePlayer, drop.itemId, drop.count);
				}
				if(getChampion() > 0 && Config.SPECIAL_ITEM_ID > 0 && Math.abs(getLevel() - activePlayer.getLevel()) < 9 && Rnd.chance(Config.SPECIAL_ITEM_DROP_CHANCE))
					ItemFunctions.addItem(activePlayer, Config.SPECIAL_ITEM_ID, Config.SPECIAL_ITEM_COUNT);
				break;
		}
	}

	private double[] calculateExpAndSp(int level, long damage)
	{
		int diff = Math.min(Math.max(0, level - getLevel()), Config.MONSTER_LEVEL_DIFF_EXP_PENALTY.length - 1);

		double xp = SafeMath.mulAndLimit(getExpReward(), (double) damage / getMaxHp());
		double sp = SafeMath.mulAndLimit(getSpReward(), (double) damage / getMaxHp());

		double mod = (100.0 - Config.MONSTER_LEVEL_DIFF_EXP_PENALTY[diff]) / 100.0;
		xp = SafeMath.mulAndLimit(xp, mod);
		sp = SafeMath.mulAndLimit(sp, mod);

		xp = Math.max(0., xp);
		sp = Math.max(0., sp);

		return new double[]{xp, sp};
	}

	private double applyOverhit(Player killer, double xp)
	{
		if(xp > 0 && killer.getObjectId() == overhitAttackerId)
		{
			int overHitExp = calculateOverhitExp(xp);
			killer.sendPacket(SystemMsg.OVERHIT);
			killer.sendPacket(new ExMagicAttackInfo(killer.getObjectId(), getObjectId(), ExMagicAttackInfo.OVERHIT));

			xp += overHitExp;
		}
		return xp;
	}

	@Override
	public void setOverhitAttacker(Creature attacker)
	{
		overhitAttackerId = attacker == null ? 0 : attacker.getObjectId();
	}

	public double getOverhitDamage()
	{
		return _overhitDamage;
	}

	@Override
	public void setOverhitDamage(double damage)
	{
		_overhitDamage = damage;
	}

	public int calculateOverhitExp(final double normalExp)
	{
		double overhitPercentage = getOverhitDamage() * 100 / getMaxHp();
		if(overhitPercentage > 25)
			overhitPercentage = 25;

		double overhitExp = overhitPercentage / 100 * normalExp;
		setOverhitAttacker(null);
		setOverhitDamage(0);
		return (int) Math.round(overhitExp);
	}

	@Override
	public boolean isAggressive()
	{
		return (Config.ALT_CHAMPION_CAN_BE_AGGRO || getChampion() == 0) && super.isAggressive();
	}

	@Override
	public Faction getFaction()
	{
		if(getTemplate().isNoClan())
			return Faction.NONE;

		return Config.ALT_CHAMPION_CAN_BE_SOCIAL || getChampion() == 0 ? super.getFaction() : Faction.NONE;
	}

	@Override
	public boolean isMonster()
	{
		return true;
	}

	@Override
	public Clan getClan()
	{
		return null;
	}

	@Override
	public boolean isPeaceNpc()
	{
		return false;
	}
}