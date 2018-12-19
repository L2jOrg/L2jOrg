package org.l2j.gameserver.model;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import org.l2j.commons.collections.CollectionUtils;
import org.l2j.commons.lang.ArrayUtils;
import org.l2j.commons.lang.reference.HardReference;
import org.l2j.commons.lang.reference.HardReferences;
import org.l2j.commons.listener.Listener;
import org.l2j.commons.threading.RunnableImpl;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.ai.CharacterAI;
import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.ai.PlayableAI.AINextAction;
import org.l2j.gameserver.data.xml.holder.LevelBonusHolder;
import org.l2j.gameserver.data.xml.holder.SkillHolder;
import org.l2j.gameserver.data.xml.holder.TransformTemplateHolder;
import org.l2j.gameserver.geodata.GeoEngine;
import org.l2j.gameserver.geodata.GeoMove;
import org.l2j.gameserver.listener.hooks.ListenerHook;
import org.l2j.gameserver.listener.hooks.ListenerHookType;
import org.l2j.gameserver.model.GameObjectTasks.*;
import org.l2j.gameserver.model.Skill.SkillTargetType;
import org.l2j.gameserver.model.Skill.SkillType;
import org.l2j.gameserver.model.Zone.ZoneType;
import org.l2j.gameserver.model.actor.basestats.CreatureBaseStats;
import org.l2j.gameserver.model.actor.flags.CreatureFlags;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.model.actor.instances.creature.AbnormalList;
import org.l2j.gameserver.model.actor.listener.CharListenerList;
import org.l2j.gameserver.model.actor.recorder.CharStatsChangeRecorder;
import org.l2j.gameserver.model.base.Element;
import org.l2j.gameserver.model.base.Sex;
import org.l2j.gameserver.model.base.TeamType;
import org.l2j.gameserver.model.base.TransformType;
import org.l2j.gameserver.model.entity.Reflection;
import org.l2j.gameserver.model.entity.events.Event;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.model.quest.QuestEventType;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.reference.L2Reference;
import org.l2j.gameserver.network.l2.components.CustomMessage;
import org.l2j.gameserver.network.l2.components.IBroadcastPacket;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.*;
import org.l2j.gameserver.network.l2.s2c.FlyToLocationPacket.FlyType;
import org.l2j.gameserver.network.l2.s2c.updatetype.IUpdateTypeComponent;
import org.l2j.gameserver.skills.*;
import org.l2j.gameserver.skills.effects.Effect;
import org.l2j.gameserver.stats.*;
import org.l2j.gameserver.stats.Formulas.AttackInfo;
import org.l2j.gameserver.stats.funcs.Func;
import org.l2j.gameserver.stats.triggers.RunnableTrigger;
import org.l2j.gameserver.stats.triggers.TriggerInfo;
import org.l2j.gameserver.stats.triggers.TriggerType;
import org.l2j.gameserver.taskmanager.LazyPrecisionTaskManager;
import org.l2j.gameserver.taskmanager.RegenTaskManager;
import org.l2j.gameserver.templates.CreatureTemplate;
import org.l2j.gameserver.templates.item.WeaponTemplate;
import org.l2j.gameserver.templates.item.WeaponTemplate.WeaponType;
import org.l2j.gameserver.templates.npc.NpcTemplate;
import org.l2j.gameserver.templates.player.transform.TransformTemplate;
import org.l2j.gameserver.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.util.Objects.requireNonNullElse;
import static org.l2j.commons.util.Util.STRING_EMPTY;
import static org.l2j.gameserver.ai.CtrlIntention.AI_INTENTION_ACTIVE;

public abstract class Creature extends GameObject
{
	public class AbortCastDelayed extends RunnableImpl
	{
		private Creature _cha;
		
		public AbortCastDelayed (Creature cha)
		{
			_cha = cha;
		}

		@Override
		public void runImpl() throws Exception
		{
			if(_cha == null)
				return;
			_cha.abortCast(true, true);	
		}	
	}

	public class MoveNextTask extends RunnableImpl
	{
		private double alldist, donedist;

		public MoveNextTask setDist(double dist)
		{
			alldist = dist;
			donedist = 0.;
			return this;
		}

		@Override
		public void runImpl() throws Exception
		{
			if(!isMoving)
				return;

			moveLock.lock();
			try
			{
				if(!isMoving)
					return;

				if(isMovementDisabled())
				{
					stopMove();
					return;
				}

				Creature follow = null;
				int speed = getMoveSpeed();
				if(speed <= 0)
				{
					stopMove();
					return;
				}
				long now = System.currentTimeMillis();

				if(isFollow)
				{
					follow = getFollowTarget();
					if(follow == null || follow.isInvisible(Creature.this))
					{
						stopMove();
						return;
					}
					if(isInRangeZ(follow, _offset) && GeoEngine.canSeeTarget(Creature.this, follow, false))
					{
						stopMove();
						ThreadPoolManager.getInstance().execute(new NotifyAITask(Creature.this, CtrlEvent.EVT_ARRIVED_TARGET));
						return;
					}
				}

				if(alldist <= 0)
				{
					moveNext(false);
					return;
				}

				donedist += (now - _startMoveTime) * _previousSpeed / 1000.;
				double done = donedist / alldist;

				if(done < 0)
					done = 0;
				if(done >= 1)
				{
					moveNext(false);
					return;
				}

				if(isMovementDisabled())
				{
					stopMove();
					return;
				}

				Location loc = null;

				int index = (int) (moveList.size() * done);
				if(index >= moveList.size())
					index = moveList.size() - 1;
				if(index < 0)
					index = 0;

				loc = moveList.get(index).clone().geo2world();

				if(!isFlying() && !isInBoat() && !isInWater() && !isBoat())
					if(loc.z - getZ() > 256)
					{
						String bug_text = "geo bug 1 at: " + getLoc() + " => " + loc.x + "," + loc.y + "," + loc.z + "\tAll path: " + moveList.get(0) + " => " + moveList.get(moveList.size() - 1);
						Log.add(bug_text, "geo");
						stopMove();
						return;
					}

				// Проверяем, на всякий случай
				if(loc == null || isMovementDisabled())
				{
					stopMove();
					return;
				}

				setLoc(loc, true);

				// В процессе изменения координат, мы остановились
				if(isMovementDisabled())
				{
					stopMove();
					return;
				}

                if(isFollow)
                {
                    Location followLoc = follow.getLoc();
                    if(movingDestTempPos.distance3D(followLoc) != 0)
                    {
                        _followCounter++;
                        if(Math.abs(getZ() - loc.z) > 1000 && !isFlying())
                        {
                            sendPacket(SystemMsg.CANNOT_SEE_TARGET);
                            stopMove();
                            return;
                        }

                        if(_followCounter == 5)
                        {
                            if(buildPathTo(followLoc.x, followLoc.y, followLoc.z, 0, follow, _forestalling, !follow.isDoor()) != null)
                                movingDestTempPos.set(followLoc.x, followLoc.y, followLoc.z);
                            else
                            {
                                stopMove();
                                return;
                            }
	                        moveNext(true);
                            _followCounter = 0;
                            return;
                        }
                    }
                }

				_previousSpeed = speed;
				_startMoveTime = now;
				_moveTask = ThreadPoolManager.getInstance().schedule(this, getMoveTickInterval());
			}
			catch(Exception e)
			{
				_log.error("", e);
			}
			finally
			{
				moveLock.unlock();
			}
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(Creature.class);

	public static final double HEADINGS_IN_PI = 10430.378350470452724949566316381;
	//растояние от перса до нпц для диалога
	public static final int INTERACTION_DISTANCE = 200;

	private Skill _castingSkill;
    private boolean _isCriticalBlowCastingSkill;

	private long _castInterruptTime;
	private long _animationEndTime;

	private int _castInterval;

	public Future<?> _skillTask;
	private Future<?> _skillLaunchedTask;

	private Future<?> _stanceTask;
	private Runnable _stanceTaskRunnable;
	private long _stanceEndTime;

    private Future<?> _deleteTask;

	public final static int CLIENT_BAR_SIZE = 352; // 352 - размер полоски CP/HP/MP в клиенте, в пикселях

	private int _lastCpBarUpdate = -1;
	private int _lastHpBarUpdate = -1;
	private int _lastMpBarUpdate = -1;

	protected double _currentCp = 0;
	private double _currentHp = 1;
	protected double _currentMp = 1;

	protected boolean _isAttackAborted;
	protected long _attackEndTime;
	protected long _attackReuseEndTime;
    private long _lastAttackTime;
	private int _poleAttackCount = 0;
	private static final double[] POLE_VAMPIRIC_MOD = { 1, 0.9, 0, 7, 0.2, 0.01 };

	/** HashMap(Integer, L2Skill) containing all skills of the L2Character */
	protected final TIntObjectMap<SkillEntry> _skills = new TIntObjectHashMap<>();
	protected Map<TriggerType, Set<TriggerInfo>> _triggers;

	protected TIntObjectMap<TimeStamp> _skillReuses = new TIntObjectHashMap<>();

	protected volatile AbnormalList _effectList;

	protected volatile CharStatsChangeRecorder<? extends Creature> _statsRecorder;

	/** Map 32 bits (0x00000000) containing all abnormal effect in progress */
	private Set<AbnormalEffect> _abnormalEffects = new CopyOnWriteArraySet<AbnormalEffect>();

	private AtomicBoolean isDead = new AtomicBoolean();
	protected AtomicBoolean isTeleporting = new AtomicBoolean();

	private boolean _fakeDeath;
	private boolean _isPreserveAbnormal; // Восстанавливает все бафы после смерти
	private boolean _isSalvation; // Восстанавливает все бафы после смерти и полностью CP, MP, HP

	private boolean _meditated;
	private boolean _lockedTarget;

	private boolean _blocked;

    private final Map<Effect, TIntSet> _ignoreSkillsEffects = new HashMap<Effect, TIntSet>();

	private volatile HardReference<? extends Creature> _effectImmunityException = HardReferences.emptyRef();
	private volatile HardReference<? extends Creature> _damageBlockedException = HardReferences.emptyRef();

	private boolean _flying;

	private boolean _running;

	public boolean isMoving;
	public boolean isFollow;
    public boolean isKeyboardMoving;
    public boolean isPathfindMoving;

	private final Lock moveLock = new ReentrantLock();
	private Future<?> _moveTask;
	private MoveNextTask _moveTaskRunnable;
	private List<Location> moveList;
	private Location destination;
	/**
	 * при moveToLocation используется для хранения геокоординат в которые мы двигаемся для того что бы избежать повторного построения одного и того же пути
	 * при followToCharacter используется для хранения мировых координат в которых находилась последний раз преследуемая цель для отслеживания необходимости перестраивания пути
	 */
	private final Location movingDestTempPos = new Location();
	private int _offset;
    private int _followCounter;

	private boolean _forestalling;

	private volatile HardReference<? extends GameObject> target = HardReferences.emptyRef();
	private volatile HardReference<? extends Creature> _castingTarget = HardReferences.emptyRef();
	private volatile HardReference<? extends Creature> followTarget = HardReferences.emptyRef();
	private volatile HardReference<? extends Creature> _aggressionTarget = HardReferences.emptyRef();

	private final List<List<Location>> _targetRecorder = new ArrayList<List<Location>>();
	private long _followTimestamp, _startMoveTime;
	private int _previousSpeed = 0;
	private int _rndCharges = 0;

	private int _heading;

	private final Calculator[] _calculators;

	private CreatureTemplate _template;

	protected volatile CharacterAI _ai;

	protected String _name;
	protected String _title;
	protected TeamType _team = TeamType.NONE;

	private boolean _isRegenerating;
	private final Lock regenLock = new ReentrantLock();
	private Future<?> _regenTask;
	private Runnable _regenTaskRunnable;

	private List<Zone> _zones = new ArrayList<>();
	/** Блокировка для чтения/записи объектов из региона */
	private final ReadWriteLock zonesLock = new ReentrantReadWriteLock();
	private final Lock zonesRead = zonesLock.readLock();
	private final Lock zonesWrite = zonesLock.writeLock();

	protected volatile CharListenerList listeners;

	private final Lock statusListenersLock = new ReentrantLock();

	protected HardReference<? extends Creature> reference;

	private boolean _isInTransformUpdate = false;
	private TransformTemplate _visualTransform = null;

    private boolean _isTargetable = true;

	protected CreatureBaseStats _baseStats = null;

    protected CreatureFlags _statuses = null;

	private Location _flyLoc = null;

	private volatile Map<BasicProperty, BasicPropertyResist> _basicPropertyResists;

	public Creature(int objectId, CreatureTemplate template)
	{
		super(objectId);

		_template = template;

		_calculators = new Calculator[Stats.NUM_STATS];

		StatFunctions.addPredefinedFuncs(this);

		reference = new L2Reference<Creature>(this);

        if(!isObservePoint())
            GameObjectsStorage.put(this);
	}

	@Override
	public HardReference<? extends Creature> getRef()
	{
		return reference;
	}

	public boolean isAttackAborted()
	{
		return _isAttackAborted;
	}

	public final void abortAttack(boolean force, boolean message)
	{
		if(isAttackingNow())
		{
			_attackEndTime = 0;
			if(force)
				_isAttackAborted = true;

			getAI().setIntention(AI_INTENTION_ACTIVE);

			if(isPlayer() && message)
			{
				sendActionFailed();
				sendPacket(new SystemMessage(SystemMessage.C1S_ATTACK_FAILED).addName(this));
			}
		}
	}

    public final void abortCast(boolean force, boolean message)
	{
		boolean cancelled = false;

		if(isCastingNow() && (force || canAbortCast()))
		{
			final Skill castingSkill = getCastingSkill();
			if(castingSkill != null && castingSkill.isAbortable())
			{
				final Future<?> skillTask = _skillTask;
				final Future<?> skillLaunchedTask = _skillLaunchedTask;

				clearCastVars();

				if(skillTask != null)
					skillTask.cancel(false); // cancels the skill hit scheduled task

				if(skillLaunchedTask != null)
					skillLaunchedTask.cancel(false); // cancels the skill hit scheduled task

				cancelled = true;
			}
		}

		if(cancelled)
		{
			broadcastPacket(new MagicSkillCanceled(getObjectId())); // broadcast packet to stop animations client-side

			getAI().setIntention(AI_INTENTION_ACTIVE);

			if(isPlayer() && message)
				sendPacket(SystemMsg.YOUR_CASTING_HAS_BEEN_INTERRUPTED);
		}
	}

	private final boolean canAbortCast()
	{
		return _castInterruptTime > System.currentTimeMillis();
	}

	// Reworked by Rivelia.
	private double reflectDamage(Creature attacker, Skill skill, double damage)
	{
		if(isDead() || damage <= 0 || !attacker.checkRange(attacker, this) || getCurrentHp() + getCurrentCp() <= damage)
			return 0.;

		final boolean bow = attacker.getBaseStats().getAttackType() == WeaponType.BOW || attacker.getBaseStats().getAttackType() == WeaponType.CROSSBOW || attacker.getBaseStats().getAttackType() == WeaponType.TWOHANDCROSSBOW;
		final double resistReflect = 1 - (attacker.calcStat(Stats.RESIST_REFLECT_DAM, 0, null, null) * 0.01); 

		double value = 0.;
		double chanceValue = 0.;
		if(skill != null)
		{
			if(skill.isMagic())
			{
				chanceValue = calcStat(Stats.REFLECT_AND_BLOCK_MSKILL_DAMAGE_CHANCE, 0, attacker, skill);
				value = calcStat(Stats.REFLECT_MSKILL_DAMAGE_PERCENT, 0, attacker, skill);
			}
			else if(skill.isPhysic())
			{
				chanceValue = calcStat(Stats.REFLECT_AND_BLOCK_PSKILL_DAMAGE_CHANCE, 0, attacker, skill);
				value = calcStat(Stats.REFLECT_PSKILL_DAMAGE_PERCENT, 0, attacker, skill);
			}
		}
		else
		{
			chanceValue = calcStat(Stats.REFLECT_AND_BLOCK_DAMAGE_CHANCE, 0, attacker, null);
			if (bow)
				value = calcStat(Stats.REFLECT_BOW_DAMAGE_PERCENT, 0, attacker, null);
			else
				value = calcStat(Stats.REFLECT_DAMAGE_PERCENT, 0, attacker, null);
		}

		// If we are not lucky, set back value to 0, otherwise set it equal to damage.
		if(chanceValue > 0 && Rnd.chance(chanceValue))
			chanceValue = damage;
		else
			chanceValue = 0.;			

		if(value > 0 || chanceValue > 0)
		{
			value = ((value / 100. * damage) + chanceValue) * resistReflect;
			if (Config.REFLECT_DAMAGE_CAPPED_BY_PDEF)	// @Rivelia. If config is on: reflected damage cannot exceed enemy's P. Def.
			{
				int xPDef = attacker.getPDef(this);
				if (xPDef > 0)
					value = Math.min(value, xPDef);
			}
			return value;
		}
		return 0.;
	}

	private void absorbDamage(Creature target, Skill skill, double damage)
	{
		if(target.isDead())
			return;

		if(damage <= 0)
			return;

		final boolean bow = getBaseStats().getAttackType() == WeaponType.BOW || getBaseStats().getAttackType() == WeaponType.CROSSBOW || getBaseStats().getAttackType() == WeaponType.TWOHANDCROSSBOW;

		// вампирик
		//damage = (int) (damage - target.getCurrentCp() - target.getCurrentHp()); WTF?

		double absorb = 0;
		if(skill != null)
		{
			if(skill.isMagic())
				absorb = calcStat(Stats.ABSORB_MSKILL_DAMAGE_PERCENT, 0, this, skill);
			else
				absorb = calcStat(Stats.ABSORB_PSKILL_DAMAGE_PERCENT, 0, this, skill);
		}
		else if(skill == null && !bow)
			absorb = calcStat(Stats.ABSORB_DAMAGE_PERCENT, 0, this, null);
		else if(skill == null && bow)
			absorb = calcStat(Stats.ABSORB_BOW_DAMAGE_PERCENT, 0, this, null);

		final double poleMod = POLE_VAMPIRIC_MOD[Math.max(0, Math.min(_poleAttackCount, POLE_VAMPIRIC_MOD.length - 1))];

		absorb = poleMod * absorb;

		final boolean damageBlocked = target.isDamageBlocked(this);
		double limit;
		if(absorb > 0 && !damageBlocked && Rnd.chance(Config.ALT_VAMPIRIC_CHANCE) && !target.isServitor() && !target.isInvulnerable())
		{
			limit = calcStat(Stats.HP_LIMIT, null, null) * getMaxHp() / 100.;
			if(getCurrentHp() < limit)
				setCurrentHp(Math.min(_currentHp + (damage * absorb / 100.), limit), false);
		}

		absorb = poleMod * calcStat(Stats.ABSORB_DAMAGEMP_PERCENT, 0, target, null);
		if(absorb > 0 && !damageBlocked && !target.isServitor() && !target.isInvulnerable())
		{
			limit = calcStat(Stats.MP_LIMIT, null, null) * getMaxMp() / 100.;
			if(getCurrentMp() < limit)
				setCurrentMp(Math.min(_currentMp + damage * absorb / 100., limit));
		}
	}

	public double absorbToEffector(Creature attacker, double damage)
	{
		if(damage == 0)
			return 0;

		double transferToEffectorDam = calcStat(Stats.TRANSFER_TO_EFFECTOR_DAMAGE_PERCENT, 0.);
		if(transferToEffectorDam > 0)
		{
            Collection<Abnormal> abnormals = getAbnormalList().values();
			if(abnormals.isEmpty())
				return damage;

            for(Abnormal abnormal : abnormals)
            {
                for(Effect effect : abnormal.getEffects())
                {
                    if(effect.getEffectType() != EffectType.AbsorbDamageToEffector)
                        continue;

                    Creature effector = effect.getEffector();
                    // на мертвого чара, не онлайн игрока - не даем абсорб, и не на самого себя
                    if(effector == this || effector.isDead() || !isInRange(effector, 1200))
                        return damage;

                    Player thisPlayer = getPlayer();
                    Player effectorPlayer = effector.getPlayer();
                    if(thisPlayer != null && effectorPlayer != null)
                    {
                        if(thisPlayer != effectorPlayer && (!thisPlayer.isOnline() || !thisPlayer.isInParty() || thisPlayer.getParty() != effectorPlayer.getParty()))
                            return damage;
                    }
                    else
                        return damage;

                    double transferDamage = (damage * transferToEffectorDam) * .01;
                    damage -= transferDamage;

                    effector.reduceCurrentHp(transferDamage, effector, null, false, false, !attacker.isPlayable(), false, true, false, true);
                }
            }
		}
		return damage;
	}

    private double reduceDamageByMp(Creature attacker, double damage)
	{
		if(damage == 0)
			return 0;

		double power = calcStat(Stats.TRANSFER_TO_MP_DAMAGE_PERCENT, 0.);
		if(power <= 0)
			return damage;

        double mpDam = damage - damage * power / 100.;

        if(mpDam > 0)
        {
            if(mpDam >= getCurrentMp())
            {
                damage = mpDam - getCurrentMp();
                sendPacket(SystemMsg.MP_BECAME_0_AND_THE_ARCANE_SHIELD_IS_DISAPPEARING);
                setCurrentMp(0);
                getAbnormalList().stop(AbnormalType.mp_shield);
            }
            else
            {
                reduceCurrentMp(mpDam, null);
                sendPacket(new SystemMessagePacket(SystemMsg.ARCANE_SHIELD_DECREASED_YOUR_MP_BY_S1_INSTEAD_OF_HP).addInteger((int) mpDam));
                return 0;
            }
        }

		return damage;
	}

	public Servitor getServitorForTransfereDamage(double damage)
	{
		return null;
	}

	public double getDamageForTransferToServitor(double damage)
	{
		return 0.;
	}

    public SkillEntry addSkill(SkillEntry newSkillEntry)
	{
		if(newSkillEntry == null)
			return null;

        SkillEntry oldSkillEntry = _skills.get(newSkillEntry.getId());
		if(newSkillEntry.equals(oldSkillEntry))
			return oldSkillEntry;

		// Replace oldSkill by newSkill or Add the newSkill
        _skills.put(newSkillEntry.getId(), newSkillEntry);

        Skill newSkill = newSkillEntry.getTemplate();

        if(oldSkillEntry != null)
		{
            Skill oldSkill = oldSkillEntry.getTemplate();
			if(oldSkill.isToggle())
			{
				if(oldSkill.getLevel() > newSkill.getLevel())
                    getAbnormalList().stop(oldSkill);
			}

			removeStatsOwner(oldSkill);
			removeTriggers(oldSkill);
		}

		addTriggers(newSkill);

		// Add Func objects of newSkill to the calculator set of the L2Character
		addStatFuncs(newSkill.getStatFuncs());

        onAddSkill(newSkillEntry);

		return oldSkillEntry;
	}

    protected void onAddSkill(SkillEntry skill)
    {}

    protected void onRemoveSkill(SkillEntry skill)
    {}

	public Calculator[] getCalculators()
	{
		return _calculators;
	}

	public final void addStatFunc(Func f)
	{
		if(f == null)
			return;
		int stat = f.stat.ordinal();
		synchronized (_calculators)
		{
			if(_calculators[stat] == null)
				_calculators[stat] = new Calculator(f.stat, this);
			_calculators[stat].addFunc(f);
		}
	}

	public final void addStatFuncs(Func[] funcs)
	{
		for(Func f : funcs)
			addStatFunc(f);
	}

	public final void removeStatFunc(Func f)
	{
		if(f == null)
			return;
		int stat = f.stat.ordinal();
		synchronized (_calculators)
		{
			if(_calculators[stat] != null)
				_calculators[stat].removeFunc(f);
		}
	}

	public final void removeStatFuncs(Func[] funcs)
	{
		for(Func f : funcs)
			removeStatFunc(f);
	}

	public final void removeStatsOwner(Object owner)
	{
		synchronized (_calculators)
		{
			for(int i = 0; i < _calculators.length; i++)
				if(_calculators[i] != null)
					_calculators[i].removeOwner(owner);
		}
	}

	public void altOnMagicUse(Creature aimingTarget, Skill skill)
	{
		if(isAlikeDead() || skill == null)
			return;
		int magicId = skill.getDisplayId();
        int level = skill.getDisplayLevel();
		List<Creature> targets = skill.getTargets(this, aimingTarget, true);
        if(!skill.isNotBroadcastable())
		    broadcastPacket(new MagicSkillLaunchedPacket(getObjectId(), magicId, level, targets));
		double mpConsume2 = skill.getMpConsume2();
		if(mpConsume2 > 0)
		{
			double mpConsume2WithStats;
			if(skill.isMagic())
				mpConsume2WithStats = calcStat(Stats.MP_MAGIC_SKILL_CONSUME, mpConsume2, aimingTarget, skill);
			else
				mpConsume2WithStats = calcStat(Stats.MP_PHYSICAL_SKILL_CONSUME, mpConsume2, aimingTarget, skill);

			if(_currentMp < mpConsume2WithStats)
			{
				sendPacket(SystemMsg.NOT_ENOUGH_MP);
				return;
			}
			reduceCurrentMp(mpConsume2WithStats, null);
		}
		callSkill(skill, targets, false, false);
	}

	public final void forceUseSkill(Skill skill, Creature target)
	{
		if(skill == null)
			return;

		if(target == null)
		{
			target = skill.getAimingTarget(this, getTarget());
			if(target == null)
				return;
		}

		final List<Creature> targets = skill.getTargets(this, target, true);

        if(!skill.isNotBroadcastable())
        {
            broadcastPacket(new MagicSkillUse(this, target, skill.getDisplayId(), skill.getDisplayLevel(), 0, 0));
            broadcastPacket(new MagicSkillLaunchedPacket(getObjectId(), skill.getDisplayId(), skill.getDisplayLevel(), targets));
        }

		callSkill(skill, targets, false, false);
	}

	public void altUseSkill(Skill skill, Creature target)
	{
		if(skill == null)
			return;

		if(isUnActiveSkill(skill.getId()))
			return;

		if(isSkillDisabled(skill))
			return;

		if(target == null)
		{
			target = skill.getAimingTarget(this, getTarget());
			if(target == null)
				return;
		}

		getListeners().onMagicUse(skill, target, true);

		if(!skill.isHandler() && isPlayable())
		{
			if(skill.getItemConsumeId() > 0 && skill.getItemConsume() > 0)
			{
                if(skill.isItemConsumeFromMaster())
                {
                    Player master = getPlayer();
                    if(master == null || !master.consumeItem(skill.getItemConsumeId(), skill.getItemConsume(), false))
                        return;
                }
                else if(!consumeItem(skill.getItemConsumeId(), skill.getItemConsume(), false))
                    return;
            }
		}

		if(skill.getReferenceItemId() > 0)
		{
			if(!consumeItemMp(skill.getReferenceItemId(), skill.getReferenceItemMpConsume()))
				return;
		}

		if(skill.getSoulsConsume() > getConsumedSouls())
			return;

		if(skill.getSoulsConsume() > 0)
			setConsumedSouls(getConsumedSouls() - skill.getSoulsConsume(), null);

        long reuseDelay = Formulas.calcSkillReuseDelay(this, skill);

        if(!skill.isToggle() && !skill.isNotBroadcastable())
        {
            MagicSkillUse msu = new MagicSkillUse(this, target, skill.getDisplayId(), skill.getDisplayLevel(), skill.getHitTime(), reuseDelay);
            msu.setReuseSkillId(skill.getReuseSkillId());
            broadcastPacket(msu);
        }

		disableSkill(skill, reuseDelay);

		altOnMagicUse(target, skill);
	}

	public void sendReuseMessage(Skill skill)
	{}

	public void broadcastPacket(L2GameServerPacket... packets)
	{
		sendPacket(packets);
		broadcastPacketToOthers(packets);
	}

	public void broadcastPacket(List<L2GameServerPacket> packets)
	{
		sendPacket(packets);
		broadcastPacketToOthers(packets);
	}

	public void broadcastPacketToOthers(L2GameServerPacket... packets)
	{
		if(!isVisible() || packets.length == 0)
			return;

		for(Player target : World.getAroundObservers(this))
			target.sendPacket(packets);
	}

	public void broadcastPacketToOthers(List<L2GameServerPacket> packets)
	{
		broadcastPacketToOthers(packets.toArray(new L2GameServerPacket[packets.size()]));
	}

	public StatusUpdatePacket makeStatusUpdate(Creature caster, int... fields)
	{
		StatusUpdatePacket su = new StatusUpdatePacket(this, caster);
		for(int field : fields)
			switch(field)
			{
				case StatusUpdatePacket.CUR_HP:
					su.addAttribute(field, (int) getCurrentHp());
					break;
				case StatusUpdatePacket.MAX_HP:
					su.addAttribute(field, getMaxHp());
					break;
				case StatusUpdatePacket.CUR_MP:
					su.addAttribute(field, (int) getCurrentMp());
					break;
				case StatusUpdatePacket.MAX_MP:
					su.addAttribute(field, getMaxMp());
					break;
				case StatusUpdatePacket.KARMA:
					su.addAttribute(field, getKarma());
					break;
				case StatusUpdatePacket.CUR_CP:
					su.addAttribute(field, (int) getCurrentCp());
					break;
				case StatusUpdatePacket.MAX_CP:
					su.addAttribute(field, getMaxCp());
					break;
				case StatusUpdatePacket.PVP_FLAG:
					su.addAttribute(field, getPvpFlag());
					break;
			}
		return su;
	}

	public void broadcastStatusUpdate()
	{
		if(!needStatusUpdate())
			return;

		StatusUpdatePacket su = makeStatusUpdate(null, StatusUpdatePacket.MAX_HP, StatusUpdatePacket.MAX_MP, StatusUpdatePacket.CUR_HP, StatusUpdatePacket.CUR_MP);
		broadcastPacket(su);
	}

	public int calcHeading(int x_dest, int y_dest)
	{
		return (int) (Math.atan2(getY() - y_dest, getX() - x_dest) * HEADINGS_IN_PI) + 32768;
	}

	public final double calcStat(Stats stat)
	{
		return calcStat(stat, null, null);
	}

	public final double calcStat(Stats stat, double init)
	{
		return calcStat(stat, init, null, null);
	}

	public final double calcStat(Stats stat, double init, Creature target, Skill skill)
	{
		int id = stat.ordinal();
		Calculator c = _calculators[id];
		if(c == null)
			return init;
		Env env = new Env();
		env.character = this;
		env.target = target;
		env.skill = skill;
		env.value = init;
		c.calc(env);
		return env.value;
	}

	public final double calcStat(Stats stat, Creature target, Skill skill)
	{
		Env env = new Env(this, target, skill);
		env.value = stat.getInit();
		int id = stat.ordinal();
		Calculator c = _calculators[id];
		if(c != null)
			c.calc(env);
		return env.value;
	}

	/**
	 * Return the Attack Speed of the L2Character (delay (in milliseconds) before next attack).
	 */
	public int calculateAttackDelay()
	{
		return Formulas.calcPAtkSpd(getPAtkSpd());
	}

	public void callSkill(Skill skill, List<Creature> targets, boolean useActionSkills, boolean trigger)
	{
		try
		{
            final Creature castingTarget = getCastingTarget();
			if(useActionSkills && _triggers != null)
			{
				if(skill.isOffensive())
				{
					if(skill.isMagic())
						useTriggers(castingTarget, TriggerType.OFFENSIVE_MAGICAL_SKILL_USE, null, skill, 0);
					else if(skill.isPhysic())
						useTriggers(castingTarget, TriggerType.OFFENSIVE_PHYSICAL_SKILL_USE, null, skill, 0);
				}
				else if(skill.isMagic())
					useTriggers(castingTarget, TriggerType.SUPPORT_MAGICAL_SKILL_USE, null, skill, 0);

                useTriggers(this, TriggerType.ON_CAST_SKILL, null, skill, 0);
            }

			final Player player = getPlayer();
			for(Creature target : targets)
			{
				if(target == null)
					continue;

				target.getListeners().onMagicHit(skill, this);

				if(player != null && target.isNpc())
				{
					NpcInstance npc = (NpcInstance) target;
					List<QuestState> ql = player.getQuestsForEvent(npc, QuestEventType.MOB_TARGETED_BY_SKILL);
					if(ql != null)
					{
						for(QuestState qs : ql)
							qs.getQuest().notifySkillUse(npc, skill, qs);
					}
				}
			}

			useTriggers(castingTarget, TriggerType.ON_END_CAST, null, skill, 0);

			skill.onEndCast(this, targets);
		}
		catch(Exception e)
		{
			_log.error("", e);
		}
	}

	public void useTriggers(GameObject target, TriggerType type, Skill ex, Skill owner, double damage)
	{
		useTriggers(target, null, type, ex, owner, owner, damage);
	}

	public void useTriggers(GameObject target, List<Creature> targets, TriggerType type, Skill ex, Skill owner, double damage)
	{
		useTriggers(target, targets, type, ex, owner, owner, damage);
	}

	public void useTriggers(GameObject target, TriggerType type, Skill ex, Skill owner, StatTemplate triggersOwner, double damage)
	{
		useTriggers(target, null, type, ex, owner, triggersOwner, damage);
	}

	public void useTriggers(GameObject target, List<Creature> targets, TriggerType type, Skill ex, Skill owner, StatTemplate triggersOwner, double damage)
	{
		Set<TriggerInfo> triggers = null;
		switch(type)
		{
			case ON_START_CAST:
            case ON_TICK_CAST:
			case ON_END_CAST:
			case ON_FINISH_CAST:
			case ON_START_EFFECT:
			case ON_EXIT_EFFECT:
			case ON_FINISH_EFFECT:
			case ON_REVIVE:
				if(triggersOwner != null)
				{
					triggers = new CopyOnWriteArraySet<TriggerInfo>();
					for(TriggerInfo t : triggersOwner.getTriggerList())
					{
						if(t.getType() == type)
							triggers.add(t);
					}
				}
				break;
            case ON_CAST_SKILL:
                if(_triggers.get(type) != null)
                {
                    triggers = new CopyOnWriteArraySet<TriggerInfo>();
                    for(TriggerInfo t : _triggers.get(type))
                    {
                        int skillID = t.getArgs() == null || t.getArgs().isEmpty() ? -1 : Integer.parseInt(t.getArgs());
                        if(skillID == -1 || skillID == owner.getId())
                            triggers.add(t);
                    }
                }
                break;
            default:
				if(_triggers != null)
					triggers = _triggers.get(type);
				break;
		}

		if(triggers != null && !triggers.isEmpty())
		{
			for(TriggerInfo t : triggers)
			{
                SkillEntry skillEntry = t.getSkill();
				if(skillEntry != null)
				{
					if(!skillEntry.getTemplate().equals(ex))
						useTriggerSkill(target == null ? getTarget() : target, targets, t, owner, damage);
				}
			}
		}
	}

	public void useTriggerSkill(GameObject target, List<Creature> targets, TriggerInfo trigger, Skill owner, double damage)
	{
        SkillEntry skillEntry = trigger.getSkill();
        if(skillEntry == null)
            return;

        Skill skill = skillEntry.getTemplate();
		if(skill == null)
			return;

		/*if(skill.getTargetType() == SkillTargetType.TARGET_SELF && !skill.isTrigger())
			logger.warn("Self trigger skill dont have trigger flag. SKILL ID[" + skill.getId() + "]");*/

		Creature aimTarget = skill.getAimingTarget(this, target);
		if(aimTarget != null && trigger.isIncreasing())
		{
			int increasedTriggerLvl = 0;
			for(Abnormal effect : aimTarget.getAbnormalList())
			{
				if(effect.getSkill().getId() != skill.getId())
					continue;

				increasedTriggerLvl = effect.getSkill().getLevel(); //taking the first one only.
				break;
			}

            if(increasedTriggerLvl == 0)
            {
                loop: for(Servitor servitor : aimTarget.getServitors())
                {
                    for(Abnormal effect : servitor.getAbnormalList())
                    {
                        if(effect.getSkill().getId() != skill.getId())
                            continue;

                        increasedTriggerLvl = effect.getSkill().getLevel(); //taking the first one only.
                        break loop;
                    }
                }
            }

			if(increasedTriggerLvl > 0)
			{
				Skill newSkill = SkillHolder.getInstance().getSkill(skill.getId(), increasedTriggerLvl + 1);
				if(newSkill != null)
					skill = newSkill;
				else
					skill = SkillHolder.getInstance().getSkill(skill.getId(), increasedTriggerLvl);
			}
		}

		if(skill.getReuseDelay() > 0 && isSkillDisabled(skill))
			return;

		if(!Rnd.chance(trigger.getChance()))
			return;

		// DS: Для шансовых скиллов с TARGET_SELF и условием "пвп" сам кастер будет являться aimTarget,
		// поэтому в условиях для триггера проверяем реальную цель.
		Creature realTarget = target != null && target.isCreature() ? (Creature) target : null;
		if(trigger.checkCondition(this, realTarget, aimTarget, owner, damage) && skill.checkCondition(this, aimTarget, true, true, true, false, true))
		{
			if(targets == null)
				targets = skill.getTargets(this, aimTarget, false);

			if(!skill.isNotBroadcastable() && !isCastingNow())
			{
				if(trigger.getType() != TriggerType.IDLE)
				{
					for(Creature cha : targets)
						broadcastPacket(new MagicSkillUse(this, cha, skill.getDisplayId(), skill.getDisplayLevel(), 0, 0));
				}
			}

			callSkill(skill, targets, false, true);
			disableSkill(skill, skill.getReuseDelay());
		}
	}

	private void triggerCancelEffects(TriggerInfo trigger)
	{
        SkillEntry skillEntry = trigger.getSkill();
        if(skillEntry == null)
            return;

        getAbnormalList().stop(skillEntry.getTemplate());
	}

	public boolean checkReflectSkill(Creature attacker, Skill skill)
	{
		if(this == attacker)
			return false;
		if(isDead() || attacker.isDead())
			return false;
		if(!skill.isReflectable())
			return false;
		// Не отражаем, если есть неуязвимость, иначе она может отмениться
		if(isInvulnerable() || attacker.isInvulnerable() || !skill.isOffensive())
			return false;
		// Из магических скилов отражаются только скилы наносящие урон по ХП.
		if(skill.isMagic() && skill.getSkillType() != SkillType.MDAM)
			return false;
		if(Rnd.chance(calcStat(skill.isMagic() ? Stats.REFLECT_MAGIC_SKILL : Stats.REFLECT_PHYSIC_SKILL, 0, attacker, skill)))
		{
			sendPacket(new SystemMessage(SystemMessage.YOU_COUNTERED_C1S_ATTACK).addName(attacker));
			attacker.sendPacket(new SystemMessage(SystemMessage.C1_DODGES_THE_ATTACK).addName(this));
			return true;
		}
		return false;
	}

	public boolean checkReflectDebuff(Creature effector, Skill skill)
	{
		if(this == effector)
			return false;
		if(isDead() || effector.isDead())
			return false;
		if(effector.isTrap())
			return false;
        if(effector.isRaid())
            return false;
		if(!skill.isReflectable())
			return false;
		// Не отражаем, если есть неуязвимость, иначе она может отмениться
		if(isInvulnerable() || effector.isInvulnerable() || !skill.isOffensive())
			return false;
		if(isDebuffImmune())
			return false;
		return Rnd.chance(calcStat(skill.isMagic() ? Stats.REFLECT_MAGIC_DEBUFF : Stats.REFLECT_PHYSIC_DEBUFF, 0, effector, skill));
	}

	public void doCounterAttack(Skill skill, Creature attacker, boolean blow)
	{
		if(isDead()) // если персонаж уже мертв, контратаки быть не должно
			return;
		if(isDamageBlocked(attacker) || attacker.isDamageBlocked(this)) // Не контратакуем, если есть неуязвимость, иначе она может отмениться
			return;
		if(skill == null || skill.hasEffects(EffectUseType.NORMAL) || skill.isMagic() || !skill.isOffensive() || skill.getCastRange() > 200)
			return;
		if(Rnd.chance(calcStat(Stats.COUNTER_ATTACK, 0, attacker, skill)))
		{
			double damage = 1189 * getPAtk(attacker) / Math.max(attacker.getPDef(this), 1);
			attacker.sendPacket(new SystemMessagePacket(SystemMsg.C1_IS_PERFORMING_A_COUNTERATTACK).addName(this));
			if(blow) // урон х2 для отражения blow скиллов
			{
				sendPacket(new SystemMessagePacket(SystemMsg.C1_IS_PERFORMING_A_COUNTERATTACK).addName(this));
				sendPacket(new SystemMessagePacket(SystemMsg.C1_HAS_DONE_S3_POINTS_OF_DAMAGE_TO_C2).addName(this).addName(attacker).addInteger((int) damage).addHpChange(getObjectId(), attacker.getObjectId(), (int) -damage));
				attacker.reduceCurrentHp(damage, this, skill, true, true, false, false, false, false, true);
			}
			else
				sendPacket(new SystemMessagePacket(SystemMsg.C1_IS_PERFORMING_A_COUNTERATTACK).addName(this));
			sendPacket(new SystemMessagePacket(SystemMsg.C1_HAS_DONE_S3_POINTS_OF_DAMAGE_TO_C2).addName(this).addName(attacker).addInteger((int) damage).addHpChange(getObjectId(), attacker.getObjectId(), (int) -damage));
			attacker.reduceCurrentHp(damage, this, skill, true, true, false, false, false, false, true);
		}
	}

	/**
	 * Disable this skill id for the duration of the delay in milliseconds.
	 *
	 * @param skill
	 * @param delay (seconds * 1000)
	 */
	public void disableSkill(Skill skill, long delay)
	{
		_skillReuses.put(skill.getReuseHash(), new TimeStamp(skill, delay));
	}

	public abstract boolean isAutoAttackable(Creature attacker);

	public void doAttack(Creature target)
	{
		if(target == null || isAMuted() || isAttackingNow() || isAlikeDead() || target.isDead() || !isInRange(target, 2000)) //why alikeDead?
			return;

		getListeners().onAttack(target);

		// Get the Attack Speed of the L2Character (delay (in milliseconds) before next attack)
        int sAtk = calculateAttackDelay();
		int ssGrade = 0;

        int attackReuseDelay = 0;
        boolean ssEnabled = false;
        if(isNpc())
        {
            attackReuseDelay = ((NpcTemplate) getTemplate()).getBaseReuseDelay();
            NpcTemplate.ShotsType shotType = ((NpcTemplate) getTemplate()).getShots();
            if(shotType != NpcTemplate.ShotsType.NONE && shotType != NpcTemplate.ShotsType.BSPIRIT && shotType != NpcTemplate.ShotsType.SPIRIT)
                ssEnabled = true;
        }
        else
        {
            WeaponTemplate weaponItem = getActiveWeaponTemplate();
            if(weaponItem != null)
            {
                attackReuseDelay = weaponItem.getAttackReuseDelay();
                ssGrade = weaponItem.getGrade().extOrdinal();
            }
            ssEnabled = getChargedSoulshotPower() > 0;
        }

        if(attackReuseDelay > 0)
		{
            int reuse = (int) (attackReuseDelay * getReuseModifier(target) * 666 * getBaseStats().getPAtkSpd() / 293. / getPAtkSpd());
            if(reuse > 0)
            {
                sendPacket(new SetupGaugePacket(this, SetupGaugePacket.Colors.RED, reuse));
                _attackReuseEndTime = reuse + System.currentTimeMillis() - 75;
                if(reuse > sAtk)
                    ThreadPoolManager.getInstance().schedule(new NotifyAITask(this, CtrlEvent.EVT_READY_TO_ACT), reuse);
            }
        }

		// DS: скорректировано на 1/100 секунды поскольку AI task вызывается с небольшой погрешностью
		// особенно на слабых машинах и происходит обрыв автоатаки по isAttackingNow() == true
		_attackEndTime = sAtk + System.currentTimeMillis() - 10;
		_isAttackAborted = false;

        _lastAttackTime = System.currentTimeMillis();

		AttackPacket attack = new AttackPacket(this, target, ssEnabled, ssGrade);

		setHeading(PositionUtils.calculateHeadingFrom(this, target));

		switch(getBaseStats().getAttackType())
		{
			case BOW:
			case CROSSBOW:
            case TWOHANDCROSSBOW:
				doAttackHitByBow(attack, target, sAtk);
				break;
			case POLE:
				doAttackHitByPole(attack, target, sAtk);
				break;
			case DUAL:
			case DUALFIST:
			case DUALDAGGER:
            case DUALBLUNT:
				doAttackHitByDual(attack, target, sAtk);
				break;
			default:
				doAttackHitSimple(attack, target, sAtk);
		}

		if(attack.hasHits())
			broadcastPacket(attack);
	}

    private void doAttackHitSimple(AttackPacket attack, Creature target, int sAtk)
    {
        int attackcountmax = (int) Math.round(calcStat(Stats.ATTACK_TARGETS_COUNT, 0, target, null));
        if(attackcountmax > 0 && !isInPeaceZone()) // Гварды с пикой, будут атаковать только одиночные цели в городе
        {
            int angle = getPhysicalAttackAngle();
            int range = getPhysicalAttackRadius();

            int attackedCount = 1;

            for(Creature t : getAroundCharacters(range, 200))
            {
                if(attackedCount <= attackcountmax)
                {
	                if(t == target || t.isDead() || !PositionUtils.isFacing(this, t, angle))
		                continue;

	                // @Rivelia. Pole should not hit targets that are flagged if we are not flagged.
	                if(t.isAutoAttackable(this) && ((this.getPvpFlag() == 0 && t.getPvpFlag() == 0) || this.getPvpFlag() != 0))
	                {
		                doAttackHitSimple0(attack, t, 1., false, sAtk, false);
		                attackedCount++;
	                }
                }
                else
	                break;
            }
        }

        doAttackHitSimple0(attack, target, 1., true, sAtk, true);
    }

	private void doAttackHitSimple0(AttackPacket attack, Creature target, double multiplier, boolean unchargeSS, int sAtk, boolean notify)
	{
		int damage1 = 0;
		boolean shld1 = false;
		boolean crit1 = false;
		boolean miss1 = Formulas.calcHitMiss(this, target);

		if(!miss1)
		{
			AttackInfo info = Formulas.calcPhysDam(this, target, null, false, false, attack._soulshot, false);
            if(info != null)
            {
                damage1 = (int) (info.damage * multiplier);
                shld1 = info.shld;
                crit1 = info.crit;
            }
		}

		ThreadPoolManager.getInstance().schedule(new HitTask(this, target, damage1, crit1, miss1, attack._soulshot, shld1, unchargeSS, notify, sAtk), sAtk / 2);

		attack.addHit(target, damage1, miss1, crit1, shld1);
	}

	private void doAttackHitByBow(AttackPacket attack, Creature target, int sAtk)
	{
		int damage1 = 0;
		boolean shld1 = false;
		boolean crit1 = false;

		// Calculate if hit is missed or not
		boolean miss1 = Formulas.calcHitMiss(this, target);

		reduceArrowCount();

		if(!miss1)
		{
			AttackInfo info = Formulas.calcPhysDam(this, target, null, false, false, attack._soulshot, false);
            if(info != null)
            {
                damage1 = (int) info.damage;
                shld1 = info.shld;
                crit1 = info.crit;
            }

			/* В Lindvior атака теперь не зависит от расстояния.
			int range = getPhysicalAttackRange();
			damage1 *= Math.min(range, getDistance(target)) / range * .4 + 0.8; // разброс 20% в обе стороны
			*/
		}

		ThreadPoolManager.getInstance().schedule(new HitTask(this, target, damage1, crit1, miss1, attack._soulshot, shld1, true, true, sAtk), sAtk / 2);

		attack.addHit(target, damage1, miss1, crit1, shld1);
	}

	private void doAttackHitByDual(AttackPacket attack, Creature target, int sAtk)
	{
		int damage1 = 0;
		int damage2 = 0;
		boolean shld1 = false;
		boolean shld2 = false;
		boolean crit1 = false;
		boolean crit2 = false;

		boolean miss1 = Formulas.calcHitMiss(this, target);
		boolean miss2 = Formulas.calcHitMiss(this, target);

		if(!miss1)
		{
			AttackInfo info = Formulas.calcPhysDam(this, target, null, true, false, attack._soulshot, false);
            if(info != null)
            {
                damage1 = (int) info.damage;
                shld1 = info.shld;
                crit1 = info.crit;
            }
		}

		if(!miss2)
		{
            AttackInfo info = Formulas.calcPhysDam(this, target, null, true, false, attack._soulshot, false);
            if(info != null)
            {
                damage2 = (int) info.damage;
                shld2 = info.shld;
                crit2 = info.crit;
            }
		}

		// Create a new hit task with Medium priority for hit 1 and for hit 2 with a higher delay
		ThreadPoolManager.getInstance().schedule(new HitTask(this, target, damage1, crit1, miss1, attack._soulshot, shld1, true, false, sAtk / 2), sAtk / 4);
		ThreadPoolManager.getInstance().schedule(new HitTask(this, target, damage2, crit2, miss2, attack._soulshot, shld2, false, true, sAtk), sAtk / 2);

		attack.addHit(target, damage1, miss1, crit1, shld1);
		attack.addHit(target, damage2, miss2, crit2, shld2);
	}

	private void doAttackHitByPole(AttackPacket attack, Creature target, int sAtk)
	{
        // Используем Math.round т.к. обычный кастинг обрезает к меньшему
		// double d = 2.95. int i = (int)d, выйдет что i = 2
		// если 1% угла или 1 дистанции не играет огромной роли, то для
		// количества целей это критично
		int attackcountmax = (int) Math.round(calcStat(Stats.POLE_TARGET_COUNT, 0, target, null));
        attackcountmax += (int) Math.round(calcStat(Stats.ATTACK_TARGETS_COUNT, 0, target, null));

        if(isBoss())
			attackcountmax += 27;
		else if(isRaid())
			attackcountmax += 12;
		else if(isMonster())
			attackcountmax += getLevel() / 7.5;

        if(attackcountmax > 0 && !isInPeaceZone())// Гварды с пикой, будут атаковать только одиночные цели в городе
        {
            int angle = (int) calcStat(Stats.POLE_ATTACK_ANGLE, getPhysicalAttackAngle(), target, null);
            int range = getPhysicalAttackRange() + getPhysicalAttackRadius();

            double mult = 1.;
            _poleAttackCount = 1;

            for(Creature t : getAroundCharacters(range, 200))
            {
                if(_poleAttackCount <= attackcountmax)
                {
                    if(t == target || t.isDead() || !PositionUtils.isFacing(this, t, angle))
                        continue;

                    // @Rivelia. Pole should not hit targets that are flagged if we are not flagged.
                    if(t.isAutoAttackable(this) && ((this.getPvpFlag() == 0 && t.getPvpFlag() == 0) || this.getPvpFlag() != 0))
                    {
                        doAttackHitSimple0(attack, t, mult, false, sAtk, false);
                        mult *= Config.ALT_POLE_DAMAGE_MODIFIER;
                        _poleAttackCount++;
                    }
                }
                else
                    break;
            }

            _poleAttackCount = 0;
        }

        doAttackHitSimple0(attack, target, 1., true, sAtk, true);
	}

	public long getAnimationEndTime()
	{
		return _animationEndTime;
	}

	public void doCast(SkillEntry skillEntry, Creature target, boolean forceUse)
	{
		if(skillEntry == null)
			return;

        Skill skill = skillEntry.getTemplate();
		if(skill.getReferenceItemId() > 0)
		{
			if(!consumeItemMp(skill.getReferenceItemId(), skill.getReferenceItemMpConsume()))
				return;
		}

		if(target == null)
			target = skill.getAimingTarget(this, getTarget());
		if(target == null)
			return;

		getListeners().onMagicUse(skill, target, false);

        Location groundLoc = null;
		if(skill.getTargetType() == SkillTargetType.TARGET_GROUND)
		{
			if(isPlayer())
			{
				groundLoc = getPlayer().getGroundSkillLoc();
				if(groundLoc != null)
					setHeading(PositionUtils.calculateHeadingFrom(getX(), getY(), groundLoc.getX(), groundLoc.getY()), true);
			}
		}
		else if(this != target)
			setHeading(PositionUtils.calculateHeadingFrom(this, target));

        int magicId = skill.getDisplayId();
        int level = skill.getDisplayLevel();

        int skillTime = skill.isSkillTimePermanent() ? skill.getHitTime() : Formulas.calcSkillCastSpd(this, skill, skill.getHitTime());
		int skillInterruptTime = skill.isSkillTimePermanent() ? skill.getSkillInterruptTime() : Formulas.calcSkillCastSpd(this, skill, skill.getSkillInterruptTime());

        _animationEndTime = System.currentTimeMillis() + skillTime;

		if(skill.isMagic() && !skill.isSkillTimePermanent() && getChargedSpiritshotPower() > 0)
		{
			skillTime = (int) (0.70 * skillTime);
			skillInterruptTime = (int) (0.70 * skillInterruptTime);
		}

		int minCastTimePhysical = Math.min(Config.SKILLS_CAST_TIME_MIN_PHYSICAL, skill.getHitTime());
		int minCastTimeMagical = Math.min(Config.SKILLS_CAST_TIME_MIN_MAGICAL, skill.getHitTime());
		if(!skill.isSkillTimePermanent())
		{
			if(skill.isMagic() && skillTime < minCastTimeMagical)
			{
				skillTime = minCastTimeMagical;
				skillInterruptTime = 0;
			}
			else if(!skill.isMagic() && skillTime < minCastTimePhysical)
			{
				skillTime = minCastTimePhysical;
				skillInterruptTime = 0;
			}
		}

        boolean criticalBlow = skill.calcCriticalBlow(this, target);

		long reuseDelay = Math.max(0, Formulas.calcSkillReuseDelay(this, skill));

        if(!skill.isNotBroadcastable())
        {
            MagicSkillUse msu = new MagicSkillUse(this, target, skill.getDisplayId(), level, skillTime, reuseDelay);
            msu.setReuseSkillId(skill.getReuseSkillId());
            msu.setGroundLoc(groundLoc);
            msu.setCriticalBlow(criticalBlow);
            if(isServitor()) // TODO: [Bonux] Переделать.
            {
                Servitor.UsedSkill servitorUsedSkill = ((Servitor) this).getUsedSkill();
                if(servitorUsedSkill != null && servitorUsedSkill.getSkill() == skill)
                {
                    msu.setServitorSkillInfo(servitorUsedSkill.getActionId());
                    ((Servitor) this).setUsedSkill(null);
                }
            }
            broadcastPacket(msu);
        }

		disableSkill(skill, reuseDelay);

		if(skill.getTargetType() == SkillTargetType.TARGET_HOLY)
			target.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, this, 1);

		final double mpConsume1 = skill.getMpConsume1();
		if(mpConsume1 > 0)
		{
			if(_currentMp < mpConsume1)
			{
				sendPacket(SystemMsg.NOT_ENOUGH_MP);
                onCastEndTime(target, null, false);
				return;
			}
		}

        if(!skill.isHandler() && isPlayable())
        {
            if(skill.getItemConsumeId() > 0 && skill.getItemConsume() > 0)
            {
                if(skill.isItemConsumeFromMaster())
                {
                    Player master = getPlayer();
                    if(master == null)
                        return;

                    if(ItemFunctions.getItemCount(master, skill.getItemConsumeId()) < skill.getItemConsume())
                    {
                        master.sendPacket(SystemMsg.THERE_ARE_NOT_ENOUGH_NECESSARY_ITEMS_TO_USE_THE_SKILL);
                        return;
                    }
                }
                else if(ItemFunctions.getItemCount((Playable) this, skill.getItemConsumeId()) < skill.getItemConsume())
                {
                    sendPacket(SystemMsg.THERE_ARE_NOT_ENOUGH_NECESSARY_ITEMS_TO_USE_THE_SKILL);
                    return;
                }
            }
        }

		if(isPlayer())
		{
			if(skill.getSkillType() == SkillType.PET_SUMMON)
				sendPacket(SystemMsg.SUMMONING_YOUR_PET);
			else
				sendPacket(new SystemMessagePacket(SystemMsg.YOU_USE_S1).addSkillName(magicId, level));
		}

		if(mpConsume1 > 0)
			reduceCurrentMp(mpConsume1, null);

		if(!skill.isHandler() && isPlayable())
		{
			if(skill.getItemConsumeId() > 0 && skill.getItemConsume() > 0)
            {
                if(skill.isItemConsumeFromMaster())
                {
                    Player master = getPlayer();
                    if(master != null)
                        master.consumeItem(skill.getItemConsumeId(), skill.getItemConsume(), true);
                }
                else
                    consumeItem(skill.getItemConsumeId(), skill.getItemConsume(), true);
            }
		}

		Location flyLoc = null;
		switch(skill.getFlyType())
		{
			case CHARGE:
				flyLoc = getFlyLocation(target, skill);
				if(flyLoc != null)
					broadcastPacket(new FlyToLocationPacket(this, flyLoc, skill.getFlyType(), skill.getFlySpeed(), skill.getFlyDelay(), skill.getFlyAnimationSpeed()));
				break;
			case WARP_BACK:
			case WARP_FORWARD:
				flyLoc = getFlyLocation(this, skill);
				if(flyLoc != null)
					broadcastPacket(new FlyToLocationPacket(this, flyLoc, skill.getFlyType(), (skill.getFlyRadius() / skillTime) * 1000, skill.getFlyDelay(), skill.getFlyAnimationSpeed()));
				break;
		}

		if(flyLoc != null)
            _flyLoc = flyLoc;

        _castingSkill = skill;
        _castInterruptTime = System.currentTimeMillis() + skillInterruptTime;

        if(criticalBlow)
            _isCriticalBlowCastingSkill = true;

        setCastingTarget(target);

		//if(isPlayer())
			//sendPacket(new SetupGaugePacket(this, SetupGaugePacket.Colors.BLUE, skillTime));

		// Create a task MagicUseTask with Medium priority to launch the
		// MagicSkill at the end of the casting time
		final int tickInterval = skill.getTickInterval() > 0 ? skill.getTickInterval() : skillTime;

        _castInterval = skillTime;
        _skillLaunchedTask = ThreadPoolManager.getInstance().schedule(new MagicLaunchedTask(this, forceUse), skillInterruptTime);
        _skillTask = ThreadPoolManager.getInstance().schedule(new MagicUseTask(this, forceUse), tickInterval);

        skill.onStartCast(skillEntry, this, skill.getTargets(this, target, forceUse));

        useTriggers(target, TriggerType.ON_START_CAST, null, skill, 0);
    }

	public Location getFlyLocation(GameObject target, Skill skill)
	{
		if(target != null && target != this)
		{
			Location loc;

			int heading = target.getHeading();
			if(!skill.isFlyDependsOnHeading())
				heading = PositionUtils.calculateHeadingFrom(target, this);

			double radian = PositionUtils.convertHeadingToDegree(heading) + skill.getFlyPositionDegree();
			if(radian > 360)
				radian -= 360;

			radian = (Math.PI * radian) / 180;

			loc = new Location(target.getX() + (int) (Math.cos(radian) * 40), target.getY() + (int) (Math.sin(radian) * 40), target.getZ());

			if(isFlying())
			{
				if(isInFlyingTransform() && ((loc.z <= 0) || (loc.z >= 6000)))
					return null;
				if(GeoEngine.moveCheckInAir(getX(), getY(), getZ(), loc.x, loc.y, loc.z, getCollisionRadius(), getGeoIndex()) == null)
					return null;
			}
			else
			{
				loc.correctGeoZ();

				if(!GeoEngine.canMoveToCoord(getX(), getY(), getZ(), loc.x, loc.y, loc.z, getGeoIndex()))
				{
					loc = target.getLoc(); // Если не получается встать рядом с объектом, пробуем встать прямо в него
					if(!GeoEngine.canMoveToCoord(getX(), getY(), getZ(), loc.x, loc.y, loc.z, getGeoIndex()))
						return null;
				}
			}

			return loc;
		}

		int x1 = 0;
		int y1 = 0;
		int z1 = 0;
		
		if(skill.getFlyType() == FlyType.THROW_UP)
		{
			x1 = 0;
			y1 = 0;
			z1 = getZ() + skill.getFlyRadius();
		}
		else
		{

		double radian = PositionUtils.convertHeadingToRadian(getHeading());
			x1 = -(int) (Math.sin(radian) * skill.getFlyRadius());
			y1 = (int) (Math.cos(radian) * skill.getFlyRadius());
		}

		if(isFlying())
			return GeoEngine.moveCheckInAir(getX(), getY(), getZ(), getX() + x1, getY() + y1, getZ() + z1, getCollisionRadius(), getGeoIndex());
		return GeoEngine.moveCheck(getX(), getY(), getZ(), getX() + x1, getY() + y1, getGeoIndex());
	}

	public final void doDie(Creature killer)
	{
		// killing is only possible one time
		if(!isDead.compareAndSet(false, true))
			return;

		onDeath(killer);
	}

	protected void onDeath(Creature killer)
	{
		if(killer != null)
		{
			Player killerPlayer = killer.getPlayer();
			if(killerPlayer != null)
				killerPlayer.getListeners().onKillIgnorePetOrSummon(this);

			killer.getListeners().onKill(this);

			if(isPlayer() && killer.isPlayable())
				_currentCp = 0;
		}

		setTarget(null);

		abortCast(true, false);
		abortAttack(true, false);

		stopMove();
		stopAttackStanceTask();
		stopRegeneration();

		_currentHp = 0;

        if(isPlayable())
        {
            final TIntSet effectsToRemove = new TIntHashSet();

            // Stop all active skills effects in progress on the L2Character
            if(isPreserveAbnormal() || isSalvation())
            {
                if(isSalvation() && isPlayer() && !getPlayer().isInOlympiadMode())
                    getPlayer().reviveRequest(getPlayer(), 100, false);

                for(Abnormal abnormal : getAbnormalList())
                {
                    int skillId = abnormal.getId();
                    if(skillId == 2168)
                    {
                        effectsToRemove.add(skillId);
                        continue;
                    }
                    // Noblesse Blessing Buff/debuff effects are retained after
                    // death. However, Noblesse Blessing and Lucky Charm are lost as normal.
                    for(Effect effect : abnormal.getEffects())
                    {
                        if(effect.getEffectType() == EffectType.p_preserve_abnormal)
                            effectsToRemove.add(skillId);
                        else if(effect.getEffectType() == EffectType.AgathionResurrect)
                        {
                            if(isPlayer())
                                getPlayer().setAgathionRes(true);
                            effectsToRemove.add(skillId);
                        }
                    }
                }
            }
            else
            {
                for(Abnormal abnormal : getAbnormalList())
                {
                    // Некоторые эффекты сохраняются при смерти
                    if(!abnormal.getSkill().isPreservedOnDeath())
                        effectsToRemove.add(abnormal.getSkill().getId());
                }

                deleteCubics();
            }

            getAbnormalList().stop(effectsToRemove);
        }

		if(isPlayer())
			getPlayer().sendUserInfo(true); // Принудительно посылаем, исправляет баг, когда персонаж умирает в воздушных оковах.

		broadcastStatusUpdate();
		
		ThreadPoolManager.getInstance().execute(new NotifyAITask(this, CtrlEvent.EVT_DEAD, killer, null, null));

		if(killer != null)
			killer.useTriggers(this, TriggerType.ON_KILL, null, null, 0);

		getListeners().onDeath(killer);
	}

	protected void onRevive()
	{
		useTriggers(this, TriggerType.ON_REVIVE, null, null, 0);
	}

	public void enableSkill(Skill skill)
	{
		_skillReuses.remove(skill.getReuseHash());
	}

	/**
	 * Return a map of 32 bits (0x00000000) containing all abnormal effects
	 */
	public Set<AbnormalEffect> getAbnormalEffects()
	{
		return _abnormalEffects;
	}

	public AbnormalEffect[] getAbnormalEffectsArray()
	{
		return _abnormalEffects.toArray(new AbnormalEffect[_abnormalEffects.size()]);
	}

	public int getPAccuracy()
	{
		return (int) Math.round(calcStat(Stats.P_ACCURACY_COMBAT, 0, null, null));
	}

	public int getMAccuracy()
	{
		return (int) calcStat(Stats.M_ACCURACY_COMBAT, 0, null, null);
	}


	public Collection<SkillEntry> getAllSkills()
	{
		return _skills.valueCollection();
	}


	public final SkillEntry[] getAllSkillsArray() {
		return _skills.values(new SkillEntry[_skills.size()]);
	}

	public final double getAttackSpeedMultiplier()
	{
		return 1.1 * getPAtkSpd() / getBaseStats().getPAtkSpd();
	}

	public int getBuffLimit()
	{
		return (int) calcStat(Stats.BUFF_LIMIT, Config.ALT_BUFF_LIMIT, null, null);
	}

	public Skill getCastingSkill()
	{
		return _castingSkill;
	}

    public boolean isCriticalBlowCastingSkill()
    {
        return _isCriticalBlowCastingSkill;
    }

	public int getPCriticalHit(Creature target)
	{
		return (int) Math.round(calcStat(Stats.BASE_P_CRITICAL_RATE, getBaseStats().getPCritRate(), target, null));
	}

	public int getMCriticalHit(Creature target, Skill skill)
	{
		return (int) Math.round(calcStat(Stats.BASE_M_CRITICAL_RATE, getBaseStats().getMCritRate(), target, skill));
	}

	/**
	 * Return the current CP of the L2Character.
	 */
	public double getCurrentCp()
	{
		return _currentCp;
	}

	public final double getCurrentCpRatio()
	{
		return getCurrentCp() / getMaxCp();
	}

	public final double getCurrentCpPercents()
	{
		return getCurrentCpRatio() * 100.;
	}

	public final boolean isCurrentCpFull()
	{
		return getCurrentCp() >= getMaxCp();
	}

	public final boolean isCurrentCpZero()
	{
		return getCurrentCp() < 1;
	}

	public double getCurrentHp()
	{
		return _currentHp;
	}

	public final double getCurrentHpRatio()
	{
		return getCurrentHp() / getMaxHp();
	}

	public final double getCurrentHpPercents()
	{
		return getCurrentHpRatio() * 100.;
	}

	public final boolean isCurrentHpFull()
	{
		return getCurrentHp() >= getMaxHp();
	}

	public final boolean isCurrentHpZero()
	{
		return getCurrentHp() < 1;
	}

	public double getCurrentMp()
	{
		return _currentMp;
	}

	public final double getCurrentMpRatio()
	{
		return getCurrentMp() / getMaxMp();
	}

	public final double getCurrentMpPercents()
	{
		return getCurrentMpRatio() * 100.;
	}

	public final boolean isCurrentMpFull()
	{
		return getCurrentMp() >= getMaxMp();
	}

	public final boolean isCurrentMpZero()
	{
		return getCurrentMp() < 1;
	}

	public Location getDestination()
	{
		if(destination == null)
			return new Location(0,0,0);
		return destination;
	}

	public int getINT()
	{
		return (int) calcStat(Stats.STAT_INT, getBaseStats().getINT(), null, null);
	}

	public int getSTR()
	{
		return (int) calcStat(Stats.STAT_STR, getBaseStats().getSTR(), null, null);
	}

	public int getCON()
	{
		return (int) calcStat(Stats.STAT_CON, getBaseStats().getCON(), null, null);
	}

	public int getMEN()
	{
		return (int) calcStat(Stats.STAT_MEN, getBaseStats().getMEN(), null, null);
	}

	public int getDEX()
	{
		return (int) calcStat(Stats.STAT_DEX, getBaseStats().getDEX(), null, null);
	}

	public int getWIT()
	{
		return (int) calcStat(Stats.STAT_WIT, getBaseStats().getWIT(), null, null);
	}

	public int getPEvasionRate(Creature target)
	{
		return (int) Math.round(calcStat(Stats.P_EVASION_RATE, 0, target, null));
	}

	public int getMEvasionRate(Creature target)
	{
		return (int) calcStat(Stats.M_EVASION_RATE, 0, target, null);
	}

	public List<Creature> getAroundCharacters(int radius, int height)
	{
		if(!isVisible())
			return Collections.emptyList();
		return World.getAroundCharacters(this, radius, height);
	}

	public List<NpcInstance> getAroundNpc(int range, int height)
	{
		if(!isVisible())
			return Collections.emptyList();
		return World.getAroundNpc(this, range, height);
	}

	public boolean knowsObject(GameObject obj)
	{
		return World.getAroundObjectById(this, obj.getObjectId()) != null;
	}

	public final SkillEntry getKnownSkill(int skillId)
	{
		return _skills.get(skillId);
	}

	public final int getMagicalAttackRange(Skill skill)
	{
		if(skill != null)
			return (int) calcStat(Stats.MAGIC_ATTACK_RANGE, skill.getCastRange(), null, skill);
		return getBaseStats().getAtkRange();
	}

	public int getMAtk(Creature target, Skill skill)
	{
		if(skill != null && skill.getMatak() > 0)
			return skill.getMatak();
		return (int) Math.round(calcStat(Stats.MAGIC_ATTACK, getBaseStats().getMAtk(), target, skill));
	}

	public int getMAtkSpd()
	{
		return (int) calcStat(Stats.MAGIC_ATTACK_SPEED, getBaseStats().getMAtkSpd(), null, null);
	}

	public int getMaxCp()
	{
		return Math.max(1, (int) calcStat(Stats.MAX_CP, getBaseStats().getCpMax(), null, null));
	}

	public int getMaxHp()
	{
		return Math.max(1, (int) calcStat(Stats.MAX_HP, getBaseStats().getHpMax(), null, null));
	}

	public int getMaxMp()
	{
		return Math.max(1, (int) calcStat(Stats.MAX_MP, getBaseStats().getMpMax(), null, null));
	}

	public int getMDef(Creature target, Skill skill)
	{
        double mDef = calcStat(Stats.MAGIC_DEFENCE, getBaseStats().getMDef(), target, skill);
        return (int) Math.max(mDef, getBaseStats().getMDef() / 2.);
	}

	public double getMinDistance(GameObject obj)
	{
		double distance = getCollisionRadius();

		if(obj != null && obj.isCreature())
			distance += ((Creature) obj).getCollisionRadius();

		return distance;
	}

	@Override
	public String getName()
	{
		return requireNonNullElse(_name, STRING_EMPTY);
	}

	public int getPAtk(Creature target)
	{
		return (int) calcStat(Stats.POWER_ATTACK, getBaseStats().getPAtk(), target, null);
	}

	public int getPAtkSpd()
	{
		return (int) calcStat(Stats.POWER_ATTACK_SPEED, getBaseStats().getPAtkSpd(), null, null);
	}

	public int getPDef(Creature target)
	{
        double pDef = calcStat(Stats.POWER_DEFENCE, getBaseStats().getPDef(), target, null);
        return (int) Math.max(pDef, getBaseStats().getPDef() / 2.);
	}

	public int getPhysicalAttackRange()
	{
		return (int) calcStat(Stats.POWER_ATTACK_RANGE, getBaseStats().getAtkRange());
	}

    public int getPhysicalAttackRadius()
    {
        return (int) calcStat(Stats.P_ATTACK_RADIUS, getBaseStats().getAttackRadius());
    }

    public int getPhysicalAttackAngle()
    {
        return getBaseStats().getAttackAngle();
    }

	public int getRandomDamage()
	{
		WeaponTemplate weaponItem = getActiveWeaponTemplate();
		if(weaponItem == null)
			return getBaseStats().getRandDam();
		return weaponItem.getRandomDamage();
	}

	public double getReuseModifier(Creature target)
	{
		return calcStat(Stats.ATK_REUSE, 1, target, null);
	}

	public final int getShldDef()
	{
		return (int) calcStat(Stats.SHIELD_DEFENCE, getBaseStats().getShldDef(), null, null);
	}

    public double getPhysicalAbnormalResist()
    {
        return calcStat(Stats.PHYSICAL_ABNORMAL_RESIST, getBaseStats().getPhysicalAbnormalResist());
    }

    public double getMagicAbnormalResist()
    {
        return calcStat(Stats.MAGIC_ABNORMAL_RESIST, getBaseStats().getMagicAbnormalResist());
    }

	public int getSkillLevel(int skillId)
	{
		return getSkillLevel(skillId, -1);
	}

	public final int getSkillLevel(int skillId, int def)
	{
        SkillEntry skill = _skills.get(skillId);
		if(skill == null)
			return def;
		return skill.getLevel();
	}

	public GameObject getTarget()
	{
		return target.get();
	}

	public final int getTargetId()
	{
		GameObject target = getTarget();
		return target == null ? -1 : target.getObjectId();
	}

	public CreatureTemplate getTemplate()
	{
		return _template;
	}

	protected void setTemplate(CreatureTemplate template)
	{
		_template = template;
	}

	public String getTitle()
	{
		return requireNonNullElse(_title, STRING_EMPTY);
	}

	public double headingToRadians(int heading)
	{
		return (heading - 32768) / HEADINGS_IN_PI;
	}

	public final boolean isAlikeDead()
	{
		return _fakeDeath || isDead();
	}

	public final boolean isAttackingNow()
	{
		return _attackEndTime > System.currentTimeMillis();
	}

    public final long getLastAttackTime()
    {
        return _lastAttackTime;
    }

    public final boolean isPreserveAbnormal()
    {
        return _isPreserveAbnormal;
    }

	public final boolean isSalvation()
	{
		return _isSalvation;
	}

	public boolean isEffectImmune(Creature effector)
	{
		Creature exception = _effectImmunityException.get();
		if(exception != null && exception == effector)
			return false;

		return getFlags().getEffectImmunity().get();
	}

	public boolean isBuffImmune()
	{
		return getFlags().getBuffImmunity().get();
	}

	public boolean isDebuffImmune()
	{
		return getFlags().getDebuffImmunity().get() || isPeaceNpc();
	}

	public boolean isDead()
	{
		return _currentHp < 0.5 || isDead.get();
	}

	@Override
	public final boolean isFlying()
	{
		return _flying;
	}

	/**
	 * Находится ли персонаж в боевой позе
	 * @return true, если персонаж в боевой позе, атакован или атакует
	 */
	public final boolean isInCombat()
	{
		return System.currentTimeMillis() < _stanceEndTime;
	}

	public boolean isMageClass()
	{
		return getBaseStats().getMAtk() > 3;
	}

	public final boolean isRunning()
	{
		return _running;
	}

	public boolean isSkillDisabled(Skill skill)
	{
		TimeStamp sts = _skillReuses.get(skill.getReuseHash());
		if(sts == null)
			return false;
		if(sts.hasNotPassed())
			return true;
		_skillReuses.remove(skill.getReuseHash());
		return false;
	}

	public final boolean isTeleporting()
	{
		return isTeleporting.get();
	}

	/**
	 * Возвращает позицию цели, в которой она будет через пол секунды.
	 */
	public Location getIntersectionPoint(Creature target)
	{
		if(!PositionUtils.isFacing(this, target, 90))
			return new Location(target.getX(), target.getY(), target.getZ());
		double angle = PositionUtils.convertHeadingToDegree(target.getHeading()); // угол в градусах
		double radian = Math.toRadians(angle - 90); // угол в радианах
		double range = target.getMoveSpeed() / 2; // расстояние, пройденное за 1 секунду, равно скорости. Берем половину.
		return new Location((int) (target.getX() - range * Math.sin(radian)), (int) (target.getY() + range * Math.cos(radian)), target.getZ());
	}

	public Location applyOffset(Location point, int offset)
	{
		if(offset <= 0)
			return point;

		long dx = point.x - getX();
		long dy = point.y - getY();
		long dz = point.z - getZ();

		double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

		if(distance <= offset)
		{
			point.set(getX(), getY(), getZ());
			return point;
		}

		if(distance >= 1)
		{
			double cut = offset / distance;
			point.x -= (int) (dx * cut + 0.5);
			point.y -= (int) (dy * cut + 0.5);
			point.z -= (int) (dz * cut + 0.5);

			if(!isFlying() && !isInBoat() && !isInWater() && !isBoat())
				point.correctGeoZ();
		}

		return point;
	}

	public List<Location> applyOffset(List<Location> points, int offset)
	{
		offset = offset >> 4;
		if(offset <= 0)
			return points;

		long dx = points.get(points.size() - 1).x - points.get(0).x;
		long dy = points.get(points.size() - 1).y - points.get(0).y;
		long dz = points.get(points.size() - 1).z - points.get(0).z;

		double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
		if(distance <= offset)
		{
			Location point = points.get(0);
			points.clear();
			points.add(point);
			return points;
		}

		if(distance >= 1)
		{
			double cut = offset / distance;
			int num = (int) (points.size() * cut + 0.5);
			for(int i = 1; i <= num && points.size() > 0; i++)
				points.remove(points.size() - 1);
		}

		return points;
	}

	private Location setSimplePath(Location dest)
	{
		List<Location> moveList = GeoMove.constructMoveList(getLoc(), dest);
		if(moveList.isEmpty())
			return null;
		_targetRecorder.clear();
		_targetRecorder.add(moveList);
		return moveList.get(moveList.size() - 1);
	}

	private Location buildPathTo(int x, int y, int z, int offset, boolean pathFind)
	{
		return buildPathTo(x, y, z, offset, null, false, pathFind);
	}

	private Location buildPathTo(int x, int y, int z, int offset, Creature follow, boolean forestalling, boolean pathFind)
	{
		int geoIndex = getGeoIndex();

		Location dest;

		if(forestalling && follow != null && follow.isMoving)
			dest = getIntersectionPoint(follow);
		else
			dest = new Location(x, y, z);

		if(isInBoat() || isBoat() || !Config.ALLOW_GEODATA)
		{
			applyOffset(dest, offset);
			return setSimplePath(dest);
		}

		if(isFlying() || isInWater())
		{
			applyOffset(dest, offset);

			Location nextloc;

			if(isFlying())
			{
				if(GeoEngine.canSeeCoord(this, dest.x, dest.y, dest.z, true))
					return setSimplePath(dest);

				// DS: При передвижении обсервера клавишами клиент шлет очень далекие (дистанция больше 2000) координаты,
				// поэтому обычная процедура проверки не работает. Используем имитацию плавания в воде.
                if(isObservePoint())
                    nextloc = GeoEngine.moveInWaterCheck(getX(), getY(), getZ(), dest.x, dest.y, dest.z, 15000, geoIndex);
                else
                    nextloc = GeoEngine.moveCheckInAir(getX(), getY(), getZ(), dest.x, dest.y, dest.z, getCollisionRadius(), geoIndex);

                if(nextloc != null && !nextloc.equals(getX(), getY(), getZ()))
                    return setSimplePath(nextloc);
			}
			else
			{
				int waterZ = getWaterZ();
				nextloc = GeoEngine.moveInWaterCheck(getX(), getY(), getZ(), dest.x, dest.y, dest.z, waterZ, geoIndex);
				if(nextloc == null)
					return null;

				List<Location> moveList = GeoMove.constructMoveList(getLoc(), nextloc.clone());
				_targetRecorder.clear();
				if(!moveList.isEmpty())
					_targetRecorder.add(moveList);

				int dz = dest.z - nextloc.z;
				// если пытаемся выбратся на берег, считаем путь с точки выхода до точки назначения
				if(dz > 0 && dz < 128)
				{
					moveList = GeoEngine.MoveList(nextloc.x, nextloc.y, nextloc.z, dest.x, dest.y, geoIndex, false);
					if(moveList != null) // null - до конца пути дойти нельзя
					{
						if(!moveList.isEmpty()) // уже стоим на нужной клетке
							_targetRecorder.add(moveList);
					}
				}

				if(!moveList.isEmpty())
					return moveList.get(moveList.size() - 1);
			}
			return null;
		}

		List<Location> moveList = GeoEngine.MoveList(getX(), getY(), getZ(), dest.x, dest.y, geoIndex, true); // onlyFullPath = true - проверяем весь путь до конца
		if(moveList != null) // null - до конца пути дойти нельзя
		{
			if(moveList.size() < 2) // уже стоим на нужной клетке
				return null;
			applyOffset(moveList, offset);
			if(moveList.size() < 2) // уже стоим на нужной клетке
				return null;
			_targetRecorder.clear();
			_targetRecorder.add(moveList);
			return moveList.get(moveList.size() - 1);
		}

		if(pathFind || isFakePlayer())
		{
			List<List<Location>> targets = GeoMove.findMovePath(getX(), getY(), getZ(), dest.getX(), dest.getY(), dest.getZ(), this, geoIndex);
			if(!targets.isEmpty())
			{
				moveList = targets.remove(targets.size() - 1);
				applyOffset(moveList, offset);
				if(!moveList.isEmpty())
					targets.add(moveList);
				if(!targets.isEmpty())
				{
					_targetRecorder.clear();
					_targetRecorder.addAll(targets);
					for(int i = targets.size() - 1; i >= 0; i--)
					{
						List<Location> target = targets.get(i);
						if(!target.isEmpty())
                        {
                            isPathfindMoving = true;
                            return target.get(target.size() - 1);
                        }
					}
					return null;
				}
			}
		}

		if(!isFakePlayer()) // расчитываем путь куда сможем дойти, только для игровых персонажей
		{
			applyOffset(dest, offset);

			moveList = GeoEngine.MoveList(getX(), getY(), getZ(), dest.x, dest.y, geoIndex, false); // onlyFullPath = false - идем до куда можем
			if(moveList != null && moveList.size() > 1) // null - нет геодаты, empty - уже стоим на нужной клетке
			{
				_targetRecorder.clear();
				_targetRecorder.add(moveList);
				return moveList.get(moveList.size() - 1);
			}
		}

		return null;
	}

	public Creature getFollowTarget()
	{
		return followTarget.get();
	}

	public void setFollowTarget(Creature target)
	{
		followTarget = target == null ? HardReferences.<Creature> emptyRef() : target.getRef();
	}

	public boolean followToCharacter(Creature target, int offset, boolean forestalling)
	{
		return followToCharacter(target.getLoc(), target, offset, forestalling);
	}

	public boolean followToCharacter(Location loc, Creature target, int offset, boolean forestalling)
	{
		moveLock.lock();
		try
		{
			if(isMovementDisabled() || target == null || isInBoat() && !isInShuttle() || target.isInvisible(this))
				return false;

            if(getReflection() != target.getReflection())
                return false;

            if(getDistance(target) > 5000) // TODO: Вынести в конфиг?!?
                return false;

			offset = Math.max(offset, 10);
			if(isFollow && target == getFollowTarget() && offset == _offset)
				return true;

			if(Math.abs(getZ() - target.getZ()) > 1000 && !isFlying())
				return false;

			getAI().clearNextAction();

			stopMove(false, false);

			if(buildPathTo(loc.x, loc.y, loc.z, 0, target, forestalling, !target.isDoor()) != null)
				movingDestTempPos.set(loc.x, loc.y, loc.z);
			else
			{
                sendActionFailed();
				return false;
			}

			isMoving = true;
            isKeyboardMoving = false;
			isFollow = true;
			_forestalling = forestalling;
			_offset = offset;
            _followCounter = 0;
			setFollowTarget(target);

			moveNext(true);

			return true;
		}
		finally
		{
			moveLock.unlock();
		}
	}

	public boolean moveToLocation(Location loc, int offset, boolean pathfinding)
	{
        return moveToLocation(loc.x, loc.y, loc.z, offset, pathfinding, true, false, -1);
	}

    public boolean moveToLocation(Location loc, int offset, boolean pathfinding, int maxDestRange)
    {
        return moveToLocation(loc.x, loc.y, loc.z, offset, pathfinding, true, false, maxDestRange);
    }

	public boolean moveToLocation(Location loc, int offset, boolean pathfinding, boolean cancelNextAction, boolean keyboard)
	{
		return moveToLocation(loc.x, loc.y, loc.z, offset, pathfinding, cancelNextAction, keyboard, -1);
	}

	public boolean moveToLocation(int x_dest, int y_dest, int z_dest, int offset, boolean pathfinding)
	{
        return moveToLocation(x_dest, y_dest, z_dest, offset, pathfinding, true, false, -1);
    }

    public boolean moveToLocation(int x_dest, int y_dest, int z_dest, int offset, boolean pathfinding, boolean cancelNextAction, boolean keyboard)
    {
        return moveToLocation(x_dest, y_dest, z_dest, offset, pathfinding, cancelNextAction, keyboard, -1);
    }

	public boolean moveToLocation(int x_dest, int y_dest, int z_dest, int offset, boolean pathfinding, boolean cancelNextAction, boolean keyboard, int maxDestRange)
	{
		moveLock.lock();
		try
		{
			offset = Math.max(offset, 0);
			Location dst_geoloc = new Location(x_dest, y_dest, z_dest).world2geo();
			if(isMoving && !isFollow && movingDestTempPos.equals(dst_geoloc))
			{
				sendActionFailed();
				return true;
			}

			if(isMovementDisabled())
			{
				getAI().setNextAction(AINextAction.MOVE, new Location(x_dest, y_dest, z_dest), offset, pathfinding && !keyboard, false);
				sendActionFailed();
				return false;
			}

			getAI().clearNextAction();

			if(isPlayer())
			{
				if(cancelNextAction)
					getAI().changeIntention(AI_INTENTION_ACTIVE, null, null);
			}

			stopMove(false, false);

			dst_geoloc = buildPathTo(x_dest, y_dest, z_dest, offset, pathfinding && !keyboard);
			if(dst_geoloc != null)
            {
                if(maxDestRange == -1)
                    movingDestTempPos.set(dst_geoloc);
                else
                {
                    Location dst_loc = dst_geoloc.geo2world();
                    if(!PositionUtils.checkIfInRange(maxDestRange + offset, x_dest, y_dest, z_dest, dst_loc.x, dst_loc.y, dst_loc.z, true))
                    {
                        sendActionFailed();
                        return false;
                    }
                    movingDestTempPos.set(dst_geoloc);
                }
            }
			else
			{
                sendActionFailed();
				return false;
			}

			isMoving = true;
            isKeyboardMoving = keyboard;

			moveNext(true);

			return true;
		}
		finally
		{
			moveLock.unlock();
		}
	}

	private void moveNext(boolean firstMove)
	{
		if(!isMoving || isMovementDisabled())
		{
			stopMove();
			return;
		}

		_previousSpeed = getMoveSpeed();
		if(_previousSpeed <= 0)
		{
			stopMove();
			return;
		}

        if(!firstMove)
        {
            Location dest = destination.clone();
            if(dest != null)
            {
                setLoc(dest, true);
                getListeners().onMove(dest); // TODO: Подходящее ли место?
            }
        }

		if(_targetRecorder.isEmpty())
		{
			CtrlEvent ctrlEvent = isFollow ? CtrlEvent.EVT_ARRIVED_TARGET : CtrlEvent.EVT_ARRIVED;
            stopMove(isKeyboardMoving, true);

			ThreadPoolManager.getInstance().execute(new NotifyAITask(this, ctrlEvent));
			return;
		}

		moveList = _targetRecorder.remove(0);
		Location begin = moveList.get(0).clone().geo2world();
		Location end = moveList.get(moveList.size() - 1).clone().geo2world();

        if(!isFlying() && !isInBoat() && !isInWater() && !isBoat() && !GeoEngine.canMoveToCoord(getX(), getY(), getZ(), end.x, end.y, end.z, getGeoIndex()))
        {
            stopMove();
            return;
        }

        destination = end;
		double distance = (isFlying() || isInWater()) ? begin.distance3D(end) : begin.distance(end); //клиент при передвижении не учитывает поверхность

		if(distance != 0)
			setHeading(PositionUtils.calculateHeadingFrom(begin.x, begin.y, destination.x, destination.y));

		broadcastMove();

		_startMoveTime = _followTimestamp = System.currentTimeMillis();
		if(_moveTaskRunnable == null)
			_moveTaskRunnable = new MoveNextTask();
		_moveTask = ThreadPoolManager.getInstance().schedule(_moveTaskRunnable.setDist(distance), getMoveTickInterval());
	}

    public void broadcastMove()
	{
		validateLocation(isPlayer() ? 2 : 1);
		broadcastPacket(movePacket());
	}

	public void broadcastStopMove()
	{
		broadcastPacket(stopMovePacket());
	}

	/**
	 * Останавливает движение и рассылает StopMove, ValidateLocation
	 */
	public void stopMove()
	{
		stopMove(true, true);
	}

	/**
	 * Останавливает движение и рассылает StopMove
	 * @param validate - рассылать ли ValidateLocation
	 */
	public void stopMove(boolean validate)
	{
		stopMove(true, validate);
	}

	/**
	 * Останавливает движение
	 *
	 * @param stop - рассылать ли StopMove
	 * @param validate - рассылать ли ValidateLocation
	 */
	public void stopMove(boolean stop, boolean validate)
	{
		if(!isMoving)
			return;

		moveLock.lock();
		try
		{
			if(!isMoving)
				return;

			isMoving = false;
            isKeyboardMoving = false;
			isFollow = false;
            isPathfindMoving = false;

			if(_moveTask != null)
			{
				_moveTask.cancel(false);
				_moveTask = null;
			}

			destination = null;
			moveList = null;

			_targetRecorder.clear();

			if(validate)
				validateLocation(isPlayer() ? 2 : 1);
			if(stop)
				broadcastStopMove();
			else
				sendActionFailed();
		}
		finally
		{
			moveLock.unlock();
		}
	}

	/** Возвращает координаты поверхности воды, если мы находимся в ней, или над ней. */
	public int getWaterZ()
	{
		if(!isInWater())
			return Integer.MIN_VALUE;

		int waterZ = Integer.MIN_VALUE;
		zonesRead.lock();
		try
		{
			Zone zone;
			for(int i = 0; i < _zones.size(); i++)
			{
				zone = _zones.get(i);
				if(zone.getType() == ZoneType.water)
					if(waterZ == Integer.MIN_VALUE || waterZ < zone.getTerritory().getZmax())
						waterZ = zone.getTerritory().getZmax();
			}
		}
		finally
		{
			zonesRead.unlock();
		}

		return waterZ;
	}

	protected L2GameServerPacket stopMovePacket()
	{
		return new StopMovePacket(this);
	}

	public L2GameServerPacket movePacket()
	{
        if(isFollow && !isPathfindMoving)
        {
            Creature target = getFollowTarget();
            if(target != null)
                return new MoveToPawnPacket(this, target, _offset);
        }
        return new MTLPacket(this);
	}

	public void updateZones()
	{
        Zone[] zones = isVisible() ? getCurrentRegion().getZones() : Zone.EMPTY_L2ZONE_ARRAY;

		List<Zone> entering = null;
		List<Zone> leaving = null;

		Zone zone;

		zonesWrite.lock();
		try
		{
			if(!_zones.isEmpty())
			{
				leaving = CollectionUtils.pooledList();
				for(int i = 0; i < _zones.size(); i++)
				{
					zone = _zones.get(i);
					// зоны больше нет в регионе, либо вышли за территорию зоны
					if(!ArrayUtils.contains(zones, zone) || !zone.checkIfInZone(getX(), getY(), getZ(), getReflection()))
						leaving.add(zone);
				}

				//Покинули зоны, убираем из списка зон персонажа
				if(!leaving.isEmpty())
				{
					for(int i = 0; i < leaving.size(); i++)
					{
						zone = leaving.get(i);
						_zones.remove(zone);
					}
				}
			}

			if(zones.length > 0)
			{
				entering = CollectionUtils.pooledList();
				for(int i = 0; i < zones.length; i++)
				{
					zone = zones[i];
					// в зону еще не заходили и зашли на территорию зоны
					if(!_zones.contains(zone) && zone.checkIfInZone(getX(), getY(), getZ(), getReflection()))
						entering.add(zone);
				}

				//Вошли в зоны, добавим в список зон персонажа
				if(!entering.isEmpty())
				{
					for(int i = 0; i < entering.size(); i++)
					{
						zone = entering.get(i);
						_zones.add(zone);
					}
				}
			}
		}
		finally
		{
			zonesWrite.unlock();
		}

		onUpdateZones(leaving, entering);

		if(leaving != null)
			CollectionUtils.recycle(leaving);

		if(entering != null)
			CollectionUtils.recycle(entering);

	}

	protected void onUpdateZones(List<Zone> leaving, List<Zone> entering)
	{
		Zone zone;

		if(leaving != null && !leaving.isEmpty())
		{
			for(int i = 0; i < leaving.size(); i++)
			{
				zone = leaving.get(i);
				zone.doLeave(this);
			}
		}

		if(entering != null && !entering.isEmpty())
		{
			for(int i = 0; i < entering.size(); i++)
			{
				zone = entering.get(i);
				zone.doEnter(this);
			}
		}
	}

	public boolean isInPeaceZone()
	{
		return isInZone(ZoneType.peace_zone) && !isInZoneBattle();
	}

	public boolean isInZoneBattle()
	{
        for(Event event : getEvents())
            if(event.isInZoneBattle(this))
                return true;

        return isInZone(ZoneType.battle_zone);
	}

	public boolean isInWater()
	{
		return isInZone(ZoneType.water) && !(isInBoat() || isBoat() || isFlying());
	}

    public boolean isInSiegeZone()
    {
        return isInZone(ZoneType.SIEGE);
    }

    public boolean isInSSQZone()
    {
        return isInZone(ZoneType.ssq_zone);
    }

    public boolean isInDangerArea()
    {
        zonesRead.lock();
        try
        {
            Zone zone;
            for(int i = 0; i < _zones.size(); ++i)
            {
                zone = _zones.get(i);
                if(zone.getTemplate().isShowDangerzone())
                    return true;
            }
        }
        finally
        {
            zonesRead.unlock();
        }
        return false;
    }

	public boolean isInZone(ZoneType type)
	{
		zonesRead.lock();
		try
		{
			Zone zone;
			for(int i = 0; i < _zones.size(); i++)
			{
				zone = _zones.get(i);
				if(zone.getType() == type)
					return true;
			}
		}
		finally
		{
			zonesRead.unlock();
		}

		return false;
	}

    public List<Event> getZoneEvents()
    {
        List<Event> e = Collections.emptyList();
        zonesRead.lock();
        try
        {
            Zone zone;
            for(int i = 0; i < _zones.size(); i++)
            {
                zone = _zones.get(i);
                if(!zone.getEvents().isEmpty())
                {
                    if(e.isEmpty())
                        e = new ArrayList<Event>(2);

                    e.addAll(zone.getEvents());
                }
            }
        }
        finally
        {
            zonesRead.unlock();
        }

        return e;
    }

	public boolean isInZone(String name)
	{
		zonesRead.lock();
		try
		{
			Zone zone;
			for(int i = 0; i < _zones.size(); i++)
			{
				zone = _zones.get(i);
				if(zone.getName().equals(name))
					return true;
			}
		}
		finally
		{
			zonesRead.unlock();
		}

		return false;
	}

	public boolean isInZone(Zone zone)
	{
		zonesRead.lock();
		try
		{
			return _zones.contains(zone);
		}
		finally
		{
			zonesRead.unlock();
		}
	}

	public Zone getZone(ZoneType type)
	{
		zonesRead.lock();
		try
		{
			Zone zone;
			for(int i = 0; i < _zones.size(); i++)
			{
				zone = _zones.get(i);
				if(zone.getType() == type)
					return zone;
			}
		}
		finally
		{
			zonesRead.unlock();
		}
		return null;
	}

	public Location getRestartPoint()
	{
		zonesRead.lock();
		try
		{
			Zone zone;
			for(int i = 0; i < _zones.size(); i++)
			{
				zone = _zones.get(i);
				if(zone.getRestartPoints() != null)
				{
					ZoneType type = zone.getType();
					if(type == ZoneType.battle_zone || type == ZoneType.peace_zone || type == ZoneType.offshore || type == ZoneType.dummy)
						return zone.getSpawn();
				}
			}
		}
		finally
		{
			zonesRead.unlock();
		}

		return null;
	}

	public Location getPKRestartPoint()
	{
		zonesRead.lock();
		try
		{
			Zone zone;
			for(int i = 0; i < _zones.size(); i++)
			{
				zone = _zones.get(i);
				if(zone.getRestartPoints() != null)
				{
					ZoneType type = zone.getType();
					if(type == ZoneType.battle_zone || type == ZoneType.peace_zone || type == ZoneType.offshore || type == ZoneType.dummy)
						return zone.getPKSpawn();
				}
			}
		}
		finally
		{
			zonesRead.unlock();
		}

		return null;
	}

	@Override
	public int getGeoZ(Location loc)
	{
		if(isFlying() || isInWater() || isInBoat() || isBoat() || isDoor())
			return loc.z;

		return super.getGeoZ(loc);
	}

	protected boolean needStatusUpdate()
	{
		if(!isVisible())
			return false;

		boolean result = false;

		int bar;
		bar = (int) (getCurrentHp() * CLIENT_BAR_SIZE / getMaxHp());
		if(bar == 0 || bar != _lastHpBarUpdate)
		{
			_lastHpBarUpdate = bar;
			result = true;
		}

		bar = (int) (getCurrentMp() * CLIENT_BAR_SIZE / getMaxMp());
		if(bar == 0 || bar != _lastMpBarUpdate)
		{
			_lastMpBarUpdate = bar;
			result = true;
		}

		if(isPlayer())
		{
			bar = (int) (getCurrentCp() * CLIENT_BAR_SIZE / getMaxCp());
			if(bar == 0 || bar != _lastCpBarUpdate)
			{
				_lastCpBarUpdate = bar;
				result = true;
			}
		}

		return result;
	}

	public void onHitTimer(Creature target, int damage, boolean crit, boolean miss, boolean soulshot, boolean shld, boolean unchargeSS)
	{
		if(isAlikeDead())
		{
			sendActionFailed();
			return;
		}

		if(target.isDead() || !isInRange(target, 2000))
		{
			sendActionFailed();
			return;
		}

		if(isPlayable() && target.isPlayable() && isInZoneBattle() != target.isInZoneBattle())
		{
			Player player = getPlayer();
			if(player != null)
			{
				player.sendPacket(SystemMsg.INVALID_TARGET);
				player.sendActionFailed();
			}
			return;
		}

		target.getListeners().onAttackHit(this);

		// if hitted by a cursed weapon, Cp is reduced to 0, if a cursed weapon is hitted by a Hero, Cp is reduced to 0
        ThreadPoolManager.getInstance().execute(new NotifyAITask(this, CtrlEvent.EVT_ATTACK, target, null, damage));

		ThreadPoolManager.getInstance().execute(new NotifyAITask(target, CtrlEvent.EVT_ATTACKED, this, null, damage));

		boolean checkPvP = checkPvP(target, null);

		// Reduce HP of the target and calculate reflection damage to reduce HP of attacker if necessary
		target.reduceCurrentHp(damage, this, null, true, true, false, true, false, false, true, true, crit, miss, shld);

		if(!miss && damage > 0)
		{
			// Скиллы, кастуемые при физ атаке
			if(!target.isDead())
			{
				if(crit)
					useTriggers(target, TriggerType.CRIT, null, null, damage);

				useTriggers(target, TriggerType.ATTACK, null, null, damage);

                if(Formulas.calcStunBreak(crit, false, false))
                    target.getAbnormalList().stop(AbnormalType.stun);

				// Manage attack or cast break of the target (calculating rate, sending message...)
				if(Formulas.calcCastBreak(target, crit))
					target.abortCast(false, true);
			}

			if(soulshot && unchargeSS)
				unChargeShots(false);
		}

		if(miss)
			target.useTriggers(this, TriggerType.UNDER_MISSED_ATTACK, null, null, damage);

		startAttackStanceTask();

		if(checkPvP)
			startPvPFlag(target);
	}

    public void onMagicUseTimer(Creature aimingTarget, Skill skill, boolean forceUse)
	{
        if(skill == null)
        {
            broadcastPacket(new MagicSkillCanceled(getObjectId()));
            onCastEndTime(aimingTarget, null, false);
            return;
        }

		Location flyLoc;

		switch(skill.getFlyType())
        {
            case CHARGE:
            case WARP_BACK:
            case WARP_FORWARD:
            {
                flyLoc = _flyLoc;
                if(flyLoc == null)
                    break;

                setLoc(flyLoc);
                if(skill.getTickInterval() > 0)
                    _log.warn("Skill ID[" + skill.getId() + "] LEVEL[" + skill.getLevel() + "] have fly effect and tick effects. Rework please fly algoritm!");

                break;
            }
        }

        _castInterruptTime = 0;

		if(!skill.isOffensive() && getAggressionTarget() != null)
			forceUse = true;

		if(!skill.checkCondition(this, aimingTarget, forceUse, false, false))
		{
			if(skill.getSkillType() == SkillType.PET_SUMMON && isPlayer())
				getPlayer().setPetControlItem(null);
            broadcastPacket(new MagicSkillCanceled(getObjectId()));
            onCastEndTime(aimingTarget, null, false);
			return;
		}

		if(skill.getCastRange() != -1 && skill.getSkillType() != SkillType.TAKECASTLE && !GeoEngine.canSeeTarget(this, aimingTarget, isFlying()))
		{
			sendPacket(SystemMsg.CANNOT_SEE_TARGET);
			broadcastPacket(new MagicSkillCanceled(getObjectId()));
			onCastEndTime(aimingTarget, null, false);
			return;
		}

		List<Creature> targets = skill.getTargets(this, aimingTarget, forceUse);

		if(skill.getTickInterval() > 0)
		{
            _castInterval = _castInterval - skill.getTickInterval();
            if(_castInterval >= 0)
            {
                double mpConsumeTick = skill.getMpConsumeTick();
                if(mpConsumeTick > 0)
                {
                    if(skill.isMusic())
                    {
                        double inc = mpConsumeTick / 2;
                        double add = 0;
                        for(Abnormal e : getAbnormalList())
                        {
                            if(e.getSkill().getId() != skill.getId() && e.getSkill().isMusic() && e.getTimeLeft() > 30)
                                add += inc;
                        }
                        mpConsumeTick += add;
                        mpConsumeTick = calcStat(Stats.MP_DANCE_SKILL_CONSUME, mpConsumeTick, aimingTarget, skill);
                    }
                    else if(skill.isMagic())
                        mpConsumeTick = calcStat(Stats.MP_MAGIC_SKILL_CONSUME, mpConsumeTick, aimingTarget, skill);
                    else
                        mpConsumeTick = calcStat(Stats.MP_PHYSICAL_SKILL_CONSUME, mpConsumeTick, aimingTarget, skill);

                    if(_currentMp < mpConsumeTick && isPlayable())
                    {
                        sendPacket(SystemMsg.NOT_ENOUGH_MP);
                        broadcastPacket(new MagicSkillCanceled(getObjectId()));
                        onCastEndTime(aimingTarget, null, false);
                        return;
                    }
                    reduceCurrentMp(mpConsumeTick, null);
                }
                skill.onTickCast(this, targets);
                useTriggers(aimingTarget, TriggerType.ON_TICK_CAST, null, skill, 0);
            }

            if(_castInterval > 0)
            {
                final int delay = Math.min(_castInterval, skill.getTickInterval());
                _skillLaunchedTask = ThreadPoolManager.getInstance().schedule(new MagicLaunchedTask(this, forceUse), delay);
                _skillTask = ThreadPoolManager.getInstance().schedule(new MagicUseTask(this, forceUse), delay);
                return;
            }
		}

		//must be player for usage with a clan.
		int clanRepConsume = skill.getClanRepConsume();
		if(clanRepConsume > 0)
			getPlayer().getClan().incReputation(-clanRepConsume, false, "clan skills");

		int fameConsume = skill.getFameConsume();
		if(fameConsume > 0)
			getPlayer().setFame(getPlayer().getFame() - fameConsume, "clan skills", true);

		int hpConsume = skill.getHpConsume();
		if(hpConsume > 0)
			setCurrentHp(Math.max(0, _currentHp - hpConsume), false);

		double mpConsume2 = skill.getMpConsume2();
		if(mpConsume2 > 0)
		{
			if(skill.isMusic())
			{
				double inc = mpConsume2 / 2;
				double add = 0;
				for(Abnormal e : getAbnormalList())
				{
					if(e.getSkill().getId() != skill.getId() && e.getSkill().isMusic() && e.getTimeLeft() > 30)
						add += inc;
				}
				mpConsume2 += add;
				mpConsume2 = calcStat(Stats.MP_DANCE_SKILL_CONSUME, mpConsume2, aimingTarget, skill);
			}
			else if(skill.isMagic())
				mpConsume2 = calcStat(Stats.MP_MAGIC_SKILL_CONSUME, mpConsume2, aimingTarget, skill);
			else
				mpConsume2 = calcStat(Stats.MP_PHYSICAL_SKILL_CONSUME, mpConsume2, aimingTarget, skill);

			if(_currentMp < mpConsume2 && isPlayable())
			{
				sendPacket(SystemMsg.NOT_ENOUGH_MP);
                broadcastPacket(new MagicSkillCanceled(getObjectId()));
                onCastEndTime(aimingTarget, null, false);
				return;
			}
			reduceCurrentMp(mpConsume2, null);
		}

		callSkill(skill, targets, true, false);

		if(skill.getNumCharges() > 0)
			setIncreasedForce(getIncreasedForce() - skill.getNumCharges());

		if(skill.getCondCharges() > 0 && getIncreasedForce() > 0)
		{
			int decreasedForce = skill.getCondCharges();
			if(decreasedForce > 15)
				decreasedForce = 5;
			setIncreasedForce(getIncreasedForce() - decreasedForce);
		}

		if(skill.isSoulBoost())
			setConsumedSouls(getConsumedSouls() - Math.min(getConsumedSouls(), 5), null);
		else if(skill.getSoulsConsume() > 0)
			setConsumedSouls(getConsumedSouls() - skill.getSoulsConsume(), null);

		switch(skill.getFlyType())
		{
			// @Rivelia. Targets fly types.
			case THROW_UP:
			case THROW_HORIZONTAL:
			case PUSH_HORIZONTAL:
			case PUSH_DOWN_HORIZONTAL:
				for(Creature target : targets)
				{
					flyLoc = target.getFlyLocation(this, skill);
					if(flyLoc == null)
						_log.warn(skill.getFlyType() + " have null flyLoc.");
					else
					{
						target.broadcastPacket(new FlyToLocationPacket(target, flyLoc, skill.getFlyType(), skill.getFlySpeed(), skill.getFlyDelay(), skill.getFlyAnimationSpeed()));
						target.setLoc(flyLoc);
					}
				}
				break;
			// @Rivelia. Caster fly types.
			case DUMMY:
				Creature dummyTarget = aimingTarget;
				if(skill.getTargetType() == SkillTargetType.TARGET_AURA)
					dummyTarget = this;

				flyLoc = getFlyLocation(dummyTarget, skill);
				if(flyLoc != null)
				{
					broadcastPacket(new FlyToLocationPacket(this, flyLoc, skill.getFlyType(), skill.getFlySpeed(), skill.getFlyDelay(), skill.getFlyAnimationSpeed()));
					setLoc(flyLoc);
				}
				/*else
					sendPacket(SystemMsg.CANNOT_SEE_TARGET);*/
				break;
		}

		// @Rivelia.
		int skillCoolTime = 0;
		int chargeAddition = 0;

		// @Rivelia. Add the fly speed in the skill cooltime to make the travelling end before the creature can take action again.
		if(skill.getFlyType() == FlyType.CHARGE && skill.getFlySpeed() > 0)
			chargeAddition = (int)((getDistance(aimingTarget) / skill.getFlySpeed()) * 1000);
		
		if(!skill.isSkillTimePermanent())
			skillCoolTime = Formulas.calcSkillCastSpd(this, skill, skill.getCoolTime() + chargeAddition);
		else
			skillCoolTime = skill.getCoolTime() + chargeAddition;

		if(skillCoolTime > 0)
			ThreadPoolManager.getInstance().schedule(new CastEndTimeTask(this, aimingTarget, targets), skillCoolTime);
		else
			onCastEndTime(aimingTarget, targets, true);
	}

    public void onCastEndTime(Creature aimingTarget, List<Creature> targets, boolean success)
	{
        final Skill castingSkill = getCastingSkill();
        final Creature castingTarget = getCastingTarget();

		clearCastVars();

		if(castingSkill != null)
		{
			getAI().notifyEvent(CtrlEvent.EVT_FINISH_CASTING, castingSkill, castingTarget, success);

			if(success)
			{
				castingSkill.onFinishCast(aimingTarget,this, targets);
				useTriggers(castingTarget, TriggerType.ON_FINISH_CAST, null, castingSkill, 0);

                if(isPlayer())
                {
                    for(ListenerHook hook : getPlayer().getListenerHooks(ListenerHookType.PLAYER_FINISH_CAST_SKILL))
                        hook.onPlayerFinishCastSkill(getPlayer(), castingSkill.getId());

                    for(ListenerHook hook : ListenerHook.getGlobalListenerHooks(ListenerHookType.PLAYER_FINISH_CAST_SKILL))
                        hook.onPlayerFinishCastSkill(getPlayer(), castingSkill.getId());
                }
            }
		}
	}

	public void clearCastVars()
	{
        _castInterval = 0;
        _animationEndTime = 0;
        _castInterruptTime = 0;
        _castingSkill = null;
        _isCriticalBlowCastingSkill = false;
        _skillTask = null;
        _skillLaunchedTask = null;
        _flyLoc = null;
	}

	public final int getCastInterval()
	{
		return _castInterval;
	}

	public final void reduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflectAndAbsorb, boolean transferDamage, boolean isDot, boolean sendReceiveMessage)
	{
		reduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, canReflectAndAbsorb, transferDamage, isDot, sendReceiveMessage, false, false, false, false);
	}

	public void reduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflectAndAbsorb, boolean transferDamage, boolean isDot, boolean sendReceiveMessage, boolean sendGiveMessage, boolean crit, boolean miss, boolean shld)
	{
		if(isImmortal())
			return;

		boolean damaged = true;
		if(miss || damage <= 0)
			damaged = false;

		final boolean damageBlocked = isDamageBlocked(attacker);
        if(attacker == null || isDead() || (attacker.isDead() && !isDot) || damageBlocked)
            damaged = false;

		if(!damaged)
		{
			if(attacker != this)
			{
				if(sendGiveMessage)
					attacker.displayGiveDamageMessage(this, skill, 0, null, 0, crit, miss, shld, damageBlocked);
			}
			return;
		}

		double reflectedDamage = 0.;
		double transferedDamage = 0.;
		Servitor servitorForTransfereDamage = null;

        if(canReflectAndAbsorb)
        {
		    boolean canAbsorb = canAbsorb(this, attacker);

			if(canAbsorb)
                damage = absorbToEffector(attacker, damage);

            damage = reduceDamageByMp(attacker, damage);

            // e.g. Transfer Pain
            transferedDamage = getDamageForTransferToServitor(damage);
            servitorForTransfereDamage = getServitorForTransfereDamage(transferedDamage);
            if(servitorForTransfereDamage != null)
                damage -= transferedDamage;
            else
                transferedDamage = 0.;

            reflectedDamage = reflectDamage(attacker, skill, damage);
            if(canAbsorb)
                attacker.absorbDamage(this, skill, damage);
        }

		// Damage can be limited by ultimate effects
		double damageLimit = -1;
		if(skill == null)
			damageLimit = calcStat(Stats.RECIEVE_DAMAGE_LIMIT, damage);
		else if(skill.isMagic())
			damageLimit = calcStat(Stats.RECIEVE_DAMAGE_LIMIT_M_SKILL, damage);
		else
			damageLimit = calcStat(Stats.RECIEVE_DAMAGE_LIMIT_P_SKILL, damage);

		if(damageLimit >= 0. && damage > damageLimit)
			damage = damageLimit;

		getListeners().onCurrentHpDamage(damage, attacker, skill);

		if(attacker != this)
		{
			if(sendGiveMessage)
				attacker.displayGiveDamageMessage(this, skill, (int) damage, servitorForTransfereDamage, (int) transferedDamage, crit, miss, shld, damageBlocked);

			if(sendReceiveMessage)
				displayReceiveDamageMessage(attacker, (int) damage);

			if(!isDot)
				useTriggers(attacker, TriggerType.RECEIVE_DAMAGE, null, null, damage);
		}

		if(servitorForTransfereDamage != null && transferedDamage > 0)
			servitorForTransfereDamage.reduceCurrentHp(transferedDamage, attacker, null, false, false, false, false, true, false, true);

		onReduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, isDot);

		if(reflectedDamage > 0.)
		{
			displayGiveDamageMessage(attacker, skill, (int) reflectedDamage, null, 0, false, false, false, false);
			attacker.reduceCurrentHp(reflectedDamage, this, null, true, true, false, false, false, false, true);
		}
	}

	protected void onReduceCurrentHp(final double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean isDot)
	{
		if(awake && isSleeping())
            getAbnormalList().stop(AbnormalType.sleep);

		if(attacker != this || (skill != null && skill.isOffensive()))
		{
			final TIntSet effectsToRemove = new TIntHashSet();
			for(Abnormal effect : getAbnormalList())
			{
				if(effect.getSkill().isDispelOnDamage())
					effectsToRemove.add(effect.getSkill().getId());
			}
            getAbnormalList().stop(effectsToRemove);

			if(isMeditated())
                getAbnormalList().stop(EffectType.Meditation);

			startAttackStanceTask();
			checkAndRemoveInvisible();
		}

		if(damage <= 0)
			return;

		// GM undying mode
        if(getCurrentHp() - damage < 10 && calcStat(Stats.ShillienProtection) == 1)
        {
            setCurrentHp(getMaxHp(), false, true);
            setCurrentCp(getMaxCp());
            return;
        }

		boolean isUndying = isUndying();
        setCurrentHp(Math.max(getCurrentHp() - damage, isDot ? 1 : (isUndying ? 0.5 : 0)), false);
        if(isUndying)
        {
            if(getCurrentHp() == 0.5 && (!isPlayer() || !getPlayer().isGMUndying()) && getFlags().getUndying().getFlag().compareAndSet(false, true))
                getListeners().onDeathFromUndying(attacker);
        }
        else if(getCurrentHp() < 0.5)
		{
			if(attacker != this || (skill != null && skill.isOffensive()))
				useTriggers(attacker, TriggerType.DIE, null, null, damage);

			doDie(attacker);
		}
	}

	public void reduceCurrentMp(double i, Creature attacker)
	{
		if(attacker != null && attacker != this)
		{
			if(isSleeping())
                getAbnormalList().stop(AbnormalType.sleep);

			if(isMeditated())
                getAbnormalList().stop(EffectType.Meditation);
		}

		if(isDamageBlocked(attacker) && attacker != null && attacker != this)
		{
			attacker.sendPacket(SystemMsg.THE_ATTACK_HAS_BEEN_BLOCKED);
			return;
		}

		// 5182 = Blessing of protection, работает если разница уровней больше 10 и не в зоне осады
		if(attacker != null && attacker.isPlayer() && Math.abs(attacker.getLevel() - getLevel()) > 10)
		{
			// ПК не может нанести урон чару с блессингом
			if(attacker.isPK() && getAbnormalList().contains(5182) && !isInSiegeZone())
				return;
			// чар с блессингом не может нанести урон ПК
			if(isPK() && attacker.getAbnormalList().contains(5182) && !attacker.isInSiegeZone())
				return;
		}

		i = _currentMp - i;

		if(i < 0)
			i = 0;

		setCurrentMp(i);

		if(attacker != null && attacker != this)
			startAttackStanceTask();
	}

	public void removeAllSkills()
	{
		for(SkillEntry s : getAllSkillsArray())
			removeSkill(s);
	}

	public SkillEntry removeSkill(SkillEntry skillEntry)
	{
		if(skillEntry == null)
			return null;
		return removeSkillById(skillEntry.getId());
	}

	public SkillEntry removeSkillById(int id)
	{
		// Remove the skill from the L2Character _skills
        SkillEntry oldSkillEntry = _skills.remove(id);

		// Remove all its Func objects from the L2Character calculator set
		if(oldSkillEntry != null)
		{
            Skill oldSkill = oldSkillEntry.getTemplate();
			if(oldSkill.isToggle())
                getAbnormalList().stop(oldSkill);

			removeTriggers(oldSkill);
			removeStatsOwner(oldSkill);
			if(Config.ALT_DELETE_SA_BUFFS && (oldSkill.isItemSkill() || oldSkill.isHandler()))
			{
				// Завершаем все эффекты, принадлежащие старому скиллу
                getAbnormalList().stop(oldSkill);

				// И с петов тоже
                for(Servitor servitor : getServitors())
                    servitor.getAbnormalList().stop(oldSkill);
			}

			AINextAction nextAction = getAI().getNextAction();
			if(nextAction != null && nextAction == AINextAction.CAST)
			{
				Object args1 = getAI().getNextActionArgs()[0];
				if(args1 == oldSkill)
					getAI().clearNextAction();
			}

            onRemoveSkill(oldSkillEntry);
		}

		return oldSkillEntry;
	}

	public void addTriggers(StatTemplate f)
	{
		if(f.getTriggerList().isEmpty())
			return;

		for(TriggerInfo t : f.getTriggerList())
		{
			addTrigger(t);
		}
	}

	public void addTrigger(TriggerInfo t)
	{
		if(_triggers == null)
			_triggers = new ConcurrentHashMap<TriggerType, Set<TriggerInfo>>();

		Set<TriggerInfo> hs = _triggers.get(t.getType());
		if(hs == null)
		{
			hs = new CopyOnWriteArraySet<>();
			_triggers.put(t.getType(), hs);
		}

		hs.add(t);

		if(t.getType() == TriggerType.ADD)
			useTriggerSkill(this, null, t, null, 0);
		else if(t.getType() == TriggerType.IDLE)
			new RunnableTrigger(this, t).schedule();
	}

	public Map<TriggerType, Set<TriggerInfo>> getTriggers()
	{
		return _triggers;
	}

	public void removeTriggers(StatTemplate f)
	{
		if(_triggers == null || f.getTriggerList().isEmpty())
			return;

		for(TriggerInfo t : f.getTriggerList())
			removeTrigger(t);
	}

	public void removeTrigger(TriggerInfo t)
	{
		if(_triggers == null)
			return;
		Set<TriggerInfo> hs = _triggers.get(t.getType());
		if(hs == null)
			return;
		hs.remove(t);

		if(t.cancelEffectsOnRemove())
			triggerCancelEffects(t);
	}

	public void sendActionFailed()
	{
		sendPacket(ActionFailPacket.STATIC);
	}

	public boolean hasAI()
	{
		return _ai != null;
	}

	public CharacterAI getAI()
	{
		if(_ai == null)
			synchronized (this)
			{
				if(_ai == null)
					_ai = new CharacterAI(this);
			}

		return _ai;
	}

	public void setAI(CharacterAI newAI)
	{
		if(newAI == null)
			return;

		CharacterAI oldAI = _ai;

		synchronized (this)
		{
			_ai = newAI;
		}

		if(oldAI != null)
		{
			if(oldAI.isActive())
			{
				oldAI.stopAITask();
				newAI.startAITask();
				newAI.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			}
		}
	}

	public final void setCurrentHp(double newHp, boolean canResurrect, boolean sendInfo)
	{
		int maxHp = getMaxHp();

		newHp = Math.min(maxHp, Math.max(0, newHp));

		if(isDeathImmune())
			newHp = Math.max(1.1, newHp); // Ставим 1.1, потому что на олимпиаде 1 == Поражение, что вызовет зависание.

		if(_currentHp == newHp)
			return;

		if(newHp >= 0.5 && isDead() && !canResurrect)
			return;

		double hpStart = _currentHp;

		_currentHp = newHp;

		if(isDead.compareAndSet(true, false))
			onRevive();

		checkHpMessages(hpStart, _currentHp);

		if(sendInfo)
		{
			broadcastStatusUpdate();
			sendChanges();
		}

		if(_currentHp < maxHp)
			startRegeneration();

        onChangeCurrentHp(hpStart, newHp);
        getListeners().onChangeCurrentHp(hpStart, newHp);
    }

	public final void setCurrentHp(double newHp, boolean canResurrect)
	{
		setCurrentHp(newHp, canResurrect, true);
	}

    public void onChangeCurrentHp(double oldHp, double newHp)
    {
    	//
    }

	public final void setCurrentMp(double newMp, boolean sendInfo)
	{
		int maxMp = getMaxMp();

		newMp = Math.min(maxMp, Math.max(0, newMp));

		if(_currentMp == newMp)
			return;

		if(newMp >= 0.5 && isDead())
			return;

        double mpStart = _currentMp;

		_currentMp = newMp;

		if(sendInfo)
		{
			broadcastStatusUpdate();
			sendChanges();
		}

		if(_currentMp < maxMp)
			startRegeneration();

        getListeners().onChangeCurrentMp(mpStart, newMp);
	}

	public final void setCurrentMp(double newMp)
	{
		setCurrentMp(newMp, true);
	}

	public final void setCurrentCp(double newCp, boolean sendInfo)
	{
		if(!isPlayer())
			return;

		int maxCp = getMaxCp();
		newCp = Math.min(maxCp, Math.max(0, newCp));

		if(_currentCp == newCp)
			return;

		if(newCp >= 0.5 && isDead())
			return;

        double cpStart = _currentCp;

		_currentCp = newCp;

		if(sendInfo)
		{
			broadcastStatusUpdate();
			sendChanges();
		}

		if(_currentCp < maxCp)
			startRegeneration();

        getListeners().onChangeCurrentCp(cpStart, newCp);
	}

	public final void setCurrentCp(double newCp)
	{
		setCurrentCp(newCp, true);
	}

	public void setCurrentHpMp(double newHp, double newMp, boolean canResurrect)
	{
		int maxHp = getMaxHp();
		int maxMp = getMaxMp();

		newHp = Math.min(maxHp, Math.max(0, newHp));
		newMp = Math.min(maxMp, Math.max(0, newMp));

		if(isDeathImmune())
			newHp = Math.max(1.1, newHp); // Ставим 1.1, потому что на олимпиаде 1 == Поражение, что вызовет зависание.

		if(_currentHp == newHp && _currentMp == newMp)
			return;

		if(newHp >= 0.5 && isDead() && !canResurrect)
			return;

		double hpStart = _currentHp;
        double mpStart = _currentMp;

		_currentHp = newHp;
		_currentMp = newMp;

		if(isDead.compareAndSet(true, false))
			onRevive();

		checkHpMessages(hpStart, _currentHp);

		broadcastStatusUpdate();
		sendChanges();

		if(_currentHp < maxHp || _currentMp < maxMp)
			startRegeneration();

        getListeners().onChangeCurrentHp(hpStart, newHp);
        getListeners().onChangeCurrentMp(mpStart, newMp);
    }

	public void setCurrentHpMp(double newHp, double newMp)
	{
		setCurrentHpMp(newHp, newMp, false);
	}

	public final void setFlying(boolean mode)
	{
		_flying = mode;
	}

	@Override
	public final int getHeading()
	{
		return _heading;
	}

	public final void setHeading(int heading)
	{
		setHeading(heading, false);
	}

	public final void setHeading(int heading, boolean broadcast)
	{
		_heading = heading;
		if(broadcast)
			broadcastPacket(new ExRotation(getObjectId(), heading));
	}

	public final void setIsTeleporting(boolean value)
	{
		isTeleporting.compareAndSet(!value, value);
	}

	public final void setName(String name)
	{
		_name = name;
	}

	public Creature getCastingTarget()
	{
		return _castingTarget.get();
	}

	public void setCastingTarget(Creature target)
	{
		if(target == null)
			_castingTarget = HardReferences.emptyRef();
		else
			_castingTarget = target.getRef();
	}

	public final void setRunning()
	{
		if(!_running)
		{
			_running = true;
            broadcastPacket(changeMovePacket());
		}
	}

	public void setAggressionTarget(Creature target)
	{
		if(target == null)
			_aggressionTarget = HardReferences.emptyRef();
		else
			_aggressionTarget = target.getRef();
	}

	public Creature getAggressionTarget()
	{
		return _aggressionTarget.get();
	}

	public void setTarget(GameObject object)
	{
		if(object != null && !object.isVisible())
			object = null;

		/* DS: на оффе сброс текущей цели не отменяет атаку или каст.
		if(object == null)
		{
			if(isAttackingNow() && getAI().getAttackTarget() == getTarget())
				abortAttack(false, true);
			if(isCastingNow() && getAI().getCastTarget() == getTarget())
				abortCast(false, true);
		}
		*/

		if(object == null)
			target = HardReferences.emptyRef();
		else
			target = object.getRef();
	}

	public void setTitle(String title)
	{
		_title = title;
	}

	public void setWalking()
	{
		if(_running)
		{
			_running = false;
            broadcastPacket(changeMovePacket());
		}
	}

    protected L2GameServerPacket changeMovePacket()
    {
        return new ChangeMoveTypePacket(this);
    }

	public final void startAbnormalEffect(AbnormalEffect ae)
	{
		if(ae == AbnormalEffect.NONE)
			_abnormalEffects.clear();
		else
			_abnormalEffects.add(ae);
		sendChanges();
	}

	public void startAttackStanceTask()
	{
		startAttackStanceTask0();
	}

	/**
	 * Запускаем задачу анимации боевой позы. Если задача уже запущена, увеличиваем время, которое персонаж будет в боевой позе на 15с
	 */
	protected void startAttackStanceTask0()
	{
		// предыдущая задача еще не закончена, увеличиваем время
		if(isInCombat())
		{
			_stanceEndTime = System.currentTimeMillis() + 15000L;
			return;
		}

		_stanceEndTime = System.currentTimeMillis() + 15000L;

		broadcastPacket(new AutoAttackStartPacket(getObjectId()));

		// отменяем предыдущую
		final Future<?> task = _stanceTask;
		if(task != null)
			task.cancel(false);

		// Добавляем задачу, которая будет проверять, если истекло время нахождения персонажа в боевой позе,
		// отменяет задачу и останаливает анимацию.
		_stanceTask = LazyPrecisionTaskManager.getInstance().scheduleAtFixedRate(_stanceTaskRunnable == null ? _stanceTaskRunnable = new AttackStanceTask() : _stanceTaskRunnable, 1000L, 1000L);
	}

	/**
	 * Останавливаем задачу анимации боевой позы.
	 */
	public void stopAttackStanceTask()
	{
		_stanceEndTime = 0L;

		final Future<?> task = _stanceTask;
		if(task != null)
		{
			task.cancel(false);
			_stanceTask = null;

			broadcastPacket(new AutoAttackStopPacket(getObjectId()));
		}
	}

	private class AttackStanceTask extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			if(!isInCombat())
				stopAttackStanceTask();
		}
	}

	/**
	 * Остановить регенерацию
	 */
	protected void stopRegeneration()
	{
		regenLock.lock();
		try
		{
			if(_isRegenerating)
			{
				_isRegenerating = false;

				if(_regenTask != null)
				{
					_regenTask.cancel(false);
					_regenTask = null;
				}
			}
		}
		finally
		{
			regenLock.unlock();
		}
	}

	/**
	 * Запустить регенерацию
	 */
	protected void startRegeneration()
	{
		if(!isVisible() || isDead() || getRegenTick() == 0L)
			return;

		if(_isRegenerating)
			return;

		regenLock.lock();
		try
		{
			if(!_isRegenerating)
			{
				_isRegenerating = true;
				_regenTask = RegenTaskManager.getInstance().scheduleAtFixedRate(_regenTaskRunnable == null ? _regenTaskRunnable = new RegenTask() : _regenTaskRunnable, getRegenTick(), getRegenTick());
			}
		}
		finally
		{
			regenLock.unlock();
		}
	}

	public long getRegenTick()
	{
		return 3000L;
	}

	private class RegenTask implements Runnable
	{
		@Override
		public void run()
		{
			if(isAlikeDead() || getRegenTick() == 0L)
				return;

			double hpStart = _currentHp;
            double mpStart = _currentMp;
            double cpStart = _currentCp;

            int maxHp = getMaxHp();
			int maxMp = getMaxMp();
			int maxCp = isPlayer() ? getMaxCp() : 0;

			double addHp = 0.;
			double addMp = 0.;
            double addCp = 0.;

			regenLock.lock();
			try
			{
				if(_currentHp < maxHp)
					addHp += getHpRegen();

				if(_currentMp < maxMp)
					addMp += getMpRegen();

                if(_currentCp < maxCp)
                    addCp += getCpRegen();

                if(isSitting())
                {
                    // Added regen bonus when character is sitting
                    if(isPlayer() && Config.REGEN_SIT_WAIT)
                    {
                        /*TODO:
                        //HP회복속도
                        org_hp_regen_weight_begin
                            sit		= {1; 51; 1.5; 0.0033; 50; 1.5}	//앉아 있는 상태
                            stand	= {0; 0; 1.1; 0; 0; 0}	//서 있는 상태
                            low		= {0; 0; 1; 0; 0; 0}	//걸어서 이동하는 상태
                            high	= {0; 0; 0.7; 0; 0; 0}	//뛰어서 이동하는 상태
                        org_hp_regen_weight_end

                        //MP회복속도
                        org_mp_regen_weight_begin
                            sit		= {0; 0; 1.5; 0; 0; 0}	//앉아 있는 상태
                            stand	= {0; 0; 1.1; 0; 0; 0}	//서 있는 상태
                            low		= {0; 0; 1; 0; 0; 0}	//걸어서 이동하는 상태
                            high	= {0; 0; 0.7; 0; 0; 0}	//뛰어서 이동하는 상태
                        org_mp_regen_weight_end

                        //CP회복속도
                        org_cp_regen_weight_begin
                            sit		= {0; 0; 1.5; 0; 0; 0}	//앉아 있는 상태
                            stand	= {0; 0; 1.1; 0; 0; 0}	//서 있는 상태
                            low		= {0; 0; 1; 0; 0; 0}	//걸어서 이동하는 상태
                            high	= {0; 0; 0.7; 0; 0; 0}	//뛰어서 이동하는 상태
                        org_cp_regen_weight_end
                        */
                        Player pl = getPlayer();

                        pl.updateWaitSitTime();
                        if(pl.getWaitSitTime() > 5)
                        {
                            addHp += pl.getWaitSitTime();
                            addMp += pl.getWaitSitTime();
                            addCp += pl.getWaitSitTime();
                        }
                    }
                    else
                    {
                        addHp += getHpRegen() * 0.5;
                        addMp += getMpRegen() * 0.5;
                        addCp += getCpRegen() * 0.5;
                    }
                }
                else if(!isMoving)
                {
                    addHp += getHpRegen() * 0.1;
                    addMp += getMpRegen() * 0.1;
                    addCp += getCpRegen() * 0.1;
                }
                else if(isRunning())
                {
                    addHp -= getHpRegen() * 0.3;
                    addMp -= getMpRegen() * 0.3;
                    addCp -= getCpRegen() * 0.3;
                }

                if(isRaid())
				{
					addHp *= Config.RATE_RAID_REGEN;
					addMp *= Config.RATE_RAID_REGEN;
				}

				_currentHp += Math.max(0, Math.min(addHp, calcStat(Stats.HP_LIMIT, null, null) * maxHp / 100. - _currentHp));
                _currentHp = Math.min(maxHp, _currentHp);

                _currentMp += Math.max(0, Math.min(addMp, calcStat(Stats.MP_LIMIT, null, null) * maxMp / 100. - _currentMp));
				_currentMp = Math.min(maxMp, _currentMp);

                getListeners().onChangeCurrentHp(hpStart, _currentHp);
                getListeners().onChangeCurrentMp(mpStart, _currentMp);

				if(isPlayer())
				{
					_currentCp += Math.max(0, Math.min(addCp, calcStat(Stats.CP_LIMIT, null, null) * maxCp / 100. - _currentCp));
					_currentCp = Math.min(maxCp, _currentCp);
					getListeners().onChangeCurrentCp(cpStart, _currentCp);
                }

				//отрегенились, останавливаем задачу
				if(_currentHp == maxHp && _currentMp == maxMp && _currentCp == maxCp)
					stopRegeneration();
			}
			finally
			{
				regenLock.unlock();
			}

			broadcastStatusUpdate();
			sendChanges();

			checkHpMessages(hpStart, _currentHp);
		}
	}

	public final void stopAbnormalEffect(AbnormalEffect ae)
	{
		_abnormalEffects.remove(ae);
		sendChanges();
	}

	/**
	 * Блокируем персонажа
	 */
	public void block()
	{
		_blocked = true;
	}

	/**
	 * Разблокируем персонажа
	 */
	public void unblock()
	{
		_blocked = false;
	}

	public void setDamageBlockedException(Creature exception)
	{
		if(exception == null)
			_damageBlockedException = HardReferences.emptyRef();
		else
			_damageBlockedException = exception.getRef();
	}

	public void setEffectImmunityException(Creature exception)
	{
		if(exception == null)
			_effectImmunityException = HardReferences.emptyRef();
		else
			_effectImmunityException = exception.getRef();
	}

    @Override
    public boolean isInvisible(GameObject observer)
    {
	    return (observer == null || getObjectId() != observer.getObjectId()) && getFlags().getInvisible().get();
    }

    public boolean startInvisible(Object owner, boolean withServitors)
    {
        boolean result;
        if(owner == null)
            result = getFlags().getInvisible().start();
        else
            result = getFlags().getInvisible().start(owner);

        if(result)
        {
            for(Player p : World.getAroundObservers(this))
            {
                if(isInvisible(p))
                    p.sendPacket(p.removeVisibleObject(this, null));
            }

            if(withServitors)
            {
                for(Servitor servitor : getServitors())
                    servitor.startInvisible(owner, false);
            }
        }

        return result;
    }

    public final boolean startInvisible(boolean withServitors)
    {
        return startInvisible(null, withServitors);
    }

    public boolean stopInvisible(Object owner, boolean withServitors)
    {
        boolean result;
        if(owner == null)
            result = getFlags().getInvisible().stop();
        else
            result = getFlags().getInvisible().stop(owner);

        if(result)
        {
            List<Player> players = World.getAroundObservers(this);
            for(Player p : players)
            {
                if(!isInvisible(p))
                    p.sendPacket(p.addVisibleObject(this, null));
            }

            if(withServitors)
            {
                for(Servitor servitor : getServitors())
                    servitor.stopInvisible(owner, false);
            }
        }

        return result;
    }

    public final boolean stopInvisible(boolean withServitors)
    {
        return stopInvisible(null, withServitors);
    }

    public void addIgnoreSkillsEffect(Effect effect, TIntSet skills)
    {
        _ignoreSkillsEffects.put(effect, skills);
    }

    public boolean removeIgnoreSkillsEffect(Effect effect)
    {
        return _ignoreSkillsEffects.remove(effect) != null;
    }

    public boolean isIgnoredSkill(Skill skill)
    {
        for(TIntSet set : _ignoreSkillsEffects.values())
        {
            if(set.contains(skill.getId()))
                return true;
        }

        return false;
    }

    public boolean isUndying()
    {
        return getFlags().getUndying().get();
    }

    public boolean isInvulnerable()
    {
        return getFlags().getInvulnerable().get();
    }

	public void setFakeDeath(boolean value)
	{
		_fakeDeath = value;
	}

	public void breakFakeDeath()
	{
        getAbnormalList().stop(EffectType.FakeDeath);
	}

	public void setMeditated(boolean value)
	{
		_meditated = value;
	}

    public final void setPreserveAbnormal(boolean value)
    {
        _isPreserveAbnormal = value;
    }

	public final void setIsSalvation(boolean value)
	{
		_isSalvation = value;
	}

	public void setLockedTarget(boolean value)
	{
		_lockedTarget = value;
	}

	public boolean isConfused()
	{
		return getFlags().getConfused().get();
	}

	public boolean isFakeDeath()
	{
		return _fakeDeath;
	}

	public boolean isAfraid()
	{
		return getFlags().getAfraid().get();
	}

	public boolean isBlocked()
	{
		return _blocked;
	}

	public boolean isMuted(Skill skill)
	{
		if(skill == null || skill.isNotAffectedByMute())
			return false;
		return isMMuted() && skill.isMagic() || isPMuted() && !skill.isMagic();
	}

	public boolean isPMuted()
	{
		return getFlags().getPMuted().get();
	}

	public boolean isMMuted()
	{
		return getFlags().getMuted().get();
	}

	public boolean isAMuted()
	{
		return getFlags().getAMuted().get() || isTransformed() && !getTransform().getType().isCanAttack();
	}

	public boolean isMoveBlocked()
	{
		return getFlags().getMoveBlocked().get();
	}

	public boolean isSleeping()
	{
		return getFlags().getSleeping().get();
	}

	public boolean isStunned()
	{
		return getFlags().getStunned().get();
	}

	public boolean isMeditated()
	{
		return _meditated;
	}

	public boolean isWeaponEquipBlocked()
	{
		return getFlags().getWeaponEquipBlocked().get();
	}

	public boolean isParalyzed()
	{
		return getFlags().getParalyzed().get();
	}

	public boolean isFrozen()
	{
		return getFlags().getFrozen().get();
	}

	public boolean isImmobilized()
	{
		return getFlags().getImmobilized().get() || getRunSpeed() < 1;
	}

	public boolean isHealBlocked()
	{
		if(isInvulnerable())
			return true;

		return isAlikeDead() || getFlags().getHealBlocked().get();
	}

	public boolean isDamageBlocked(Creature attacker)
	{
        if(attacker == this)
            return false;

		if(isInvulnerable())
			return true;

		Creature exception = _damageBlockedException.get();
		if(exception != null && exception == attacker)
			return false;

		if(getFlags().getDamageBlocked().get())
		{
			double blockRadius = calcStat(Stats.DAMAGE_BLOCK_RADIUS);
			if(blockRadius == -1)
				return true;

			if(attacker == null)
				return false;

			if(attacker.getDistance(this) <= blockRadius)
				return true;
		}

		return false;
	}

	public boolean isDistortedSpace()
	{
		return getFlags().getDistortedSpace().get();
	}

	public boolean isCastingNow()
	{
		return _skillTask != null;
	}
	
	public boolean isLockedTarget()
	{
		return _lockedTarget;
	}

	public boolean isMovementDisabled()
	{
		return isBlocked() || isMoveBlocked() || isImmobilized() || isAlikeDead() || isStunned() || isSleeping() || isDecontrolled() || isAttackingNow() || isCastingNow() || isFrozen();
	}

	public final boolean isActionsDisabled()
	{
		return isActionsDisabled(true);
	}

	public boolean isActionsDisabled(boolean withCast)
	{
		return isBlocked() || isAlikeDead() || isStunned() || isSleeping() || isDecontrolled() || isAttackingNow() || withCast && isCastingNow() || isFrozen();
	}

    public boolean isUseItemDisabled()
    {
        return isAlikeDead() || isStunned() || isSleeping() || isParalyzed() || isFrozen();
    }

	public final boolean isDecontrolled()
	{
		return isParalyzed() || isKnockDowned() || isKnockBacked() || isFlyUp();
	}

	public final boolean isAttackingDisabled()
	{
		return _attackReuseEndTime > System.currentTimeMillis();
	}

	public boolean isOutOfControl()
	{
		return isBlocked() || isConfused() || isAfraid();
	}

    public void checkAndRemoveInvisible()
    {
        getAbnormalList().stop(AbnormalType.hide);
    }

	public void teleToLocation(Location loc)
	{
		teleToLocation(loc.x, loc.y, loc.z, getReflection());
	}

	public void teleToLocation(Location loc, Reflection r)
	{
		teleToLocation(loc.x, loc.y, loc.z, r);
	}

	public void teleToLocation(int x, int y, int z)
	{
		teleToLocation(x, y, z, getReflection());
	}

    public void teleToLocation(Location location, int min, int max)
    {
        teleToLocation(Location.findAroundPosition(location, min, max, 0), getReflection());
    }

	public void teleToLocation(int x, int y, int z, Reflection r)
	{
		if(!isTeleporting.compareAndSet(false, true))
			return;

		if(isFakeDeath())
			breakFakeDeath();

		abortCast(true, false);
		if(!isLockedTarget())
			setTarget(null);
		stopMove();

		if(!isBoat() && !isFlying() && !World.isWater(new Location(x, y, z), r))
			z = GeoEngine.getHeight(x, y, z, r.getGeoIndex());

		final Location loc = Location.findPointToStay(x, y, z, 0, 50, r.getGeoIndex());

		if(isPlayer())
		{
            Player player = (Player)this;
            if (!player.isInObserverMode())
                sendPacket(new TeleportToLocationPacket(this, loc.x, loc.y, loc.z));

            player.getListeners().onTeleport(loc.x, loc.y, loc.z, r);

            decayMe();

            setLoc(loc);

            setReflection(r);

            // Нужно при телепорте с более высокой точки на более низкую, иначе наносится вред от "падения"
            player.setLastClientPosition(null);
            player.setLastServerPosition(null);

            if (!player.isInObserverMode())
				sendPacket(new ExTeleportToLocationActivate(this, loc.x, loc.y, loc.z));

            if(player.isInObserverMode() || isFakePlayer())
                onTeleported();
		}
		else
		{
			broadcastPacket(new TeleportToLocationPacket(this, loc.x, loc.y, loc.z));

			setLoc(loc);

			setReflection(r);

			sendPacket(new ExTeleportToLocationActivate(this, loc.x, loc.y, loc.z));

			onTeleported();
		}
	}

	public boolean onTeleported()
	{
		return isTeleporting.compareAndSet(true, false);
	}

	public void sendMessage(CustomMessage message)
	{

	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName() + "[" + getObjectId() + "]";
	}

	@Override
	public double getCollisionRadius()
	{
		return getBaseStats().getCollisionRadius();
	}

	@Override
	public double getCollisionHeight()
	{
		return getBaseStats().getCollisionHeight();
	}

	public double getCurrentCollisionRadius()
	{
		return getCollisionRadius();
	}

	public double getCurrentCollisionHeight()
	{
		return getCollisionHeight();
	}

	public AbnormalList getAbnormalList()
	{
		if(_effectList == null)
		{
			synchronized (this)
			{
				if(_effectList == null)
					_effectList = new AbnormalList(this);
			}
		}

		return _effectList;
	}

	public boolean paralizeOnAttack(Creature attacker)
	{
		int max_attacker_level = 0xFFFF;

        if(isNpc())
        {
            NpcInstance npc = (NpcInstance) this;
            NpcInstance leader = npc.getLeader();

            if(isRaid() || (leader != null && leader.isRaid()))
	            max_attacker_level = getLevel() + npc.getParameter("ParalizeOnAttack", Config.RAID_MAX_LEVEL_DIFF);
            else
            {
                int max_level_diff = npc.getParameter("ParalizeOnAttack", -1000);
                if(max_level_diff != -1000)
                    max_attacker_level = getLevel() + max_level_diff;
            }
        }

		if(attacker.getLevel() > max_attacker_level)
			return true;

		return false;
	}

	@Override
	protected void onDelete()
	{
        CharacterAI ai = getAI();
        if(ai != null)
        {
            ai.stopAllTaskAndTimers();
            ai.notifyEvent(CtrlEvent.EVT_DELETE);
        }

        stopDeleteTask();

        if(!isObservePoint())
            GameObjectsStorage.remove(this);

        getAbnormalList().stopAll();

		super.onDelete();
	}

	// ---------------------------- Not Implemented -------------------------------

	public void addExpAndSp(long exp, long sp)
	{}

	public void broadcastCharInfo()
	{}

	public void broadcastCharInfoImpl(IUpdateTypeComponent... components)
	{}

	public void checkHpMessages(double currentHp, double newHp)
	{}

	public boolean checkPvP(Creature target, Skill skill)
	{
		return false;
	}

	public boolean consumeItem(int itemConsumeId, long itemCount, boolean sendMessage)
	{
		return true;
	}

	public boolean consumeItemMp(int itemId, int mp)
	{
		return true;
	}

	public boolean isFearImmune()
	{
		return isPeaceNpc();
	}

	public boolean isThrowAndKnockImmune()
	{
		return isPeaceNpc();
	}

	public boolean isTransformImmune()
	{
		return isPeaceNpc();
	}

	public boolean isLethalImmune()
	{
		return isBoss() || isRaid();
	}

	public double getChargedSoulshotPower()
	{
		return 0;
	}

	public void setChargedSoulshotPower(double val)
	{
		//
	}

	public double getChargedSpiritshotPower()
	{
		return 0;
	}

	public void setChargedSpiritshotPower(double val)
	{
		//
	}

	public int getIncreasedForce()
	{
		return 0;
	}

	public int getConsumedSouls()
	{
		return 0;
	}

	public int getKarma()
	{
		return 0;
	}

	public boolean isPK()
	{
		return getKarma() < 0;
	}

	public double getLevelBonus()
	{
        return LevelBonusHolder.getInstance().getLevelBonus(getLevel());
	}

	public int getNpcId()
	{
		return 0;
	}

	public boolean isMyServitor(int objId)
	{
		return false;
	}

    public List<Servitor> getServitors()
    {
        return Collections.emptyList();
    }

	public int getPvpFlag()
	{
		return 0;
	}

	public void setTeam(TeamType t)
	{
		_team = t;
		sendChanges();
	}

	public TeamType getTeam()
	{
		return _team;
	}

	public boolean isUndead()
	{
		return false;
	}

	public boolean isParalyzeImmune()
	{
		return false;
	}

	public void reduceArrowCount()
	{}

	public void sendChanges()
	{
		getStatsRecorder().sendChanges();
	}

	public void sendMessage(String message)
	{}

    public void sendPacket(IBroadcastPacket mov)
    {}

    public void sendPacket(IBroadcastPacket... mov)
    {}

    public void sendPacket(List<? extends IBroadcastPacket> mov)
    {}

    public int getMaxIncreasedForce()
    {
        return (int) calcStat(Stats.MAX_INCREASED_FORCE, 10, null, null);
    }

	public void setIncreasedForce(int i)
	{}

	public void setConsumedSouls(int i, NpcInstance monster)
	{}

	public void startPvPFlag(Creature target)
	{}

	public boolean unChargeShots(boolean spirit)
	{
		return false;
	}

	private Future<?> _updateAbnormalIconsTask;

	private class UpdateAbnormalIcons extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
            updateAbnormalIconsImpl();
            _updateAbnormalIconsTask = null;
		}
	}

	public void updateAbnormalIcons()
	{
		if(Config.USER_INFO_INTERVAL == 0)
		{
			if(_updateAbnormalIconsTask != null)
			{
                _updateAbnormalIconsTask.cancel(false);
                _updateAbnormalIconsTask = null;
			}
            updateAbnormalIconsImpl();
			return;
		}

		if(_updateAbnormalIconsTask != null)
			return;

        _updateAbnormalIconsTask = ThreadPoolManager.getInstance().schedule(new UpdateAbnormalIcons(), Config.USER_INFO_INTERVAL);
	}

	public void updateAbnormalIconsImpl()
	{
		broadcastAbnormalStatus(getAbnormalStatusUpdate());
	}

	public ExAbnormalStatusUpdateFromTargetPacket getAbnormalStatusUpdate()
	{
        if(!Config.SHOW_TARGET_EFFECTS)
            return null;

        Abnormal[] effects = getAbnormalList().toArray();
		Arrays.sort(effects, AbnormalsComparator.getInstance());

		ExAbnormalStatusUpdateFromTargetPacket abnormalStatus = new ExAbnormalStatusUpdateFromTargetPacket(getObjectId());
		for(Abnormal effect : effects)
		{
			if(effect != null && !effect.checkAbnormalType(AbnormalType.hp_recover))
				effect.addIcon(abnormalStatus);
		}
		return abnormalStatus;
	}

	public void broadcastAbnormalStatus(ExAbnormalStatusUpdateFromTargetPacket packet)
	{
		if(getTarget() == this)
			sendPacket(packet);

		if(!isVisible())
			return;

		List<Player> players = World.getAroundObservers(this);
		Player target;
		for(int i = 0; i < players.size(); i++)
		{
			target = players.get(i);
			if(target.getTarget() == this)
				target.sendPacket(packet);
		}
	}

	/**
	 * Выставить предельные значения HP/MP/CP и запустить регенерацию, если в этом есть необходимость
	 */
	protected void refreshHpMpCp()
	{
		final int maxHp = getMaxHp();
		final int maxMp = getMaxMp();
		final int maxCp = isPlayer() ? getMaxCp() : 0;

		if(_currentHp > maxHp)
			setCurrentHp(maxHp, false);
		if(_currentMp > maxMp)
			setCurrentMp(maxMp, false);
		if(_currentCp > maxCp)
			setCurrentCp(maxCp, false);

		if(_currentHp < maxHp || _currentMp < maxMp || _currentCp < maxCp)
			startRegeneration();
	}

	public void updateStats()
	{
		refreshHpMpCp();
		sendChanges();
	}

	public void setOverhitAttacker(Creature attacker)
	{}

	public void setOverhitDamage(double damage)
	{}

	public boolean isHero()
	{
		return false;
	}

	public int getAccessLevel()
	{
		return 0;
	}

	public Clan getClan()
	{
		return null;
	}

	public int getFormId()
	{
		return 0;
	}

	public boolean isNameAbove()
	{
		return true;
	}

	@Override
	public void setLoc(Location loc)
	{
		setXYZ(loc.x, loc.y, loc.z);
	}

	public void setLoc(Location loc, boolean MoveTask)
	{
		setXYZ(loc.x, loc.y, loc.z, MoveTask);
	}

	@Override
	public void setXYZ(int x, int y, int z)
	{
		setXYZ(x, y, z, false);
	}

	public void setXYZ(int x, int y, int z, boolean MoveTask)
	{
		if(!MoveTask)
			stopMove();

		moveLock.lock();
		try
		{
			super.setXYZ(x, y, z);
		}
		finally
		{
			moveLock.unlock();
		}

		updateZones();
	}

	@Override
	protected void onSpawn()
	{
		super.onSpawn();

		updateStats();
		updateZones();
	}

	@Override
	public void spawnMe(Location loc)
	{
		if(loc.h > 0)
			setHeading(loc.h);
		super.spawnMe(loc);
	}

	@Override
	protected void onDespawn()
	{
		if(!isLockedTarget())
			setTarget(null);
		stopMove();
		stopAttackStanceTask();
		stopRegeneration();

		updateZones();

		super.onDespawn();
	}

	public final void doDecay()
	{
		if(!isDead())
			return;

		onDecay();
	}

	protected void onDecay()
	{
		decayMe();
	}

	public void validateLocation(int broadcast)
	{
		L2GameServerPacket sp = new ValidateLocationPacket(this);
		if(broadcast == 0)
			sendPacket(sp);
		else if(broadcast == 1)
			broadcastPacket(sp);
		else
			broadcastPacketToOthers(sp);
	}

	// Функция для дизактивации умений персонажа (если умение не активно, то он не дает статтов и имеет серую иконку).
	private TIntSet _unActiveSkills = new TIntHashSet();

	public void addUnActiveSkill(Skill skill)
	{
		if(skill == null || isUnActiveSkill(skill.getId()))
			return;

		if(skill.isToggle())
            getAbnormalList().stop(skill);

		removeStatsOwner(skill);
		removeTriggers(skill);

		_unActiveSkills.add(skill.getId());
	}

	public void removeUnActiveSkill(Skill skill)
	{
		if(skill == null || !isUnActiveSkill(skill.getId()))
			return;

		addStatFuncs(skill.getStatFuncs());
		addTriggers(skill);

		_unActiveSkills.remove(skill.getId());
	}

	public boolean isUnActiveSkill(int id)
	{
		return _unActiveSkills.contains(id);
	}

	public abstract int getLevel();

	public abstract ItemInstance getActiveWeaponInstance();

	public abstract WeaponTemplate getActiveWeaponTemplate();

	public abstract ItemInstance getSecondaryWeaponInstance();

	public abstract WeaponTemplate getSecondaryWeaponTemplate();

	public CharListenerList getListeners()
	{
		if(listeners == null)
			synchronized (this)
			{
				if(listeners == null)
					listeners = new CharListenerList(this);
			}
		return listeners;
	}

	public <T extends Listener<Creature>> boolean addListener(T listener)
	{
		return getListeners().add(listener);
	}

	public <T extends Listener<Creature>> boolean removeListener(T listener)
	{
		return getListeners().remove(listener);
	}

	public CharStatsChangeRecorder<? extends Creature> getStatsRecorder()
	{
		if(_statsRecorder == null)
			synchronized (this)
			{
				if(_statsRecorder == null)
					_statsRecorder = new CharStatsChangeRecorder<Creature>(this);
			}

		return _statsRecorder;
	}

	@Override
	public boolean isCreature()
	{
		return true;
	}

	public void displayGiveDamageMessage(Creature target, Skill skill, int damage, Servitor servitorTransferedDamage, int transferedDamage, boolean crit, boolean miss, boolean shld, boolean blocked)
	{
        if(miss)
        {
            if(target.isPlayer())
                target.sendPacket(new SystemMessage(SystemMessage.C1_HAS_EVADED_C2S_ATTACK).addName(target).addName(this));
            return;
        }

		if(!blocked)
        {
            if(shld && target.isPlayer())
            {
                if(damage == Config.EXCELLENT_SHIELD_BLOCK_RECEIVED_DAMAGE)
                    target.sendPacket(SystemMsg.YOUR_EXCELLENT_SHIELD_DEFENSE_WAS_A_SUCCESS);
                else if(damage > 0)
                    target.sendPacket(SystemMsg.YOUR_SHIELD_DEFENSE_HAS_SUCCEEDED);
            }
        }
    }

	public void displayReceiveDamageMessage(Creature attacker, int damage)
	{
		//
	}

	public Collection<TimeStamp> getSkillReuses()
	{
		return _skillReuses.valueCollection();
	}

	public TimeStamp getSkillReuse(Skill skill)
	{
		return _skillReuses.get(skill.getReuseHash());
	}

	public Sex getSex()
	{
		return Sex.MALE;
	}

	public final boolean isInFlyingTransform()
	{
		if(isTransformed())
			return getTransform().getType() == TransformType.FLYING;
		return false;
	}

	public final boolean isVisualTransformed()
	{
		return getVisualTransform() != null;
	}

	public final int getVisualTransformId()
	{
		if(getVisualTransform() != null)
			return getVisualTransform().getId();

		return 0;
	}

	public final TransformTemplate getVisualTransform()
	{
		if(_isInTransformUpdate)
			return null;

		if(_visualTransform != null)
			return _visualTransform;

		return getTransform();
	}

	public final void setVisualTransform(int id)
	{
		TransformTemplate template = id > 0 ? TransformTemplateHolder.getInstance().getTemplate(getSex(), id) : null;
		setVisualTransform(template);
	}

	public void setVisualTransform(TransformTemplate template)
	{
		if(_visualTransform == template)
			return;

		if(template != null && isVisualTransformed() || template == null && isTransformed())
		{
			_isInTransformUpdate = true;
			_visualTransform = null;

			sendChanges();

			_isInTransformUpdate = false;
		}

		_visualTransform = template;

		sendChanges();
	}

	public boolean isTransformed()
	{
		return false;
	}

	public final int getTransformId()
	{
		if(isTransformed())
			return getTransform().getId();

		return 0;
	}

	public TransformTemplate getTransform()
	{
		return null;
	}

	public void setTransform(int id)
	{
		//
	}

	public void setTransform(TransformTemplate template)
	{
		//
	}

	public boolean isDeathImmune()
	{
		return getFlags().getDeathImmunity().get() || isPeaceNpc();
	}

	public int getMoveTickInterval()
	{
		return (isPlayer() && !isVisualTransformed() ? 16000 : 32000) / Math.max(getMoveSpeed(), 1);
	}

	public final double getMovementSpeedMultiplier()
	{
		return getRunSpeed() * 1. / getBaseStats().getRunSpd();
	}

	@Override
	public int getMoveSpeed()
	{
		if(isRunning())
			return getRunSpeed();

		return getWalkSpeed();
	}

	public int getRunSpeed()
	{
		if(isInWater())
			return getSwimRunSpeed();

		return getSpeed(getBaseStats().getRunSpd());
	}

	public int getWalkSpeed()
	{
		if(isInWater())
			return getSwimWalkSpeed();

		return getSpeed(getBaseStats().getWalkSpd());
	}

	public final int getSwimRunSpeed()
	{
		return getSpeed(getBaseStats().getWaterRunSpd());
	}

	public final int getSwimWalkSpeed()
	{
		return getSpeed(getBaseStats().getWaterWalkSpd());
	}

	public double relativeSpeed(GameObject target)
	{
		return getMoveSpeed() - target.getMoveSpeed() * Math.cos(headingToRadians(getHeading()) - headingToRadians(target.getHeading()));
	}

	public final int getSpeed(double baseSpeed)
	{
		return (int) Math.max(1, calcStat(Stats.RUN_SPEED, baseSpeed, null, null));
	}

	public double getHpRegen()
	{
		return calcStat(Stats.REGENERATE_HP_RATE, getBaseStats().getHpReg());
	}

	public double getMpRegen()
	{
		return calcStat(Stats.REGENERATE_MP_RATE, getBaseStats().getMpReg());
	}

	public double getCpRegen()
	{
		return calcStat(Stats.REGENERATE_CP_RATE, getBaseStats().getCpReg());
	}

	public int getEnchantEffect()
	{
		return 0;
	}

	public final boolean isKnockDowned()
	{
		return getFlags().getKnockDowned().get();
	}

	public final boolean isKnockBacked()
	{
		return getFlags().getKnockBacked().get();
	}

	public final boolean isFlyUp()
	{
		return getFlags().getFlyUp().get();
	}

	public void setRndCharges(int value)
	{
		_rndCharges = value;
	}

	public int getRndCharges()
	{
		return _rndCharges;
	}

	public boolean isPeaceNpc()
	{
		return false;
	}

    public int getInteractionDistance(GameObject target)
    {
        int range = (int) Math.max(10, getMinDistance(target));
        if(target.isNpc())
        {
            range += 100;
            if(!target.isInRangeZ(this, range))
            {
                List<Location> moveList = GeoEngine.MoveList(getX(), getY(), getZ(), target.getX(), target.getY(), getGeoIndex(), false);
                if(moveList != null)
                {
                    Location moveLoc = moveList.get(moveList.size() - 1).geo2world();
                    if(!target.isInRangeZ(moveLoc, range) && target.isInRangeZ(moveLoc, range + 100))
                        range = (int) target.getDistance3D(moveLoc) + 20;
                }
            }
        }
        else
            range += 200;

        return range;
    }

    public boolean checkInteractionDistance(GameObject target)
    {
        return isInRangeZ(target, getInteractionDistance(target));
    }

    public boolean isTargetable(Creature creature)
    {
        if(creature != null)
        {
            if(creature == this)
                return true;

            if(creature.isPlayer() && creature.getPlayer().isGM())
                return true;
        }
        return _isTargetable;
    }

    public boolean isTargetable()
    {
        return isTargetable(this);
    }

	public void setTargetable(boolean value)
	{
		_isTargetable = value;
	}
	
	private boolean checkRange(Creature caster, Creature target)
	{
		return caster.isInRange(target, Config.REFLECT_MIN_RANGE);
	}
	
	private boolean canAbsorb(Creature attacked, Creature attacker)
	{
		if(attacked.isPlayable() || !Config.DISABLE_VAMPIRIC_VS_MOB_ON_PVP)
			return true;
		return attacker.getPvpFlag() == 0;		
	}

	public CreatureBaseStats getBaseStats()
	{
		if(_baseStats == null)
			_baseStats = new CreatureBaseStats(this);
		return _baseStats;
	}

    public CreatureFlags getFlags()
    {
        if(_statuses == null)
            _statuses = new CreatureFlags(this);
        return _statuses;
    }

	public boolean isSpecialAbnormal(Skill skill)
	{
		return false;
	}

	// Аналог isInvul, но оно не блокирует атаку, а просто не отнимает ХП.
	public boolean isImmortal()
	{
		return false;
	}

	public boolean isChargeBlocked()
	{
		return true;
	}

	public int getAdditionalVisualSSEffect()
	{
		return 0;
	}
	
	public boolean isSymbolInstance()
	{
		return false;
	}

    public boolean isFakePlayer()
    {
        return false;
    }

    public boolean isTargetUnderDebuff()
    {
        for(Abnormal effect : getAbnormalList())
        {
            if(effect.isOffensive())
                return true;
        }
        return false;
    }

    public boolean isSitting()
    {
        return false;
    }

    public void sendChannelingEffect(Creature target, int state)
    {
        broadcastPacket(new ExShowChannelingEffectPacket(this, target, state));
    }

    public void startDeleteTask(long delay)
    {
        stopDeleteTask();
        _deleteTask = ThreadPoolManager.getInstance().schedule(new DeleteTask(this), delay);
    }

    public void stopDeleteTask()
    {
        if(_deleteTask != null)
        {
            _deleteTask.cancel(false);
            _deleteTask = null;
        }
    }

    public void deleteCubics()
    {}

    public void onZoneEnter(final Zone zone)
    {}

    public void onZoneLeave(final Zone zone)
    {}

    public Element getAttackElement()
    {
        return Formulas.getAttackElement(this, null);
    }

    public int getAttack(Element element)
    {
        Stats stat = element.getAttack();
        if(stat != null)
            return (int) calcStat(stat);

        return 0;
    }

    public int getDefence(Element element)
    {
        Stats stat = element.getDefence();
        if(stat != null)
            return (int) calcStat(stat);

        return 0;
    }

    public boolean hasBasicPropertyResist()
    {
        return true;
    }

    public BasicPropertyResist getBasicPropertyResist(BasicProperty basicProperty)
    {
        if(_basicPropertyResists == null)
        {
            synchronized(this)
            {
                if(_basicPropertyResists == null)
                    _basicPropertyResists = new ConcurrentHashMap<BasicProperty, BasicPropertyResist>();
            }
        }
        return _basicPropertyResists.computeIfAbsent(basicProperty, k -> new BasicPropertyResist());
    }
}