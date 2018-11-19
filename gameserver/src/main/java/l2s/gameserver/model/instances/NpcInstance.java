package l2s.gameserver.model.instances;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;

import l2s.commons.collections.MultiValueSet;
import l2s.commons.lang.reference.HardReference;
import l2s.commons.lang.reference.HardReferences;
import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.Config;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.ai.NpcAI;
import l2s.gameserver.data.QuestHolder;
import l2s.gameserver.data.htm.HtmCache;
import l2s.gameserver.data.xml.holder.ItemHolder;
import l2s.gameserver.data.xml.holder.MultiSellHolder;
import l2s.gameserver.data.xml.holder.ResidenceHolder;
import l2s.gameserver.data.xml.holder.SkillAcquireHolder;
import l2s.gameserver.geodata.GeoEngine;
import l2s.gameserver.handler.bypass.BypassHolder;
import l2s.gameserver.handler.onshiftaction.OnShiftActionHolder;
import l2s.gameserver.idfactory.IdFactory;
import l2s.gameserver.instancemanager.EventTriggersManager;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.listener.NpcListener;
import l2s.gameserver.listener.hooks.ListenerHook;
import l2s.gameserver.listener.hooks.ListenerHookType;
import l2s.gameserver.model.AggroList;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.MinionList;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.SkillLearn;
import l2s.gameserver.model.Spawner;
import l2s.gameserver.model.Territory;
import l2s.gameserver.model.World;
import l2s.gameserver.model.Zone.ZoneType;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.model.actor.listener.NpcListenerList;
import l2s.gameserver.model.actor.recorder.NpcStatsChangeRecorder;
import l2s.gameserver.model.base.AcquireType;
import l2s.gameserver.model.base.MountType;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.entity.events.Event;
import l2s.gameserver.model.entity.residence.Castle;
import l2s.gameserver.model.entity.residence.ClanHall;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.model.pledge.SubUnit;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestEventType;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.c2s.L2GameClientPacket;
import l2s.gameserver.network.l2.c2s.RequestItemEnsoul;
import l2s.gameserver.network.l2.c2s.RequestTryEnSoulExtraction;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.network.l2.components.HtmlMessage;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.*;
import l2s.gameserver.network.l2.s2c.updatetype.IUpdateTypeComponent;
import l2s.gameserver.network.l2.s2c.updatetype.NpcInfoType;
import l2s.gameserver.skills.EffectType;
import l2s.gameserver.skills.effects.Effect;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.tables.ClanTable;
import l2s.gameserver.taskmanager.DecayTaskManager;
import l2s.gameserver.taskmanager.LazyPrecisionTaskManager;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.templates.TeleportLocation;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.templates.item.WeaponTemplate;
import l2s.gameserver.templates.npc.BuyListTemplate;
import l2s.gameserver.templates.npc.Faction;
import l2s.gameserver.templates.npc.MinionData;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.templates.spawn.SpawnRange;
import l2s.gameserver.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NpcInstance extends Creature
{
	private static final long serialVersionUID = 1L;

	public static final int BASE_CORPSE_TIME = 7;
	public static final String CORPSE_TIME = "corpse_time";

	public static final String NO_CHAT_WINDOW = "noChatWindow";
	public static final String NO_RANDOM_WALK = "noRandomWalk";
	public static final String NO_RANDOM_ANIMATION = "noRandomAnimation";
	public static final String NO_SHIFT_CLICK = "noShiftClick";
	public static final String NO_LETHAL = "noLethal";
	public static final String TARGETABLE = "targetable";
	public static final String SHOW_NAME = "show_name";
	public static final String NO_SLEEP_MODE = "no_sleep_mode";
	public static final String IS_IMMORTAL = "is_immortal";
	public static final String EVENT_TRIGGER_ID = "event_trigger_id";
	public static final String STATE_ID_VAR = "state_id";

	private static final Logger _log = LoggerFactory.getLogger(NpcInstance.class);

	private int _personalAggroRange = -1;
	private int _level = 0;

	private long _deathTime = 0L;

	protected int _spawnAnimation = 2;

	private int _currentLHandId;
	private int _currentRHandId;

	private double _collisionHeightModifier = 1.0;
	private double _collisionRadiusModifier = 1.0;

	private int npcState = 0;

	protected boolean _hasRandomAnimation;
	protected boolean _hasRandomWalk;
	protected boolean _hasChatWindow;

	private Future<?> _decayTask;
	private Future<?> _animationTask;

	private AggroList _aggroList;

	private boolean _noLethal;
	private boolean _showName;
	private boolean _noShiftClick;

	private Castle _nearestCastle;
	private ClanHall _nearestClanHall;

	private NpcString _nameNpcString = NpcString.NONE;
	private NpcString _titleNpcString = NpcString.NONE;

	private Spawner _spawn;
	private Location _spawnedLoc = new Location();
	private SpawnRange _spawnRange;

	private NpcInstance _master = null;
	private MinionList _minionList = null;

	private MultiValueSet<String> _parameters = StatsSet.EMPTY;

	private final int _enchantEffect;

	@SuppressWarnings("unused")
	private final boolean _isNoSleepMode;

	private final int _corpseTime;

	private final boolean _isImmortal;

	private HardReference<Player> _ownerRef = HardReferences.emptyRef();
	private final TIntSet _eventTriggers = new TIntHashSet();
	private final String _supportSpawnGroup;

	public NpcInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template);

		if(template == null)
			throw new NullPointerException("No template for Npc. Please check your datapack is setup correctly.");

		setParameters(template.getAIParams());
		setParameters(set);

		_hasRandomAnimation = !getParameter(NO_RANDOM_ANIMATION, false) && Config.MAX_NPC_ANIMATION > 0;
		_hasRandomWalk = !getParameter(NO_RANDOM_WALK, false);
		_noShiftClick = getParameter(NO_SHIFT_CLICK, isPeaceNpc());
		_noLethal = getParameter(NO_LETHAL, false);
		setHasChatWindow(!getParameter(NO_CHAT_WINDOW, false));
		setTargetable(getParameter(TARGETABLE, true));
		setShowName(getParameter(SHOW_NAME, true));
		npcState = getParameter(STATE_ID_VAR, 0);

		_isImmortal = getParameter(IS_IMMORTAL, false);

		for(Skill skill : template.getSkills().valueCollection())
			addSkill(skill.getEntry());

		setName(template.name);
		
		String customTitle = template.title;
		if(isMonster() && Config.ALT_SHOW_MONSTERS_LVL)
		{
			customTitle = "LvL: " + this.getLevel();
			if(Config.ALT_SHOW_MONSTERS_AGRESSION && this.isAggressive())
				customTitle += " A";
		}		
		setTitle(customTitle);

		// инициализация параметров оружия
		setLHandId(getTemplate().lhand);
		setRHandId(getTemplate().rhand);

		_aggroList = new AggroList(this);

		setFlying(getParameter("isFlying", false));

		
		int enchant = Math.min(127, getTemplate().getEnchantEffect());
		if(enchant == 0 && Config.NPC_RANDOM_ENCHANT)
			enchant = Rnd.get(0, 18);

		_enchantEffect = enchant;

		_isNoSleepMode = getParameter(NO_SLEEP_MODE, false);
		_corpseTime = getParameter(CORPSE_TIME, BASE_CORPSE_TIME);
		_supportSpawnGroup = getParameter("support_spawn_group", null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public HardReference<NpcInstance> getRef()
	{
		return (HardReference<NpcInstance>) super.getRef();
	}

	@Override
	public NpcAI getAI()
	{
		if(_ai == null)
			synchronized (this)
			{
				if(_ai == null)
					_ai = getTemplate().getNewAI(this);
			}

		return (NpcAI) _ai;
	}

	/**
	 * Return the position of the spawned point.<BR><BR>
	 * Может возвращать случайную точку, поэтому всегда следует кешировать результат вызова!
	 */
	public Location getSpawnedLoc()
	{
		return getLeader() != null ? getLeader().getLoc() : _spawnedLoc;
	}

	public void setSpawnedLoc(Location loc)
	{
		_spawnedLoc = loc;
	}

	public int getRightHandItem()
	{
		return _currentRHandId;
	}

	public int getLeftHandItem()
	{
		return _currentLHandId;
	}

	public void setLHandId(int newWeaponId)
	{
		_currentLHandId = newWeaponId;
	}

	public void setRHandId(int newWeaponId)
	{
		_currentRHandId = newWeaponId;
	}

	@Override
	protected void onReduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean isDot)
	{
		Creature damager = attacker;
		if(attacker.isConfused())
		{
			for(Abnormal abnormal : attacker.getAbnormalList())
			{
				for(Effect effect : abnormal.getEffects())
				{
					if(effect.getEffectType() == EffectType.Discord)
					{
						damager = effect.getEffector();
						break;
					}
				}
			}
		}
		getAggroList().addDamageHate(damager, (int) damage, 0);

		super.onReduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, isDot);
	}

	@Override
	protected void onDeath(Creature killer)
	{
		_deathTime = System.currentTimeMillis();

		if(isMonster() && ((MonsterInstance) this).isSpoiled())
			startDecay(20000L);
		else
			startDecay(_corpseTime * 1000L);

		// установка параметров оружия и коллизий по умолчанию
		setLHandId(getTemplate().lhand);
		setRHandId(getTemplate().rhand);

		getAI().stopAITask();
		stopAttackStanceTask();
		stopRandomAnimation();

		if(getLeader() != null)
			getLeader().notifyMinionDied(this);

		if(hasMinions())
			getMinionList().onMasterDeath();

		for(ListenerHook hook : getTemplate().getListenerHooks(ListenerHookType.NPC_KILL))
		{
			if(killer != null && killer.getPlayer() != null)
				hook.onNpcKill(this, killer.getPlayer());
		}
		for(ListenerHook hook : ListenerHook.getGlobalListenerHooks(ListenerHookType.NPC_KILL))
		{
			if(killer != null && killer.getPlayer() != null)
				hook.onNpcKill(this, killer.getPlayer());
		}

		super.onDeath(killer);
		broadcastPacket(new NpcInfoState(this));
	}

	public final long getDeathTime()
	{
		return _deathTime;
	}

	public AggroList getAggroList()
	{
		return _aggroList;
	}

	public void setLeader(final NpcInstance leader)
	{
		_master = leader;
	}

	public NpcInstance getLeader()
	{
		return _master;
	}

	@Override
	public boolean isMinion()
	{
		return getLeader() != null;
	}

	public MinionList getMinionList()
	{
		if(_minionList == null)
			_minionList = new MinionList(this);
		return _minionList;
	}

	public boolean hasMinions()
	{
		return _minionList != null && _minionList.hasMinions();
	}

	public void notifyMinionDied(NpcInstance minion)
	{}

	public Location getRndMinionPosition()
	{
		int offset = 200;
		int minRadius = (int) getCollisionRadius() + 30;
		int x = Rnd.get(minRadius * 2, 400);
		int y = Rnd.get(x, 400);
		y = (int) Math.sqrt(y * y - x * x);
		if(x > 200 + minRadius)
			x = getX() + x - 200;
		else
			x = getX() - x + minRadius;

		if(y > 200 + minRadius)
			y = getY() + y - 200;
		else
			y = getY() - y + minRadius;

		return new Location(x, y, getZ());
	}

	public void onSpawnMinion(NpcInstance minion)
	{}

	@Override
	public boolean setReflection(Reflection reflection)
	{
		if(!super.setReflection(reflection))
			return false;

		if(hasMinions())
		{
			for(NpcInstance m : getMinionList().getAliveMinions())
				m.setReflection(reflection);
		}
		return true;
	}

	public void dropItem(Player lastAttacker, int itemId, long itemCount)
	{
		if(itemCount == 0 || lastAttacker == null)
			return;

		ItemInstance item;

		for(long i = 0; i < itemCount; i++)
		{
			item = ItemFunctions.createItem(itemId);
			for(Event e : getEvents())
				item.addEvent(e);

			// Set the Item quantity dropped if L2ItemInstance is stackable
			if(item.isStackable())
			{
				i = itemCount; // Set so loop won't happent again
				item.setCount(itemCount); // Set item count
			}

			if(isRaid() || this instanceof ReflectionBossInstance)
			{
				SystemMessagePacket sm;
				if(itemId == 57)
				{
					sm = new SystemMessagePacket(SystemMsg.C1_HAS_DIED_AND_DROPPED_S2_ADENA);
					sm.addName(this);
					sm.addLong(item.getCount());
				}
				else
				{
					sm = new SystemMessagePacket(SystemMsg.C1_DIED_AND_DROPPED_S3_S2);
					sm.addName(this);
					sm.addItemName(itemId);
					sm.addLong(item.getCount());
				}
				broadcastPacket(sm);
			}

			lastAttacker.doAutoLootOrDrop(item, this);
		}
	}

	public void dropItem(Player lastAttacker, ItemInstance item)
	{
		if(item.getCount() == 0)
			return;

		if(isRaid() || this instanceof ReflectionBossInstance)
		{
			SystemMessagePacket sm;
			if(item.getItemId() == 57)
			{
				sm = new SystemMessagePacket(SystemMsg.C1_HAS_DIED_AND_DROPPED_S2_ADENA);
				sm.addName(this);
				sm.addLong(item.getCount());
			}
			else
			{
				sm = new SystemMessagePacket(SystemMsg.C1_DIED_AND_DROPPED_S3_S2);
				sm.addName(this);
				sm.addItemName(item.getItemId());
				sm.addLong(item.getCount());
			}
			broadcastPacket(sm);
		}

		lastAttacker.doAutoLootOrDrop(item, this);
	}

	@Override
	public boolean isAttackable(Creature attacker)
	{
		return true;
	}

	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		return false;
	}

	@Override
	protected void onSpawn()
	{
		super.onSpawn();

		setCurrentHpMp(getMaxHp(), getMaxMp(), true);

		_deathTime = 0L;
		_spawnAnimation = 0;

		getAI().notifyEvent(CtrlEvent.EVT_SPAWN);
		getListeners().onSpawn();
		for(ListenerHook hook : getTemplate().getListenerHooks(ListenerHookType.NPC_SPAWN))
			hook.onNpcSpawn(this);

		if(getAI().isGlobalAI() || getCurrentRegion() != null && getCurrentRegion().isActive())
		{
			getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			getAI().startAITask();
			startRandomAnimation();
		}

		if(!hasMinions())
		{
			List<MinionData> minionsData = getTemplate().getMinionData();
			if(!minionsData.isEmpty())
			{
				for(MinionData minionData : minionsData)
					getMinionList().addMinion(minionData);
			}
		}

		if(hasMinions())
			ThreadPoolManager.getInstance().schedule(() -> getMinionList().spawnMinions(), 1500L);

		if(getLeader() != null)
			getLeader().onSpawnMinion(this);

		if(_supportSpawnGroup != null)
			getReflection().spawnByGroup(_supportSpawnGroup);

		int eventTriggerId = getParameter(EVENT_TRIGGER_ID, 0);
		if(eventTriggerId != 0)
			_eventTriggers.add(eventTriggerId);

		for(int triggerId : _eventTriggers.toArray())
		{
			if(getReflection().isMain())
				EventTriggersManager.getInstance().addTrigger(MapUtils.regionX(getX()), MapUtils.regionY(getY()), triggerId);
			else
				EventTriggersManager.getInstance().addTrigger(getReflection(), triggerId);
		}
	}

	@Override
	protected void onDespawn()
	{
		getAggroList().clear();

		stopRandomAnimation();
		getAI().stopAITask();
		getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);

		getAI().notifyEvent(CtrlEvent.EVT_DESPAWN);

		super.onDespawn();

		if(_supportSpawnGroup != null)
			getReflection().despawnByGroup(_supportSpawnGroup);

		for(ListenerHook hook : getTemplate().getListenerHooks(ListenerHookType.NPC_DESPAWN))
			hook.onNpcDespawn(this);

		for(int triggerId : _eventTriggers.toArray())
		{
			if(getReflection().isMain())
				EventTriggersManager.getInstance().removeTrigger(MapUtils.regionX(getX()), MapUtils.regionY(getY()), triggerId);
			else
				EventTriggersManager.getInstance().removeTrigger(getReflection(), triggerId);
		}

		_eventTriggers.clear();
	}

	@Override
	public NpcTemplate getTemplate()
	{
		return (NpcTemplate) super.getTemplate();
	}

	@Override
	public int getNpcId()
	{
		return getTemplate().getId();
	}

	protected boolean _unAggred = false;

	public void setUnAggred(boolean state)
	{
		_unAggred = state;
	}

	/**
	 * Return True if the L2NpcInstance is aggressive (ex : L2MonsterInstance in function of aggroRange).<BR><BR>
	 */
	public boolean isAggressive()
	{
		return getAggroRange() > 0;
	}

	public int getAggroRange()
	{
		if(_unAggred)
			return 0;

		if(_personalAggroRange >= 0)
			return _personalAggroRange;

		return getTemplate().aggroRange;
	}

	/**
	 * Устанавливает данному npc новый aggroRange.
	 * Если установленый aggroRange < 0, то будет братся аггрорейндж с темплейта.
	 * @param aggroRange новый agrroRange
	 */
	public void setAggroRange(int aggroRange)
	{
		_personalAggroRange = aggroRange;
	}

	/**
	 * Возвращает группу социальности
	 */
	public Faction getFaction()
	{
		return getTemplate().getFaction();
	}

	public boolean isInFaction(NpcInstance npc)
	{
		return getFaction().equals(npc.getFaction()) && !getFaction().isIgnoreNpcId(npc.getNpcId());
	}

	@Override
	public int getMAtk(Creature target, Skill skill)
	{
		return (int) (super.getMAtk(target, skill) * Config.ALT_NPC_MATK_MODIFIER);
	}

	@Override
	public int getPAtk(Creature target)
	{
		return (int) (super.getPAtk(target) * Config.ALT_NPC_PATK_MODIFIER);
	}

	@Override
	public int getMaxHp()
	{
		return (int) (super.getMaxHp() * Config.ALT_NPC_MAXHP_MODIFIER);
	}

	@Override
	public int getMaxMp()
	{
		return (int) (super.getMaxMp() * Config.ALT_NPC_MAXMP_MODIFIER);
	}

	public long getExpReward()
	{
		return (long) calcStat(Stats.EXP_RATE_MULTIPLIER, getTemplate().rewardExp, null, null);
	}

	public long getSpReward()
	{
		return (long) calcStat(Stats.SP_RATE_MULTIPLIER, getTemplate().rewardSp, null, null);
	}

	@Override
	protected void onDelete()
	{
		getAI().stopAllTaskAndTimers();

		stopDecay();
		if(_spawn != null)
			_spawn.stopRespawn();
		setSpawn(null);

		if(hasMinions())
			getMinionList().onMasterDelete();

		NpcInstance leader = getLeader();
		if(leader != null && leader.hasMinions())
			leader.getMinionList().onMinionDelete(this);

		super.onDelete();
	}

	public Spawner getSpawn()
	{
		return _spawn;
	}

	public void setSpawn(Spawner spawn)
	{
		_spawn = spawn;
	}

	public final void decayOrDelete()
	{
		onDecay();
	}

	@Override
	protected void onDecay()
	{
		super.onDecay();

		_spawnAnimation = 2;

		if(!hasMinions() || !getMinionList().hasAliveMinions())
		{
			if(_spawn != null)
				_spawn.decreaseCount(this);
			else
				deleteMe(); // Если этот моб заспавнен не через стандартный механизм спавна значит посмертие ему не положено и он умирает насовсем
		}
	}

	/**
	 * Запустить задачу "исчезновения" после смерти
	 */
	protected void startDecay(long delay)
	{
		stopDecay();
		_decayTask = DecayTaskManager.getInstance().addDecayTask(this, delay);
	}

	/**
	 * Отменить задачу "исчезновения" после смерти
	 */
	public void stopDecay()
	{
		if(_decayTask != null)
		{
			_decayTask.cancel(false);
			_decayTask = null;
		}
	}

	/**
	 * Отменить и завершить задачу "исчезновения" после смерти
	 */
	public void endDecayTask()
	{
		if(_decayTask != null)
		{
			_decayTask.cancel(false);
			_decayTask = null;
		}
		doDecay();
	}

	@Override
	public boolean isUndead()
	{
		return getTemplate().isUndead();
	}

	public void setLevel(int level)
	{
		_level = level;
	}

	@Override
	public int getLevel()
	{
		return _level == 0 ? getTemplate().level : _level;
	}

	private int _displayId = 0;

	public void setDisplayId(int displayId)
	{
		_displayId = displayId;
	}

	public int getDisplayId()
	{
		return _displayId > 0 ? _displayId : getTemplate().displayId;
	}

	@Override
	public ItemInstance getActiveWeaponInstance()
	{
		// regular NPCs dont have weapons instancies
		return null;
	}

	@Override
	public WeaponTemplate getActiveWeaponTemplate()
	{
		// Get the weapon identifier equipped in the right hand of the L2NpcInstance
		int weaponId = getTemplate().rhand;

		if(weaponId < 1)
			return null;

		// Get the weapon item equipped in the right hand of the L2NpcInstance
		ItemTemplate item = ItemHolder.getInstance().getTemplate(getTemplate().rhand);

		if(!(item instanceof WeaponTemplate))
			return null;

		return (WeaponTemplate) item;
	}

	@Override
	public ItemInstance getSecondaryWeaponInstance()
	{
		// regular NPCs dont have weapons instances
		return null;
	}

	@Override
	public WeaponTemplate getSecondaryWeaponTemplate()
	{
		// Get the weapon identifier equipped in the right hand of the L2NpcInstance
		int weaponId = getTemplate().lhand;

		if(weaponId < 1)
			return null;

		// Get the weapon item equipped in the right hand of the L2NpcInstance
		ItemTemplate item = ItemHolder.getInstance().getTemplate(getTemplate().lhand);

		if(!(item instanceof WeaponTemplate))
			return null;

		return (WeaponTemplate) item;
	}

	@Override
	public void sendChanges()
	{
		if(isFlying()) // FIXME
			return;
		super.sendChanges();
	}

	private ScheduledFuture<?> _broadcastCharInfoTask;

	public void onMenuSelect(Player player, int ask, long reply)
	{
		for(QuestState qs : player.getAllQuestsStates())
		{
			if(qs.getQuest().getId() == ask && !qs.isCompleted())
			{
				qs.getQuest().notifyMenuSelect((int) reply, qs, this);
				return;
			}
		}

		if(ask == -303)
		{
			Castle castle = getCastle(player);
			MultiSellHolder.getInstance().SeparateAndSend((int) reply, player, (castle != null) ? castle.getSellTaxRate() : 0.0);
		}
		else if(ask == -1000)
		{
			if(reply == 1L)
				onBypassFeedback(player, "TerritoryStatus");
		}
		else if(ask == -1816)
			onBypassFeedback(player, "teleport_fi_to");
		else if(ask == 255)
			onBypassFeedback(player, "teleport_mdt_to");

		getAI().notifyEvent(CtrlEvent.EVT_MENU_SELECTED, player, ask, reply);

		for(ListenerHook hook : getTemplate().getListenerHooks(ListenerHookType.NPC_ASK))
			hook.onNpcAsk(this, ask, reply, player);

		for(ListenerHook hook : ListenerHook.getGlobalListenerHooks(ListenerHookType.NPC_ASK))
			hook.onNpcAsk(this, ask, reply, player);
	}

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
		if(!isVisible())
			return;

		if(_broadcastCharInfoTask != null)
			return;

		_broadcastCharInfoTask = ThreadPoolManager.getInstance().schedule(new BroadcastCharInfoTask(), Config.BROADCAST_CHAR_INFO_INTERVAL);
	}

	@Override
	public void broadcastCharInfoImpl(IUpdateTypeComponent... components)
	{
		if(components.length == 0)
		{
			_log.warn(getClass().getSimpleName() + ": Trying broadcast char info without components!", new Exception());
			return;
		}

		for(Player player : World.getAroundObservers(this))
			player.sendPacket(new NpcInfoPacket(this, player).update(components));
	}

	// У NPC всегда 2
	public void onRandomAnimation()
	{
		if(System.currentTimeMillis() - _lastSocialAction > 10000L)
		{
			broadcastPacket(new SocialActionPacket(getObjectId(), 2));
			_lastSocialAction = System.currentTimeMillis();
		}
	}

	public void startRandomAnimation()
	{
		if(!hasRandomAnimation())
			return;
		_animationTask = LazyPrecisionTaskManager.getInstance().addNpcAnimationTask(this);
	}

	public void stopRandomAnimation()
	{
		if(_animationTask != null)
		{
			_animationTask.cancel(false);
			_animationTask = null;
		}
	}

	public boolean hasRandomAnimation()
	{
		return _hasRandomAnimation;
	}

	public void setHaveRandomAnim(boolean value)
	{
		_hasRandomAnimation = value;
	}

	public boolean hasRandomWalk()
	{
		return _hasRandomWalk;
	}

	public void setRandomWalk(boolean value)
	{
		_hasRandomWalk = value;
	}

	public Castle getCastle()
	{
		if(getReflection() == ReflectionManager.PARNASSUS && Config.SERVICES_PARNASSUS_NOTAX)
			return null;
		if(Config.SERVICES_OFFSHORE_NO_CASTLE_TAX && getReflection() == ReflectionManager.GIRAN_HARBOR)
			return null;
		if(Config.SERVICES_OFFSHORE_NO_CASTLE_TAX && getReflection() == ReflectionManager.PARNASSUS)
			return null;
		if(Config.SERVICES_OFFSHORE_NO_CASTLE_TAX && isInZone(ZoneType.offshore))
			return null;
		if(_nearestCastle == null)
			_nearestCastle = ResidenceHolder.getInstance().getResidence(getTemplate().getCastleId());
		return _nearestCastle;
	}

	public Castle getCastle(Player player)
	{
		return getCastle();
	}

	public ClanHall getClanHall()
	{
		if(_nearestClanHall == null)
			_nearestClanHall = ResidenceHolder.getInstance().findNearestResidence(ClanHall.class, getX(), getY(), getZ(), getReflection(), 32768);

		return _nearestClanHall;
	}

	protected long _lastSocialAction;

	@Override
	public void onAction(Player player, boolean shift)
	{
		if(!isTargetable(player))
		{
			player.sendActionFailed();
			return;
		}

		if(player.getTarget() != this)
		{
			player.setNpcTarget(this);
			return;
		}

		if(shift && OnShiftActionHolder.getInstance().callShiftAction(player, NpcInstance.class, this, true))
			return;

		if(isAutoAttackable(player))
		{
			player.getAI().Attack(this, false, shift);
			return;
		}

		if(!player.checkInteractionDistance(this))
		{
			if(player.getAI().getIntention() != CtrlIntention.AI_INTENTION_INTERACT)
				player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this, null);
			return;
		}

		if(!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && player.isPK() && !player.isGM())
		{
			player.sendActionFailed();
			return;
		}

		// С NPC нельзя разговаривать мертвым, сидя и во время каста
		if((!Config.ALLOW_TALK_WHILE_SITTING && player.isSitting()) || player.isActionsDisabled())
		{
			player.sendActionFailed();
			return;
		}

		player.sendActionFailed();
		if(player.isMoving)
			player.stopMove();

		player.sendPacket(new MoveToPawnPacket(player, this, player.getInteractionDistance(this)));

		if(_isBusy)
			showBusyWindow(player);
		else if(isHasChatWindow())
		{
			boolean flag = false;
			Set<Quest> quests = getTemplate().getEventQuests(QuestEventType.NPC_FIRST_TALK);
			if(quests != null)
				for(Quest quest : quests)
				{
					QuestState qs = player.getQuestState(quest);
					if((qs == null || !qs.isCompleted()) && quest.notifyFirstTalk(this, player))
						flag = true;
				}

			if(!flag)
			{
				for(ListenerHook hook : getTemplate().getListenerHooks(ListenerHookType.NPC_FIRST_TALK))
				{
					if(hook.onNpcFirstTalk(this, player))
						flag = true;
				}
				for(ListenerHook hook : ListenerHook.getGlobalListenerHooks(ListenerHookType.NPC_FIRST_TALK))
				{
					if(hook.onNpcFirstTalk(this, player))
						flag = true;
				}
			}

			if(!flag && !isDead())
			{
				showChatWindow(player, 0, true);
				if(Config.NPC_DIALOG_PLAYER_DELAY > 0)
					player.setNpcDialogEndTime((int) (System.currentTimeMillis() / 1000L) + Config.NPC_DIALOG_PLAYER_DELAY);
			}
		}
	}

	public void showQuestWindow(Player player, int questId)
	{
		if(!player.isQuestContinuationPossible(true))
			return;

		int count = 0;
		for(QuestState qs : player.getAllQuestsStates())
			if(qs != null && qs.getQuest().isVisible(player) && qs.isStarted() && qs.getCond() > 0)
				count++;

		if(count > 40)
		{
			showChatWindow(player, "quest-limit.htm", false);
			return;
		}

		try
		{
			// Get the state of the selected quest
			QuestState qs = player.getQuestState(questId);
			if(qs != null)
			{
				if(qs.isCompleted())
				{
					if(qs.getQuest().notifyCompleted(this, qs))
						return;
				}

				if(qs.getQuest().notifyTalk(this, qs))
					return;
			}
			else
			{
				Quest quest = QuestHolder.getInstance().getQuest(questId);
				if(quest != null)
				{
					// check for start point
					Set<Quest> quests = getTemplate().getEventQuests(QuestEventType.QUEST_START);
					if(quests != null && quests.contains(quest))
					{
						qs = quest.newQuestState(player);
						if(qs.getQuest().notifyTalk(this, qs))
							return;
					}
				}
			}

			showChatWindow(player, "no-quest.htm", false);
		}
		catch(Exception e)
		{
			_log.warn("problem with npc text(QUEST ID[" + questId + "]" + e);
			_log.error("", e);
		}

		player.sendActionFailed();
	}

	public boolean canBypassCheck(Player player)
	{
		if(player.isDead() || !player.checkInteractionDistance(this))
		{
			player.sendActionFailed();
			return false;
		}
		return true;
	}

	public void onBypassFeedback(Player player, String command)
	{
		try
		{
			StringTokenizer st = new StringTokenizer(command, "_");
			String cmd = st.nextToken();
			if(command.equalsIgnoreCase("TerritoryStatus"))
			{
				HtmlMessage html = new HtmlMessage(this);

				Castle castle = getCastle(player);
				if(castle != null && castle.getId() > 0)
				{
					if(castle.getOwnerId() > 0)
					{
						Clan clan = ClanTable.getInstance().getClan(castle.getOwnerId());
						if(clan != null)
						{
							html.setFile("merchant/territorystatus.htm");
							html.replace("%castlename%", HtmlUtils.htmlResidenceName(castle.getId()));
							html.replace("%taxpercent%", String.valueOf(castle.getSellTaxPercent()));
							html.replace("%clanname%", clan.getName());
							html.replace("%clanleadername%", clan.getLeaderName());
						}
						else
						{
							html.setFile("merchant/territorystatus_noowner.htm");
							html.replace("%castlename%", HtmlUtils.htmlResidenceName(castle.getId()));
						}
					}
					else
					{
						html.setFile("merchant/territorystatus_noowner.htm");
						html.replace("%castlename%", HtmlUtils.htmlResidenceName(castle.getId()));
					}
				}
				else
				{
					html.setFile("merchant/territorystatus_noowner.htm");

					html.replace("%castlename%", "—");
				}

				player.sendPacket(html);
			}
			else if(command.startsWith("QuestEvent"))
			{
				StringTokenizer tokenizer = new StringTokenizer(command);
				tokenizer.nextToken();
				String questName = tokenizer.nextToken();
				int questId = Integer.parseInt(questName);
				if(command.length() > 12 + questName.length())
					player.processQuestEvent(questId, command.substring(12 + questName.length()), this);
				else
					player.processQuestEvent(questId, "", this);
			}
			else if(command.startsWith("Quest"))
			{
				String quest = command.substring(5).trim();
				if(quest.length() == 0)
					showQuestWindow(player);
				else
				{
					try
					{
						int questId = Integer.parseInt(quest);
						showQuestWindow(player, questId);
					}
					catch(NumberFormatException nfe)
					{
						_log.error("", nfe);
					}
				}
			}
			else if(command.startsWith("Chat") || command.startsWith("chat"))
				try
				{
					int val = Integer.parseInt(command.substring(5));
					showChatWindow(player, val, false);
				}
				catch(NumberFormatException nfe)
				{
					String filename = command.substring(5).trim();
					if(filename.length() == 0)
						showChatWindow(player, "npcdefault.htm", false);
					else
						showChatWindow(player, filename, false);
				}
			else if(command.startsWith("AttributeCancel"))
				player.sendPacket(new ExShowBaseAttributeCancelWindow(player));
			else if(command.startsWith("NpcLocationInfo"))
			{
				int val = Integer.parseInt(command.substring(16));
				NpcInstance npc = GameObjectsStorage.getByNpcId(val);
				if(npc != null)
				{
					// Убираем флажок на карте и стрелку на компасе
					player.sendPacket(new RadarControlPacket(2, 2, npc.getLoc()));
					// Ставим флажок на карте и стрелку на компасе
					player.sendPacket(new RadarControlPacket(0, 1, npc.getLoc()));
				}
			}
			else if(command.startsWith("Multisell") || command.startsWith("multisell"))
			{
				String listId = command.substring(9).trim();
				Castle castle = getCastle(player);
				MultiSellHolder.getInstance().SeparateAndSend(Integer.parseInt(listId), player, castle != null ? castle.getSellTaxRate() : 0);
			}
			else if(command.equalsIgnoreCase("ClanSkillList"))
				showClanSkillList(player);
			else if(command.startsWith("SubUnitSkillList"))
				showSubUnitSkillList(player);
			else if(command.startsWith("Link"))
				showChatWindow(player, command.substring(5), false);
			else if(cmd.equalsIgnoreCase("teleport"))
			{
				if(!st.hasMoreTokens())
				{
					errorBypass(command, player);
					return;
				}

				String cmd2 = st.nextToken();
				if(cmd2.equalsIgnoreCase("list"))
				{
					int listId = 1;
					if(st.hasMoreTokens())
						listId = Integer.parseInt(st.nextToken());

					showTeleportList(player, listId);
				}
				else if(cmd2.equalsIgnoreCase("id"))
				{
					int listId = Integer.parseInt(st.nextToken());
					int teleportNameId = Integer.parseInt(st.nextToken());

					List<TeleportLocation> list = getTemplate().getTeleportList(listId);
					if(list == null || list.isEmpty())
					{
						errorBypass(command, player);
						return;
					}

					TeleportLocation teleportLocation = null;
					for(TeleportLocation tl : list)
					{
						if(tl.getName() == teleportNameId)
						{
							teleportLocation = tl;
							break;
						}
					}

					if(teleportLocation == null)
					{
						errorBypass(command, player);
						return;
					}

					long itemCount = calcTeleportPrice(player, teleportLocation);
					if(st.hasMoreTokens())
						itemCount = Long.parseLong(st.nextToken());

					teleportPlayer(player, teleportLocation, itemCount);
				}
				else if(cmd2.equalsIgnoreCase("mdt"))
				{
					if(!st.hasMoreTokens())
					{
						errorBypass(command, player);
						return;
					}

					String cmd3 = st.nextToken();
					if(cmd3.equalsIgnoreCase("to"))
					{
						player.setVar("@mdt_back_cords", player.getLoc().toXYZString(), -1);
						player.teleToLocation(12661, 181687, -3540);
					}
					else if(cmd3.equalsIgnoreCase("from"))
					{
						String var = player.getVar("@mdt_back_cords");
						if(var == null || var.isEmpty())
						{
							player.teleToLocation(12902, 181011, -3563);
							return;
						}
						player.teleToLocation(Location.parseLoc(var));
					}
				}
				else if(cmd2.equalsIgnoreCase("fi"))
				{
					if(!st.hasMoreTokens())
					{
						errorBypass(command, player);
						return;
					}

					String cmd3 = st.nextToken();
					if(cmd3.equalsIgnoreCase("to"))
					{
						player.setVar("@fi_back_cords", player.getLoc().toXYZString(), -1);
						switch(Rnd.get(4))
						{
							case 1:
								player.teleToLocation(-60695, -56896, -2032);
								break;
							case 2:
								player.teleToLocation(-59716, -55920, -2032);
								break;
							case 3:
								player.teleToLocation(-58752, -56896, -2032);
								break;
							default :
								player.teleToLocation(-59716, -57864, -2032);
								break;
						}
					}
					else if(cmd3.equalsIgnoreCase("from"))
					{
						String var = player.getVar("@fi_back_cords");
						if(var == null || var.isEmpty())
						{
							player.teleToLocation(12902, 181011, -3563);
							return;
						}
						player.teleToLocation(Location.parseLoc(var));
					}
				}
				else
				{
					if(st.countTokens() < 2)
					{
						errorBypass(command, player);
						return;
					}

					int x = Integer.parseInt(cmd2);
					int y = Integer.parseInt(st.nextToken());
					int z = Integer.parseInt(st.nextToken());

					int itemId = 0;
					if(st.hasMoreTokens())
						itemId = Integer.parseInt(st.nextToken());

					int itemCount = 0;
					if(st.hasMoreTokens())
						itemCount = Integer.parseInt(st.nextToken());

					int castleId = 0;
					if(st.hasMoreTokens())
						castleId = Integer.parseInt(st.nextToken());

					int reflectionId = 0;
					if(st.hasMoreTokens())
						reflectionId = Integer.parseInt(st.nextToken());

					teleportPlayer(player, x, y, z, itemId, itemCount, new int[]{castleId}, reflectionId);
				}
			}
			else if(command.startsWith("open_gate"))
			{
				int val = Integer.parseInt(command.substring(10));
				ReflectionUtils.getDoor(val).openMe();
				player.sendActionFailed();
			}
			else if(command.startsWith("ExitFromQuestInstance"))
			{
				Reflection r = player.getReflection();
				if(r.isDefault())
					return;
				r.startCollapseTimer(60000);
				player.teleToLocation(r.getReturnLoc(), ReflectionManager.MAIN);
				if(command.length() > 22)
					try
					{
						int val = Integer.parseInt(command.substring(22));
						showChatWindow(player, val, false);
					}
					catch(NumberFormatException nfe)
					{
						String filename = command.substring(22).trim();
						if(filename.length() > 0)
							showChatWindow(player, filename, false);
					}
			}
			else if(cmd.equalsIgnoreCase("WithdrawP"))
				WarehouseFunctions.showRetrieveWindow(player);
			else if(cmd.equalsIgnoreCase("DepositP"))
				WarehouseFunctions.showDepositWindow(player);
			else if(cmd.equalsIgnoreCase("WithdrawC"))
				WarehouseFunctions.showWithdrawWindowClan(player);
			else if(cmd.equalsIgnoreCase("DepositC"))
				WarehouseFunctions.showDepositWindowClan(player);
			else if(cmd.equalsIgnoreCase("ensoul"))
			{
				if(!st.hasMoreTokens())
				{
					errorBypass(command, player);
					return;
				}
				String cmd2 = st.nextToken();
				if(cmd2.equalsIgnoreCase("add"))
					player.sendPacket(ExShowEnsoulWindow.STATIC);
				else if(cmd2.equalsIgnoreCase("remove"))
					player.sendPacket(ExEnSoulExtractionShow.STATIC);
			}
			else
			{
				String word = command.split("\\s+")[0];
				String args = command.substring(word.length()).trim();
				Pair<Object, Method> b = BypassHolder.getInstance().getBypass(word);
				if(b != null)
					b.getValue().invoke(b.getKey(), player, this, StringUtils.isEmpty(args) ? new String[0] : args.split("\\s+"));
				else
					_log.warn("Unknown command=[" + command + "] npcId:" + getTemplate().getId());
			}
		}
		catch(NumberFormatException nfe)
		{
			_log.warn("Invalid bypass to Server command parameter! npcId=" + getTemplate().getId() + " command=[" + command + "]", nfe);
		}
		catch(Exception sioobe)
		{
			_log.warn("Incorrect htm bypass! npcId=" + getTemplate().getId() + " command=[" + command + "]", sioobe);
		}
	}

	public void errorBypass(String bypass, Player player)
	{
		player.sendMessage(new CustomMessage("l2s.gameserver.model.instance.NpcInstance.ErrorBypass").addNumber(getNpcId()).addString(bypass));
	}

	public boolean teleportPlayer(Player player, int x, int y, int z, int itemId, long itemCount, int[] castleIds, int reflectionId)
	{
		if(player == null)
			return false;

		if(player.getMountType() == MountType.WYVERN)
		{
			//player.sendMessage("Телепортация верхом на виверне невозможна."); //TODO: [Bonux] Найти нужное сообщение!
			return false;
		}

		/* Затычка, npc Mozella не ТПшит чаров уровень которых превышает заданный в конфиге
		 * Off Like >= 56 lvl, данные по ограничению lvl'a устанавливаются в altsettings.properties.
		 */
		switch(getNpcId())
		{
			case 30483:
				if(player.getLevel() >= Config.CRUMA_GATEKEEPER_LVL)
				{
					showChatWindow(player, "teleporter/" + getNpcId() + "-no.htm", false);
					return false;
				}
				break;
			case 32864:
			case 32865:
			case 32866:
			case 32867:
			case 32868:
			case 32869:
			case 32870:
				if(player.getLevel() < 80)
				{
					showChatWindow(player, "teleporter/" + getNpcId() + "-no.htm", false);
					return false;
				}
				break;
		}

		if(itemId > 0 && itemCount > 0)
		{
			if(ItemFunctions.getItemCount(player, itemId) < itemCount)
			{
				if(itemId == ItemTemplate.ITEM_ID_ADENA)
					player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				else
					player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
				return false;
			}
		}

		if(castleIds.length > 0 && player.getReflection().isMain() && !Config.ALT_TELEPORT_TO_TOWN_DURING_SIEGE)
		{
			for(int castleId : castleIds)
			{
				Castle castle = ResidenceHolder.getInstance().getResidence(Castle.class, castleId);
				if(castle != null && castle.getSiegeEvent() != null && castle.getSiegeEvent().isInProgress())	// Нельзя телепортироваться в города, где идет осада
				{
					player.sendPacket(SystemMsg.YOU_CANNOT_TELEPORT_TO_A_VILLAGE_THAT_IS_IN_A_SIEGE);
					return false;
				}
			}
		}

		if(itemId > 0 && itemCount > 0)
		{
			if(!ItemFunctions.deleteItem(player, itemId, itemCount, true))
				return false;
		}

		Location pos = Location.findPointToStay(x, y, z, 50, 100, player.getGeoIndex());
		if(reflectionId > -1)
		{
			Reflection reflection = ReflectionManager.getInstance().get(reflectionId);
			if(reflection == null)
			{
				_log.warn("Cannot teleport to reflection ID: " + reflectionId + "!");
				return false;
			}
			player.teleToLocation(pos, reflection);
		}
		else 
			player.teleToLocation(pos);
		return true;
	}

	public boolean teleportPlayer(Player player, Location loc, int itemId, long itemCount, int[] castleIds, int reflectionId)
	{
		return teleportPlayer(player, loc.getX(), loc.getY(), loc.getZ(), itemId, itemCount, castleIds, reflectionId);
	}

	public boolean teleportPlayer(Player player, int x, int y, int z, int itemId, long itemCount)
	{
		return teleportPlayer(player, x, y, z, itemId, itemCount, new int[0], -1);
	}

	public boolean teleportPlayer(Player player, Location loc, int itemId, long itemCount)
	{
		return teleportPlayer(player, loc.getX(), loc.getY(), loc.getZ(), itemId, itemCount, new int[0], -1);
	}

	private boolean teleportPlayer(Player player, TeleportLocation loc, long itemCount)
	{
		if(teleportPlayer(player, loc, loc.getItemId(), itemCount, loc.getCastleIds(), -1))
			return true;

		return false;
	}

	private long calcTeleportPrice(Player player, TeleportLocation loc)
	{
		if(loc.getItemId() != ItemTemplate.ITEM_ID_ADENA)
			return loc.getPrice();

		double pricemod = (loc.isPrimeHours() && player.getLevel() <= Config.GATEKEEPER_FREE) ? 0. : Config.GATEKEEPER_MODIFIER;

		return (long) (loc.getPrice() * pricemod);
	}

	public void showTeleportList(Player player)
	{
		showTeleportList(player, 1);
	}

	public void showTeleportList(Player player, int listId)
	{
		StringBuilder sb = new StringBuilder();

		sb.append("&$556;").append("<br><br>");

		List<TeleportLocation> list = getTemplate().getTeleportList(listId);
		if(list != null && !list.isEmpty() && player.getPlayerAccess().UseTeleport)
		{
			for(TeleportLocation tl : list)
			{
				if(tl.getQuestZoneId() > 0 && tl.getQuestZoneId() == player.getQuestZoneId())
				{
					if(tl.getItemId() == ItemTemplate.ITEM_ID_ADENA)
					{
						long price = calcTeleportPrice(player, tl);
						sb.append("<Button ALIGN=LEFT ICON=\"QUEST\" action=\"bypass -h npc_%objectId%_teleport_id_").append(listId).append("_").append(tl.getName()).append("_").append(price).append("\" msg=\"811;F;").append(tl.getName()).append("\">").append(HtmlUtils.htmlNpcString(tl.getName()));
						if(price > 0)
							sb.append(" - ").append(price).append(" ").append(HtmlUtils.htmlItemName(ItemTemplate.ITEM_ID_ADENA));

						sb.append("</button>");
					}
					else
					{
						sb.append("<Button ALIGN=LEFT ICON=\"QUEST\" action=\"bypass -h npc_%objectId%_teleport_id_").append(listId).append("_").append(tl.getName()).append("\" msg=\"811;F;").append(tl.getName()).append("\">").append(HtmlUtils.htmlNpcString(tl.getName()));
						if(tl.getItemId() > 0 && tl.getPrice() > 0)
							sb.append(" - ").append(tl.getPrice()).append(" ").append(HtmlUtils.htmlItemName(tl.getItemId()));

						sb.append("</button>");
					}
				}
			}

			for(TeleportLocation tl : list)
			{
				if(tl.getQuestZoneId() <= 0 || tl.getQuestZoneId() != player.getQuestZoneId())
				{
					if(tl.getItemId() == ItemTemplate.ITEM_ID_ADENA)
					{
						long price = calcTeleportPrice(player, tl);
						sb.append("<Button ALIGN=LEFT ICON=\"TELEPORT\" action=\"bypass -h npc_%objectId%_teleport_id_").append(listId).append("_").append(tl.getName()).append("_").append(price).append("\" msg=\"811;F;").append(tl.getName()).append("\">").append(HtmlUtils.htmlNpcString(tl.getName()));
						if(price > 0)
							sb.append(" - ").append(price).append(" ").append(HtmlUtils.htmlItemName(ItemTemplate.ITEM_ID_ADENA));

						sb.append("</button>");
					}
					else
					{
						sb.append("<Button ALIGN=LEFT ICON=\"TELEPORT\" action=\"bypass -h npc_%objectId%_teleport_id_").append(listId).append("_").append(tl.getName()).append("\" msg=\"811;F;").append(tl.getName()).append("\">").append(HtmlUtils.htmlNpcString(tl.getName()));
						if(tl.getItemId() > 0 && tl.getPrice() > 0)
							sb.append(" - ").append(tl.getPrice()).append(" ").append(HtmlUtils.htmlItemName(tl.getItemId()));

						sb.append("</button>");
					}
				}
			}
		}
		else
			sb.append("No teleports available for you.");

		HtmlMessage html = new HtmlMessage(this);
		html.setHtml(HtmlUtils.bbParse(sb.toString()));
		player.sendPacket(html);
	}

	private class QuestInfo implements Comparable<QuestInfo>
	{
		private final Quest quest;
		private final Player player;
		private final boolean isStart;

		public QuestInfo(Quest quest, Player player, boolean isStart)
		{
			this.quest = quest;
			this.player = player;
			this.isStart = isStart;
		}

		public final Quest getQuest()
		{
			return quest;
		}

		public final boolean isStart()
		{
			return isStart;
		}

		@Override
		public int compareTo(QuestInfo info)
		{
			int quest1 = quest.getDescrState(NpcInstance.this, player, isStart);
			int quest2 = info.getQuest().getDescrState(NpcInstance.this, player, isStart);
			int questId1 = this.quest.getId();
			int questId2 = info.getQuest().getId();

			if(quest1 == 1 && quest2 == 2)
				return 1;
			else if(quest1 == 2 && quest2 == 1)
				return -1;
			else if(quest1 == 3 && quest2 == 4)
				return 1;
			else if(quest1 == 4 && quest2 == 3)
				return -1;
			else if(quest1 > quest2)
				return 1;
			else if(quest1 < quest2)
				return -1;
			else
			{
				if(questId1 > questId2)
					return 1;
				else if(questId1 < questId2)
					return -1;
				else
					// Недостижимая ситуация.
					return 0;
			}
		}
	}

	public void showQuestWindow(Player player)
	{
		// collect awaiting quests and start points
		TIntObjectMap<QuestInfo> options = new TIntObjectHashMap<QuestInfo>();

		Set<Quest> quests = getTemplate().getEventQuests(QuestEventType.QUEST_START);
		if(quests != null)
		{
			for(Quest quest : quests)
			{
				if(quest.isVisible(player) && quest.checkStartNpc(this, player) && !options.containsKey(quest.getId()))
					options.put(quest.getId(), new QuestInfo(quest, player, true));
			}
		}

		List<QuestState> awaits = player.getQuestsForEvent(this, QuestEventType.QUEST_TALK);
		if(awaits != null)
		{
			for(QuestState qs : awaits)
			{
				Quest quest = qs.getQuest();
				if(quest.isVisible(player) && quest.checkTalkNpc(this, qs) && !options.containsKey(quest.getId()))
					options.put(quest.getId(), new QuestInfo(quest, player, false));
			}
		}

		// Display a QuestChooseWindow (if several quests are available) or QuestWindow
		if(options.size() > 1)
		{
			List<QuestInfo> list = new ArrayList<QuestInfo>();
			list.addAll(options.valueCollection());
			Collections.sort(list);
			showQuestChooseWindow(player, list);
		}
		else if(options.size() == 1)
			showQuestWindow(player, options.values(new QuestInfo[1])[0].getQuest().getId());
		else
			showChatWindow(player, "no-quest.htm", false);
	}

	public void showQuestChooseWindow(Player player, List<QuestInfo> quests)
	{
		StringBuilder sb = new StringBuilder();

		sb.append("<html><body>");

		for(QuestInfo info : quests)
		{
			Quest q = info.getQuest();
			if(!q.isVisible(player))
				continue;

			sb.append("<button icon=quest align=left action=\"bypass -h npc_").append(getObjectId()).append("_Quest ").append(q.getId()).append("\">").append(q.getDescr(this, player, info.isStart())).append("</button>");
		}

		sb.append("</body></html>");

		HtmlMessage html = new HtmlMessage(this);
		html.setHtml(sb.toString());
		player.sendPacket(html);
	}

	public void showChatWindow(Player player, int val, boolean firstTalk, Object... replace)
	{
		String filename = getHtmlPath(getHtmlFilename(val, player), player);

		HtmlMessage packet = new HtmlMessage(this, filename).setPlayVoice(firstTalk);
		if(replace.length % 2 == 0)
			for(int i = 0; i < replace.length; i += 2)
				packet.replace(String.valueOf(replace[i]), String.valueOf(replace[i + 1]));
		player.sendPacket(packet);
	}

	public void showChatWindow(Player player, String filename, boolean firstTalk, Object... replace)
	{
		HtmlMessage packet;
		if(filename.endsWith(".htm"))
			packet = new HtmlMessage(this, filename);
		else
		{
			packet = new HtmlMessage(this);
			packet.setHtml(filename);
		}

		packet.setPlayVoice(firstTalk);

		if(replace.length % 2 == 0)
		{
			for(int i = 0; i < replace.length; i += 2)
				packet.replace(String.valueOf(replace[i]), String.valueOf(replace[i + 1]));
		}

		player.sendPacket(packet);
	}

	public String getHtmlFilename(int val, Player player)
	{
		String filename;
		if(val == 0)
			filename = getNpcId() + ".htm";
		else
			filename = getNpcId() + "-" + val + ".htm";

		return filename;
	}

	public String getHtmlDir(String filename, Player player)
	{
		if(getTemplate().getHtmRoot() != null && HtmCache.getInstance().getIfExists(getTemplate().getHtmRoot() + filename, player) != null)
			return getTemplate().getHtmRoot();

		if(HtmCache.getInstance().getIfExists(filename, player) != null)
			return "";

		if(HtmCache.getInstance().getIfExists("default/" + filename, player) != null)
			return "default/";

		if(HtmCache.getInstance().getIfExists("blacksmith/" + filename, player) != null)
			return "blacksmith/";

		if(HtmCache.getInstance().getIfExists("merchant/" + filename, player) != null)
			return "merchant/";

		if(HtmCache.getInstance().getIfExists("teleporter/" + filename, player) != null)
			return "teleporter/";

		if(HtmCache.getInstance().getIfExists("petmanager/" + filename, player) != null)
			return "petmanager/";

		if(HtmCache.getInstance().getIfExists("mammons/" + filename, player) != null)
			return "mammons/";

		if(HtmCache.getInstance().getIfExists("warehouse/" + filename, player) != null)
			return "warehouse/";

		return null;
	}

	public final String getHtmlPath(String filename, Player player)
	{
		String dir = getHtmlDir(filename, player);
		if(dir == null)
			return "npcdefault.htm";

		String path = dir + filename;
		if(HtmCache.getInstance().getIfExists(path, player) != null)
			return path;

		return "npcdefault.htm";
	}

	private boolean _isBusy;
	private String _busyMessage = "";

	public final boolean isBusy()
	{
		return _isBusy;
	}

	public void setBusy(boolean isBusy)
	{
		_isBusy = isBusy;
	}

	public final String getBusyMessage()
	{
		return _busyMessage;
	}

	public void setBusyMessage(String message)
	{
		_busyMessage = message;
	}

	public void showBusyWindow(Player player)
	{
		HtmlMessage html = new HtmlMessage(this);
		html.setFile("npcbusy.htm");
		html.replace("%busymessage%", _busyMessage);
		player.sendPacket(html);
	}

	public static void showFishingSkillList(Player player)
	{
		showAcquireList(AcquireType.FISHING, player);
	}

	public static void showClanSkillList(Player player)
	{
		if(player.getClan() == null || !player.isClanLeader())
		{
			player.sendPacket(SystemMsg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
			player.sendActionFailed();
			return;
		}

		showAcquireList(AcquireType.CLAN, player);
	}

	public static void showAcquireList(AcquireType t, Player player)
	{
		final Collection<SkillLearn> skills = SkillAcquireHolder.getInstance().getAvailableSkills(player, t);

		final ExAcquirableSkillListByClass asl = new ExAcquirableSkillListByClass(t, skills.size());

		for(SkillLearn s : skills)
			asl.addSkill(s.getId(), s.getLevel(), s.getLevel(), s.getCost(), s.getMinLevel());

		if(skills.size() == 0)
		{
			player.sendPacket(AcquireSkillDonePacket.STATIC);
			player.sendPacket(SystemMsg.THERE_ARE_NO_OTHER_SKILLS_TO_LEARN);
		}
		else
			player.sendPacket(asl);

		player.sendActionFailed();
	}

	public static void showSubUnitSkillList(Player player)
	{
		Clan clan = player.getClan();
		if(clan == null)
			return;

		if((player.getClanPrivileges() & Clan.CP_CL_TROOPS_FAME) != Clan.CP_CL_TROOPS_FAME)
		{
			player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return;
		}

		Set<SkillLearn> learns = new TreeSet<SkillLearn>();
		for(SubUnit sub : player.getClan().getAllSubUnits())
			learns.addAll(SkillAcquireHolder.getInstance().getAvailableSkills(player, AcquireType.SUB_UNIT, sub));

		final ExAcquirableSkillListByClass asl = new ExAcquirableSkillListByClass(AcquireType.SUB_UNIT, learns.size());

		for(SkillLearn s : learns)
			asl.addSkill(s.getId(), s.getLevel(), s.getLevel(), s.getCost(), 1, Clan.SUBUNIT_KNIGHT4);

		if(learns.size() == 0)
		{
			player.sendPacket(AcquireSkillDonePacket.STATIC);
			player.sendPacket(SystemMsg.THERE_ARE_NO_OTHER_SKILLS_TO_LEARN);
		}
		else
			player.sendPacket(asl);

		player.sendActionFailed();
	}

	/**
	 * Нужно для отображения анимации спауна, используется в пакете NpcInfo:
	 * 0=false, 1=true, 2=summoned (only works if model has a summon animation)
	 **/
	public int getSpawnAnimation()
	{
		return _spawnAnimation;
	}

	public int calculateLevelDiffForDrop(int charLevel)
	{
		if(!Config.DEEPBLUE_DROP_RULES)
			return 0;

		int mobLevel = getLevel();
		// According to official data (Prima), deep blue mobs are 9 or more levels below players
		int deepblue_maxdiff = this instanceof RaidBossInstance ? Config.DEEPBLUE_DROP_RAID_MAXDIFF : Config.DEEPBLUE_DROP_MAXDIFF;

		return Math.max(charLevel - mobLevel - deepblue_maxdiff, 0);
	}

	@Override
	public String toString()
	{
		return getNpcId() + " " + getName();
	}

	public void refreshID()
	{
		GameObjectsStorage.remove(this);
		objectId = IdFactory.getInstance().getNextId();
		GameObjectsStorage.put(this);
	}

	private boolean _isUnderground = false;

	public void setUnderground(boolean b)
	{
		_isUnderground = b;
	}

	public boolean isUnderground()
	{
		return _isUnderground;
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
	public NpcListenerList getListeners()
	{
		if(listeners == null)
			synchronized (this)
			{
				if(listeners == null)
					listeners = new NpcListenerList(this);
			}

		return (NpcListenerList) listeners;
	}

	public <T extends NpcListener> boolean addListener(T listener)
	{
		return getListeners().add(listener);
	}

	public <T extends NpcListener> boolean removeListener(T listener)
	{
		return getListeners().remove(listener);
	}

	@Override
	public NpcStatsChangeRecorder getStatsRecorder()
	{
		if(_statsRecorder == null)
			synchronized (this)
			{
				if(_statsRecorder == null)
					_statsRecorder = new NpcStatsChangeRecorder(this);
			}

		return (NpcStatsChangeRecorder) _statsRecorder;
	}

	public void setNpcState(int stateId)
	{
		broadcastPacket(new ExChangeNPCState(getObjectId(), stateId));
		npcState = stateId;
	}

	public int getNpcState()
	{
		return npcState;
	}

	@Override
	public List<L2GameServerPacket> addPacketList(Player forPlayer, Creature dropper)
	{
		List<L2GameServerPacket> list = new ArrayList<L2GameServerPacket>(3);
		list.add(new NpcInfoPacket(this, forPlayer).init());

		if(isInCombat())
			list.add(new AutoAttackStartPacket(getObjectId()));

		if(isMoving || isFollow)
			list.add(movePacket());

		return list;
	}

	@Override
	public boolean isNpc()
	{
		return true;
	}

	@Override
	public int getGeoZ(Location loc)
	{
		if(isFlying() || isInWater() || isInBoat() || isBoat() || isDoor())
			return loc.z;
		if(isNpc())
		{
			if(_spawnRange instanceof Territory)
				return GeoEngine.getHeight(loc, getGeoIndex());
			return loc.z;
		}

		return super.getGeoZ(loc);
	}

	@Override
	public Clan getClan()
	{
		Castle castle = getCastle();
		if(castle == null)
			return null;
		return castle.getOwner();
	}

	public NpcString getNameNpcString()
	{
		return _nameNpcString;
	}

	public NpcString getTitleNpcString()
	{
		return _titleNpcString;
	}

	public void setNameNpcString(NpcString nameNpcString)
	{
		_nameNpcString = nameNpcString;
	}

	public void setTitleNpcString(NpcString titleNpcString)
	{
		_titleNpcString = titleNpcString;
	}

	public SpawnRange getSpawnRange()
	{
		return _spawnRange;
	}

	public void setSpawnRange(SpawnRange spawnRange)
	{
		_spawnRange = spawnRange;
	}

	public void setParameter(String str, Object val)
	{
		if(_parameters == StatsSet.EMPTY)
			_parameters = new StatsSet();

		_parameters.set(str, val);
	}

	public void setParameters(MultiValueSet<String> set)
	{
		if(set.isEmpty())
			return;

		if(_parameters == StatsSet.EMPTY)
			_parameters = new MultiValueSet<String>(set.size());

		_parameters.putAll(set);
	}

	public int getParameter(String str, int val)
	{
		return _parameters.getInteger(str, val);
	}

	public long getParameter(String str, long val)
	{
		return _parameters.getLong(str, val);
	}

	public boolean getParameter(String str, boolean val)
	{
		return _parameters.getBool(str, val);
	}

	public String getParameter(String str, String val)
	{
		return _parameters.getString(str, val);
	}

	public MultiValueSet<String> getParameters()
	{
		return _parameters;
	}

	@Override
	public boolean isPeaceNpc()
	{
		return true;
	}

	public boolean isHasChatWindow()
	{
		return _hasChatWindow;
	}

	public void setHasChatWindow(boolean hasChatWindow)
	{
		_hasChatWindow = hasChatWindow;
	}

	public boolean isServerObject()
	{
		return false;
	}

	@Override
	public double getCurrentCollisionRadius()
	{
		if(isVisualTransformed()) // TODO: Проверить, влияет ли еффект Grow на трансформации у НПС.
			return super.getCollisionRadius();
		return super.getCollisionRadius() * getCollisionRadiusModifier();
	}

	@Override
	public double getCurrentCollisionHeight()
	{
		if(isVisualTransformed()) // TODO: Проверить, влияет ли еффект Grow на трансформации у НПС.
			return super.getCollisionHeight();
		return super.getCollisionHeight() * getCollisionHeightModifier();
	}

	public final double getCollisionHeightModifier()
	{
		return _collisionHeightModifier;
	}

	public final void setCollisionHeightModifier(double value)
	{
		_collisionHeightModifier = value;
	}

	public final double getCollisionRadiusModifier()
	{
		return _collisionRadiusModifier;
	}

	public final void setCollisionRadiusModifier(double value)
	{
		_collisionRadiusModifier = value;
	}

	@Override
	public int getEnchantEffect()
	{
		return _enchantEffect;
	}

	@SuppressWarnings("unused")
	public final boolean isNoSleepMode()
	{
		return _isNoSleepMode;
	}

	@Override
	public boolean isImmortal()
	{
		return _isImmortal;
	}

	@Override
	public boolean isFearImmune()
	{
		return getLeader() != null ? getLeader().isFearImmune() : (!isMonster() || super.isFearImmune());
	}

	public boolean canPassPacket(Player player, Class<? extends L2GameClientPacket> packet, Object... arg)
	{
		return packet == RequestItemEnsoul.class || packet == RequestTryEnSoulExtraction.class;
	}

	public boolean noShiftClick()
	{
		return _noShiftClick;
	}

	@Override
	public boolean isLethalImmune()
	{
		return _noLethal || super.isLethalImmune();
	}

	public double getRewardRate(Player player)
	{
		return player.getRateItems();
	}

	public double getDropChanceMod(Player player)
	{
		return player.getDropChanceMod();
	}

	@Override
	protected L2GameServerPacket changeMovePacket()
	{
		return new NpcInfoState(this);
	}

	public void setOwner(Player owner)
	{
		_ownerRef = owner == null ? HardReferences.<Player>emptyRef() : owner.getRef();
	}

	@Override
	public Player getPlayer()
	{
		return _ownerRef.get();
	}

	public boolean addEventTrigger(int triggerId)
	{
		if(_eventTriggers.add(triggerId))
		{
			if(getReflection().isMain())
				return EventTriggersManager.getInstance().addTrigger(MapUtils.regionX(getX()), MapUtils.regionY(getY()), triggerId);
			else
				return EventTriggersManager.getInstance().addTrigger(getReflection(), triggerId);
		}
		return false;
	}

	public boolean removeEventTrigger(int triggerId)
	{
		if(_eventTriggers.remove(triggerId))
		{
			if(getReflection().isMain())
				return EventTriggersManager.getInstance().removeTrigger(MapUtils.regionX(getX()), MapUtils.regionY(getY()), triggerId);
			else
				return EventTriggersManager.getInstance().removeTrigger(getReflection(), triggerId);
		}
		return false;
	}

	public void onSeeSocialAction(Player talker, int actionId)
	{}

	public BuyListTemplate getBuyList(int listId)
	{
		return null;
	}

	public String correctBypassLink(Player player, String link)
	{
		String dir = getHtmlDir(link, player);
		if(dir != null)
		{
			String path = dir + link;
			if(HtmCache.getInstance().getIfExists(path, player) != null)
				return path;
		}
		return link;
	}

	public void onChangeClassBypass(Player player, int classId)
	{}

	public void onSkillLearnBypass(Player player)
	{}

	@Override
	public boolean isInvulnerable()
	{
		return super.isInvulnerable() || getAI().getIntention() == CtrlIntention.AI_INTENTION_RETURN_HOME;
	}

	public void onTeleportRequest(Player talker)
	{
		showTeleportList(talker);
	}

	@Override
	public boolean onTeleported()
	{
		if(!super.onTeleported())
			return false;

		getAI().notifyEvent(CtrlEvent.EVT_TELEPORTED);
		return true;
	}

	public void onTimerFired(int timerId)
	{}

	public void onSeeCreatue(Creature creature)
	{}

	public void onDisappearCreatue(Creature creature)
	{}
}