package org.l2j.gameserver.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;

import org.l2j.commons.threading.RunnableImpl;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.ai.ServitorAI;
import org.l2j.gameserver.data.dao.EffectsDAO;
import org.l2j.gameserver.handler.onshiftaction.OnShiftActionHolder;
import org.l2j.gameserver.instancemanager.ReflectionManager;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.model.actor.recorder.ServitorStatsChangeRecorder;
import org.l2j.gameserver.model.base.Experience;
import org.l2j.gameserver.model.base.TeamType;
import org.l2j.gameserver.model.entity.events.Event;
import org.l2j.gameserver.model.entity.events.impl.SingleMatchEvent;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.model.instances.PetInstance;
import org.l2j.gameserver.model.instances.SummonInstance;
import org.l2j.gameserver.model.instances.residences.SiegeToggleNpcInstance;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.model.items.PetInventory;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.*;
import org.l2j.gameserver.network.l2.s2c.NpcInfoPacket.PetInfoPacket;
import org.l2j.gameserver.network.l2.s2c.NpcInfoPacket.SummonInfoPacket;
import org.l2j.gameserver.network.l2.s2c.updatetype.IUpdateTypeComponent;
import org.l2j.gameserver.network.l2.s2c.updatetype.NpcInfoType;
import org.l2j.gameserver.skills.AbnormalEffect;
import org.l2j.gameserver.skills.TimeStamp;
import org.l2j.gameserver.stats.Stats;
import org.l2j.gameserver.taskmanager.DecayTaskManager;
import org.l2j.gameserver.templates.item.WeaponTemplate;
import org.l2j.gameserver.templates.npc.NpcTemplate;
import org.l2j.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Servitor extends Playable
{
	public static class ServitorComparator implements Comparator<Servitor>
	{
		private static final ServitorComparator _instance = new ServitorComparator();

		public static final ServitorComparator getInstance()
		{
			return _instance;
		}

		@Override
		public int compare(Servitor o1, Servitor o2)
		{
			if(o1 == null)
				return -1;

			if(o2 == null)
				return 1;

			return o1.getSummonTime() - o2.getSummonTime();
		}
	}

	public static class UsedSkill
	{
		private final Skill _skill;
		private final int _actionId;

		public UsedSkill(Skill skill, int actionId)
		{
			_skill = skill;
			_actionId = actionId;
		}

		public Skill getSkill()
		{
			return _skill;
		}

		public int getActionId()
		{
			return _actionId;
		}
	}

	public static enum AttackMode
	{
		PASSIVE,
		DEFENCE
	}

	public static final String TITLE_BY_OWNER_NAME = "%OWNER_NAME%";

	private static final Logger _log = LoggerFactory.getLogger(Servitor.class);

	private static final int SUMMON_DISAPPEAR_RANGE = 2500;

	private final Player _owner;

	private int _spawnAnimation = 2;
	protected long _exp = 0;
	protected int _sp = 0;
	private int _maxLoad;
	private boolean _follow = true, _depressed = false;
	private UsedSkill _usedSkill;

	// Charged shot's power.
	private double _chargedSoulshotPower = 0;
	private double _chargedSpiritshotPower = 0;

	private Future<?> _decayTask;

	private int _summonTime = 0;

	private int _index = 0;

	private final int _corpseTime;

	private final boolean _targetable;

	private boolean _showName;

	public Servitor(int objectId, NpcTemplate template, Player owner)
	{
		super(objectId, template);
		_owner = owner;

		if(template.getSkills().size() > 0) {
			for (Skill skill : template.getSkills().values()) {
				addSkill(skill.getEntry());
			}
		}

		setXYZ(owner.getX() + Rnd.get(-100, 100), owner.getY() + Rnd.get(-100, 100), owner.getZ());

		_corpseTime = template.getAIParams().getInteger(NpcInstance.CORPSE_TIME, NpcInstance.BASE_CORPSE_TIME);

		_targetable = template.getAIParams().getBool(NpcInstance.TARGETABLE, true);
		setTargetable(_targetable);
		setShowName(template.getAIParams().getBool(NpcInstance.SHOW_NAME, true));
	}

	@Override
	protected void onSpawn()
	{
		super.onSpawn();

		_spawnAnimation = 0;

		Player owner = getPlayer();
		Party party = owner.getParty();
		if(party != null)
			party.broadcastToPartyMembers(owner, new ExPartyPetWindowAdd(this));
		getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);

		EffectsDAO.getInstance().restoreEffects(this);

		if(owner.isInOlympiadMode())
			getAbnormalList().stopAll();

		transferOwnerBuffs();

		_summonTime = (int) (System.currentTimeMillis() / 1000);
		_index = owner.getServitorsCount();

		if(owner.isGMInvisible())
			startAbnormalEffect(AbnormalEffect.STEALTH);
	}

	@Override
	public ServitorAI getAI()
	{
		if(_ai == null)
			synchronized (this)
			{
				if(_ai == null)
					_ai = new ServitorAI(this);
			}

		return (ServitorAI) _ai;
	}

	@Override
	public NpcTemplate getTemplate()
	{
		return (NpcTemplate) super.getTemplate();
	}

	@Override
	public boolean isUndead()
	{
		return getTemplate().isUndead();
	}

	// this defines the action buttons, 1 for Summon, 2 for Pets
	public abstract int getServitorType();

	public abstract int getEffectIdentifier();

	/**
	 * @return Returns the mountable.
	 */
	public boolean isMountable()
	{
		return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onAction(final Player player, boolean shift)
	{
		Player owner = getPlayer();
		if(!isTargetable(player))
		{
			player.sendActionFailed();
			return;
		}

		if(isFrozen())
		{
			player.sendActionFailed();
			return;
		}

		if(shift && OnShiftActionHolder.getInstance().callShiftAction(player, (Class<Servitor>) getClass(), this, true))
			return;

		if(player.getTarget() != this)
		{
			player.setTarget(this);
			if(player.getTarget() == this)
				player.sendPacket(makeStatusUpdate(null, StatusUpdatePacket.CUR_HP, StatusUpdatePacket.MAX_HP, StatusUpdatePacket.CUR_MP, StatusUpdatePacket.MAX_MP));
			else
				player.sendPacket(ActionFailPacket.STATIC);
		}
		else if(player == owner)
		{
			player.sendPacket(new MyPetSummonInfoPacket(this).update());

			if(!player.isActionsDisabled())
				player.sendPacket(new PetStatusShowPacket(this));

			player.sendPacket(ActionFailPacket.STATIC);
		}
		else if(isAutoAttackable(player))
			player.getAI().Attack(this, false, shift);
		else
		{
			if(player.getAI().getIntention() != CtrlIntention.AI_INTENTION_FOLLOW)
			{
				if(!shift)
					player.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, this, Config.FOLLOW_RANGE);
				else
					player.sendActionFailed();
			}
			else
				player.sendActionFailed();
		}
	}

	public long getExpForThisLevel()
	{
		return Experience.getExpForLevel(getLevel());
	}

	public long getExpForNextLevel()
	{
		return Experience.getExpForLevel(getLevel() + 1);
	}

	@Override
	public int getNpcId()
	{
		return getTemplate().getId();
	}

	public int getDisplayId()
	{
		return getTemplate().displayId;
	}

	public final long getExp()
	{
		return _exp;
	}

	public final void setExp(final long exp)
	{
		_exp = exp;
	}

	public final int getSp()
	{
		return _sp;
	}

	public void setSp(final int sp)
	{
		_sp = sp;
	}

	@Override
	public int getMaxLoad()
	{
		return _maxLoad;
	}

	public void setMaxLoad(final int maxLoad)
	{
		_maxLoad = maxLoad;
	}

	@Override
	public int getBuffLimit()
	{
		Player owner = getPlayer();
		return (int) calcStat(Stats.BUFF_LIMIT, owner.getBuffLimit(), null, null);
	}

	public abstract int getCurrentFed();

	public abstract int getMaxFed();

	@Override
	protected void onDeath(Creature killer)
	{
		super.onDeath(killer);

		broadcastPacket(new NpcInfoState(this)); // TODO: Нужно ли здесь?

		startDecay(getCorpseTime() * 1000L);

		Player owner = getPlayer();

		if(killer == null || killer == owner || killer == this || isInZoneBattle() || killer.isInZoneBattle())
			return;

		if(killer.isServitor())
			killer = killer.getPlayer();

		if(killer == null)
			return;
		
		if(killer.isPlayer())
		{
			if(killer.isMyServitor(getObjectId()))
				return;

			if(isInSiegeZone())
				return;

			Player pk = (Player) killer;

			if(getPvpFlag() == 0 && !getPlayer().atMutualWarWith(pk) && !isPK())
			{
				boolean eventPvPFlag = true;

				for(SingleMatchEvent matchEvent : getEvents(SingleMatchEvent.class))
				{
					if (!matchEvent.canIncreasePvPPKCounter(pk, owner))
					{
						eventPvPFlag = false;
						break;
					}
				}

				if(eventPvPFlag)
				{
					int pkCountMulti = Math.max(pk.getPkKills() / 2, 1);
					pk.decreaseKarma(Config.KARMA_MIN_KARMA * pkCountMulti);

					// Send a Server->Client UserInfo packet to attacker with its PK Kills Counter
					pk.sendChanges();
				}
			}
		}
	}

	protected void startDecay(long delay)
	{
		stopDecay();
		_decayTask = DecayTaskManager.getInstance().addDecayTask(this, delay);
	}

	protected void stopDecay()
	{
		if(_decayTask != null)
		{
			_decayTask.cancel(false);
			_decayTask = null;
		}
	}

	@Override
	protected void onDecay()
	{
		deleteMe();
	}

	public void endDecayTask()
	{
		stopDecay();
		doDecay();
	}

	@Override
	public void broadcastStatusUpdate()
	{
		if(!needStatusUpdate())
			return;

		Player owner = getPlayer();

		sendStatusUpdate();

		broadcastPacket(makeStatusUpdate(null, StatusUpdatePacket.MAX_HP, StatusUpdatePacket.CUR_HP));

		Party party = owner.getParty();
		if(party != null)
			party.broadcastToPartyMembers(owner, new ExPartyPetWindowUpdate(this));
	}

	public void sendStatusUpdate()
	{
		Player owner = getPlayer();
		owner.sendPacket(new PetStatusUpdatePacket(this));
	}

	@Override
	protected void onDelete()
	{
		Player owner = getPlayer();

		Party party = owner.getParty();
		if(party != null)
			party.broadcastToPartyMembers(owner, new ExPartyPetWindowDelete(this));
		owner.sendPacket(new PetDeletePacket(getObjectId(), getServitorType()));
		owner.deleteServitor(getObjectId());

		for(Servitor servitor : owner.getServitors())
		{
			if(_index < servitor.getIndex()) // Индекс теперь свободен, уменьшаем остальным саммонам.
				servitor.setIndex(servitor.getIndex() - 1);
		}

		stopDecay();
		super.onDelete();
	}

	public void unSummon(boolean logout)
	{
		storeEffects(!logout);
		deleteMe();
	}

	public void storeEffects(boolean clean)
	{
		Player owner = getPlayer();
		if(owner == null)
			return;

		if(clean || owner.isInOlympiadMode())
			getAbnormalList().stopAll();

		EffectsDAO.getInstance().insert(this);
	}

	public void setFollowMode(boolean state)
	{
		Player owner = getPlayer();

		_follow = state;

		if(_follow)
		{
			if(getAI().getIntention() == CtrlIntention.AI_INTENTION_ACTIVE)
				getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, owner, Config.FOLLOW_RANGE);
		}
		else if(getAI().getIntention() == CtrlIntention.AI_INTENTION_FOLLOW)
			getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
	}

	public boolean isFollowMode()
	{
		return _follow;
	}

	@Override
	public void updateAbnormalIconsImpl()
	{
		Player owner = getPlayer();
		PartySpelledPacket ps = new PartySpelledPacket(this, true);
		Party party = owner.getParty();
		if(party != null)
			party.broadCast(ps);
		else
			owner.sendPacket(ps);

		super.updateAbnormalIconsImpl();
	}

	public int getControlItemObjId()
	{
		return 0;
	}

	@Override
	public PetInventory getInventory()
	{
		return null;
	}

	@Override
	public void doPickupItem(final GameObject object)
	{}

	@Override
	public void doRevive()
	{
		super.doRevive();
		setRunning();
		getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
		setFollowMode(true);
	}

	/**
	 * Return null.<BR><BR>
	 */
	@Override
	public ItemInstance getActiveWeaponInstance()
	{
		return null;
	}

	@Override
	public WeaponTemplate getActiveWeaponTemplate()
	{
		return null;
	}

	@Override
	public ItemInstance getSecondaryWeaponInstance()
	{
		return null;
	}

	@Override
	public WeaponTemplate getSecondaryWeaponTemplate()
	{
		return null;
	}

	@Override
	public void displayGiveDamageMessage(Creature target, Skill skill, int damage, Servitor servitorTransferedDamage, int transferedDamage, boolean crit, boolean miss, boolean shld, boolean blocked)
	{
		super.displayGiveDamageMessage(target, skill, damage, servitorTransferedDamage, transferedDamage, crit, miss, shld, blocked);

		if(miss)
		{
			if(skill == null)
				getPlayer().sendPacket(new SystemMessage(SystemMessage.C1S_ATTACK_WENT_ASTRAY).addName(this));
			else
				getPlayer().sendPacket(new ExMagicAttackInfo(getObjectId(), target.getObjectId(), ExMagicAttackInfo.EVADED));
			return;
		}

		if(crit)
		{
			getPlayer().sendPacket(SystemMsg.SUMMONED_MONSTERS_CRITICAL_HIT);
			getPlayer().sendPacket(new ExMagicAttackInfo(getObjectId(), target.getObjectId(), ExMagicAttackInfo.CRITICAL));
		}
		else if(blocked)
			getPlayer().sendPacket(new ExMagicAttackInfo(getObjectId(), target.getObjectId(), ExMagicAttackInfo.IMMUNE));
		else if(!target.isDoor() && !(target instanceof SiegeToggleNpcInstance))
			getPlayer().sendPacket(new SystemMessagePacket(SystemMsg.C1_HAS_DONE_S3_POINTS_OF_DAMAGE_TO_C2).addName(this).addName(target).addInteger(damage).addHpChange(target.getObjectId(), getObjectId(), -damage));
	}

	@Override
	public void displayReceiveDamageMessage(Creature attacker, int damage)
	{
		if(attacker != this)
			getPlayer().sendPacket(new SystemMessagePacket(SystemMsg.C1_HAS_RECEIVED_S3_DAMAGE_FROM_C2).addName(this).addName(attacker).addInteger(damage).addHpChange(getObjectId(), attacker.getObjectId(), -damage));
	}

	@Override
	public boolean unChargeShots(final boolean spirit)
	{
		Player owner = getPlayer();

		if(spirit)
		{
			if(_chargedSpiritshotPower > 0)
			{
				_chargedSpiritshotPower = 0;
				owner.autoShot();
				return true;
			}
		}
		else if(_chargedSoulshotPower > 0)
		{
			_chargedSoulshotPower = 0;
			owner.autoShot();
			return true;
		}

		return false;
	}

	@Override
	public double getChargedSoulshotPower()
	{
		if(_chargedSoulshotPower > 0)
			return calcStat(Stats.SOULSHOT_POWER, _chargedSoulshotPower);
		return 0.;
	}

	@Override
	public void setChargedSoulshotPower(double val)
	{
		_chargedSoulshotPower = val;
	}

	@Override
	public double getChargedSpiritshotPower()
	{
		if(_chargedSpiritshotPower > 0)
			return calcStat(Stats.SPIRITSHOT_POWER, _chargedSpiritshotPower);
		return 0.;
	}

	@Override
	public void setChargedSpiritshotPower(double val)
	{
		_chargedSpiritshotPower = val;
	}

	public int getSoulshotConsumeCount()
	{
		return 1;
	}

	public int getSpiritshotConsumeCount()
	{
		return 1;
	}

	public boolean isDepressed()
	{
		return _depressed;
	}

	public void setDepressed(final boolean depressed)
	{
		_depressed = depressed;
	}

	public boolean isInRange()
	{
		Player owner = getPlayer();
		return getDistance(owner) < SUMMON_DISAPPEAR_RANGE;
	}

	public void teleportToOwner()
	{
		Player owner = getPlayer();

		setNonAggroTime(System.currentTimeMillis() + Config.NONAGGRO_TIME_ONTELEPORT);
		setNonPvpTime(System.currentTimeMillis() + Config.NONPVP_TIME_ONTELEPORT);
		if(owner.isInOlympiadMode())
			teleToLocation(owner.getLoc(), owner.getReflection());
		else
			teleToLocation(Location.findPointToStay(owner, 50, 150), owner.getReflection());

		if(!isDead() && _follow)
			getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, owner, Config.FOLLOW_RANGE);
	}

	private ScheduledFuture<?> _broadcastCharInfoTask;

	public class BroadcastCharInfoTask extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			broadcastCharInfoImpl(NpcInfoType.VALUES);
			_broadcastCharInfoTask = null;
		}
	}

	@Override
	public void broadcastCharInfo()
	{
		if(_broadcastCharInfoTask != null)
			return;

		_broadcastCharInfoTask = ThreadPoolManager.getInstance().schedule(new BroadcastCharInfoTask(), Config.BROADCAST_CHAR_INFO_INTERVAL);
	}

	@Override
	public void broadcastCharInfoImpl(IUpdateTypeComponent... components)
	{
		broadcastCharInfoImpl(World.getAroundObservers(this), components);
	}

	public void broadcastCharInfoImpl(Iterable<Player> players, IUpdateTypeComponent... components)
	{
		if (components.length == 0)
		{
			_log.warn(getClass().getSimpleName() + ": Trying broadcast char info without components!", new Exception());
			return;
		}

		Player owner = getPlayer();

		for(Player player : players)
		{
			if(player == owner)
				player.sendPacket(new MyPetSummonInfoPacket(this).update());
			else if(!owner.isInvisible(player))
			{
				if(isPet())
					player.sendPacket(new NpcInfoPacket.PetInfoPacket((PetInstance) this, player).update(components));
				else if(isSummon())
					player.sendPacket(new SummonInfoPacket((SummonInstance) this, player).update(components));
				else
					player.sendPacket(new NpcInfoPacket(this, player).update(components));
			}
		}
	}

	private Future<?> _petInfoTask;

	private class PetInfoTask extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			sendPetInfoImpl();
			_petInfoTask = null;
		}
	}

	private void sendPetInfoImpl()
	{
		Player owner = getPlayer();
		owner.sendPacket(new MyPetSummonInfoPacket(this).update());
	}

	public void sendPetInfo()
	{
		sendPetInfo(false);
	}

	public void sendPetInfo(boolean force)
	{
		if(Config.USER_INFO_INTERVAL == 0 || force)
		{
			if(_petInfoTask != null)
			{
				_petInfoTask.cancel(false);
				_petInfoTask = null;
			}
			sendPetInfoImpl();
			return;
		}

		if(_petInfoTask != null)
			return;

		_petInfoTask = ThreadPoolManager.getInstance().schedule(new PetInfoTask(), Config.USER_INFO_INTERVAL);
	}

	/**
	 * Нужно для отображения анимации спауна, используется в пакете NpcInfo, PetInfo:
	 * 0=false, 1=true, 2=summoned (only works if model has a summon animation)
	 **/
	public int getSpawnAnimation()
	{
		return _spawnAnimation;
	}

	@Override
	public void startPvPFlag(Creature target)
	{
		Player owner = getPlayer();
		owner.startPvPFlag(target);
	}

	@Override
	public int getPvpFlag()
	{
		Player owner = getPlayer();
		return owner.getPvpFlag();
	}

	@Override
	public int getKarma()
	{
		Player owner = getPlayer();
		return owner.getKarma();
	}

	@Override
	public TeamType getTeam()
	{
		Player owner = getPlayer();
		return owner.getTeam();
	}

	@Override
	public Player getPlayer()
	{
		return _owner;
	}

	public abstract double getExpPenalty();

	@Override
	public ServitorStatsChangeRecorder getStatsRecorder()
	{
		if(_statsRecorder == null)
			synchronized (this)
			{
				if(_statsRecorder == null)
					_statsRecorder = new ServitorStatsChangeRecorder(this);
			}

		return (ServitorStatsChangeRecorder) _statsRecorder;
	}

	@Override
	public List<L2GameServerPacket> addPacketList(Player forPlayer, Creature dropper)
	{
		List<L2GameServerPacket> list = new ArrayList<L2GameServerPacket>();
		Player owner = getPlayer();

		if(owner == forPlayer)
		{
			list.add(new MyPetSummonInfoPacket(this));
			list.add(new PartySpelledPacket(this, true));
			if(getNpcState() != 101)
				list.add(new ExChangeNPCState(getObjectId(), getNpcState()));

			if(isPet())
				list.add(new PetItemListPacket((PetInstance) this));
		}
		else if(!getPlayer().isInvisible(forPlayer))
		{
			Party party = forPlayer.getParty();
			if(getReflection() == ReflectionManager.GIRAN_HARBOR && (owner == null || party == null || party != owner.getParty()))
				return list;

			if(isPet())
				list.add(new PetInfoPacket((PetInstance) this, forPlayer).init());
			else if(isSummon())
				list.add(new SummonInfoPacket((SummonInstance) this, forPlayer).init());
			else
				list.add(new NpcInfoPacket(this, forPlayer).init());

			if(owner != null && party != null && party == owner.getParty())
				list.add(new PartySpelledPacket(this, true));
		}
		else
			return Collections.emptyList();

		if(isInCombat())
			list.add(new AutoAttackStartPacket(getObjectId()));

		if(owner != forPlayer)
			list.add(new RelationChangedPacket(this, forPlayer));

		if(isInBoat())
			list.add(getBoat().getOnPacket(this, getInBoatPosition()));
		else
		{
			if(isMoving || isFollow)
				list.add(movePacket());
		}
		return list;
	}

	@Override
	public void startAttackStanceTask()
	{
		startAttackStanceTask0();
		Player player = getPlayer();
		if(player != null)
			player.startAttackStanceTask0();
	}

	@Override
	public <E extends Event> E getEvent(Class<E> eventClass)
	{
		Player player = getPlayer();
		if(player != null)
			return player.getEvent(eventClass);
		else
			return super.getEvent(eventClass);
	}

	@Override
	public <E extends Event> List<E> getEvents(Class<E> eventClass)
	{
		Player player = getPlayer();
		if(player != null)
			return player.getEvents(eventClass);
		else
			return super.getEvents(eventClass);
	}

	@Override
	public Set<Event> getEvents()
	{
		Player player = getPlayer();
		if(player != null)
			return player.getEvents();
		else
			return super.getEvents();
	}

	@Override
	public void sendReuseMessage(Skill skill)
	{
		Player player = getPlayer();
		if(player != null && isSkillDisabled(skill))
		{
			TimeStamp sts = getSkillReuse(skill);
			if(sts == null || !sts.hasNotPassed())
				return;
			long timeleft = sts.getReuseCurrent();
			if(!Config.ALT_SHOW_REUSE_MSG && timeleft < 10000 || timeleft < 500)
				return;
			long hours = timeleft / 3600000;
			long minutes = (timeleft - hours * 3600000) / 60000;
			long seconds = (long) Math.ceil((timeleft - hours * 3600000 - minutes * 60000) / 1000.);
			if(hours > 0)
				player.sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S2_HOURS_S3_MINUTES_AND_S4_SECONDS_REMAINING_IN_S1S_REUSE_TIME).addSkillName(skill.getId(), skill.getDisplayLevel()).addNumber(hours).addNumber(minutes).addNumber(seconds));
			else if(minutes > 0)
				player.sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S2_MINUTES_S3_SECONDS_REMAINING_IN_S1S_REUSE_TIME).addSkillName(skill.getId(), skill.getDisplayLevel()).addNumber(minutes).addNumber(seconds));
			else
				player.sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S2_SECONDS_REMAINING_IN_S1S_REUSE_TIME).addSkillName(skill.getId(), skill.getDisplayLevel()).addNumber(seconds));
		
		}
	}

	@Override
	public boolean isServitor()
	{
		return true;
	}

	public boolean isHungry()
	{
		return false;
	}

	public boolean isNotControlled()
	{
		return false;
	}

	public int getNpcState()
	{
		return 101;
	}

	public void onAttacked(Creature attacker)
	{}

	public void onOwnerGotAttacked(Creature attacker)
	{
		onAttacked(attacker);
	}

	public void onOwnerOfAttacks(Creature target)
	{}

	public void setAttackMode(AttackMode mode)
	{}

	public AttackMode getAttackMode()
	{
		return AttackMode.PASSIVE;
	}

	public void transferOwnerBuffs()
	{
		Collection<Abnormal> abnormals = getPlayer().getAbnormalList().values();
		for(Abnormal a : abnormals)
		{
			Skill skill = a.getSkill();
			if(a.isOffensive() || skill.isToggle() || skill.isCubicSkill())
				continue;

			if(isSummon() && !skill.applyEffectsOnSummon())
				continue;

			if(isPet() && !skill.applyEffectsOnPet())
				continue;

			Abnormal abnormal = new Abnormal(a.getEffector(), this, a);

			abnormal.setDuration(a.getDuration());
			abnormal.setTimeLeft(a.getTimeLeft());
			getAbnormalList().add(abnormal);
		}
	}

	@Override
	public boolean checkPvP(final Creature target, Skill skill)
	{
		if(target != this && target.isServitor() && getPlayer().isMyServitor(target.getObjectId()))
		{
			if(skill == null || skill.isOffensive())
				return true;
		}

		return super.checkPvP(target, skill);
	}

	public UsedSkill getUsedSkill()
	{
		return _usedSkill;
	}

	public void setUsedSkill(Skill skill, int actionId)
	{
		_usedSkill = new UsedSkill(skill, actionId);
	}

	public void setUsedSkill(UsedSkill usedSkill)
	{
		_usedSkill = usedSkill;
	}

	public void notifyMasterDeath()
	{
		getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
		setFollowMode(true);		
	}

	public int getSummonTime()
	{
		return _summonTime;
	}

	@Override
	public boolean isSpecialAbnormal(Skill skill)
	{
		if(getPlayer() != null)
			return getPlayer().isSpecialAbnormal(skill);

		return false;
	}

	protected int getCorpseTime()
	{
		return _corpseTime;
	}

	public void setIndex(int index)
	{
		_index = index;
	}

	public int getIndex()
	{
		return _index;
	}

	public boolean isShowName()
	{
		return _showName;
	}

	public void setShowName(boolean value)
	{
		_showName = value;
	}

	@Override
	protected L2GameServerPacket changeMovePacket()
	{
		return new NpcInfoState(this);
	}

	@Override
	public boolean isInvisible(GameObject observer)
	{
		Player owner = getPlayer();
		if(owner != null)
		{
			if(owner == observer)
				return false;
			if(observer != null)
			{
				if(observer.isPlayer())
				{
					Player observPlayer = (Player) observer;
					if(owner.isInSameParty(observPlayer))
						return false;
				}
			}
			if(owner.isGMInvisible())
				return true;
		}
		return super.isInvisible(observer);
	}

	@Override
	public boolean isTargetable(Creature creature)
	{
		if (!_targetable)
			return false;
		if (getPlayer() == creature)
			return true;
		return super.isTargetable(creature);
	}

	@Override
	public int getPAtk(Creature target)
	{
		return (int) (super.getPAtk(target) * Config.SERVITOR_P_ATK_MODIFIER);
	}

	@Override
	public int getPDef(Creature target)
	{
		return (int) (super.getPDef(target) * Config.SERVITOR_P_DEF_MODIFIER);
	}

	@Override
	public int getMAtk(Creature target, Skill skill)
	{
		return (int)(super.getMAtk(target, skill) * Config.SERVITOR_M_ATK_MODIFIER);
	}

	@Override
	public int getMDef(Creature target, Skill skill)
	{
		return (int)(super.getMDef(target, skill) * Config.SERVITOR_M_DEF_MODIFIER);
	}
}