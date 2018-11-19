package l2s.gameserver.model.actor.instances.creature;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.Config;
import l2s.gameserver.listener.actor.OnAttackListener;
import l2s.gameserver.listener.actor.OnMagicUseListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.*;
import l2s.gameserver.skills.AbnormalEffect;
import l2s.gameserver.skills.AbnormalType;
import l2s.gameserver.skills.EffectUseType;
import l2s.gameserver.skills.effects.Effect;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.triggers.TriggerType;
import l2s.gameserver.taskmanager.EffectTaskManager;
import l2s.gameserver.templates.skill.EffectTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Abnormal extends RunnableImpl implements Comparable<Abnormal>
{
	private static final Logger _log = LoggerFactory.getLogger(Abnormal.class);

	//Состояние, при котором работает задача запланированного эффекта
	private static final int SUSPENDED = -1;

	private static final int STARTING = 0;
	private static final int ACTING = 1;
	private static final int FINISHED = 2;

	/** Накладывающий эффект */
	private final Creature _effector;
	/** Тот, на кого накладывают эффект */
	private final Creature _effected;

	private final Skill _skill;

	private final Env _env;
	private final EffectUseType _useType;
	private final Collection<Effect> _effects = new ConcurrentLinkedQueue<Effect>();

	// the current state
	private final AtomicInteger _state;

	private final boolean _saveable;

	private Future<?> _effectTask;

	private long _startTimeMillis = Long.MAX_VALUE;

	private int _duration;
	private int _timeLeft;

	public Abnormal(Creature effector, Creature effected, Skill skill, EffectUseType useType, boolean saveable)
	{
		_effector = effector;
		_effected = effected;
		_skill = skill;
		_env = new Env(effector, effected, skill);
		_useType = useType;


		_duration = Math.min(Integer.MAX_VALUE, Math.max(0, getSkill().getAbnormalTime() < 0 ? Integer.MAX_VALUE : getSkill().getAbnormalTime()));
		_timeLeft = _duration;

		_state = new AtomicInteger(STARTING);
		_saveable = saveable;

		List<EffectTemplate> templates = getSkill().getEffectTemplates(getUseType());
		if(!templates.isEmpty())
		{
			for(EffectTemplate template : templates)
			{
				if(template.isInstant() || !isOfUseType(template.getUseType()))
					continue;

				Effect effect = template.getEffect(this, getEnv());
				if(effect == null)
					continue;

				_effects.add(effect);
			}
		}
	}

	public Abnormal(Creature effector, Creature effected, Abnormal abnormal)
	{
		this(effector, effected, abnormal.getSkill(), abnormal.getUseType(), true);
	}

	public Env getEnv()
	{
		return _env;
	}

	public Skill getSkill()
	{
		return _skill;
	}

	public AbnormalType getAbnormalType()
	{
		return getSkill().getAbnormalType();
	}

	public int getAbnormalLvl()
	{
		return getSkill().getAbnormalLvl();
	}

	public Creature getEffector()
	{
		return _effector;
	}

	public Creature getEffected()
	{
		return _effected;
	}

	public boolean isReflected()
	{
		return _env.reflected;
	}

	/**
	 * Возвращает время старта эффекта, если время не установлено, возвращается текущее
	 */
	public long getStartTime()
	{
		return _startTimeMillis;
	}

	/** Возвращает оставшееся время в секундах. */
	public int getTimeLeft()
	{
		return _timeLeft;
	}

	public void setTimeLeft(int value)
	{
		_timeLeft = Math.max(0, Math.min(value, _duration));
	}

	/** Возвращает true, если осталось время для действия эффекта */
	public boolean isTimeLeft()
	{
		return getTimeLeft() > 0;
	}

	public Collection<Effect> getEffects()
	{
		return _effects;
	}

	public boolean isActive()
	{
		return getState() == ACTING;
	}

	/**
	 * Для неактивных эфектов не вызывается onActionTime.
	 */
	public boolean isSuspended()
	{
		return getState() == SUSPENDED;
	}

	public boolean checkAbnormalType(AbnormalType abnormal)
	{
		AbnormalType abnormalType = getAbnormalType();
		if(abnormalType == AbnormalType.none)
			return false;

		return abnormal == abnormalType;
	}

	public boolean checkAbnormalType(Abnormal effect)
	{
		return checkAbnormalType(effect.getAbnormalType());
	}

	public boolean isFinished()
	{
		return getState() == FINISHED;
	}

	private int getState()
	{
		return _state.get();
	}

	private boolean setState(int oldState, int newState)
	{
		return _state.compareAndSet(oldState, newState);
	}

	private ActionDispelListener _listener;

	private class ActionDispelListener implements OnAttackListener, OnMagicUseListener
	{
		@Override
		public void onMagicUse(Creature actor, Skill skill, Creature target, boolean alt)
		{
			if(getSkill().isDoNotDispelOnSelfBuff() && !skill.isOffensive())
				return;
			exit();
		}

		@Override
		public void onAttack(Creature actor, Creature target)
		{
			exit();
		}
	}

	public boolean checkCondition()
	{
		for(Effect effect : getEffects())
		{
			if(getEffected().isRaid() && effect.getEffectType().isRaidImmune())
				return false;

			if(!effect.checkCondition())
				return false;
		}
		return true;
	}

	public boolean checkActingCondition()
	{
		if(isOfUseType(EffectUseType.START) || isOfUseType(EffectUseType.TICK))
		{
			if(getEffector().getCastingSkill() == getSkill())
				return true;

			return false;
		}

		for(Effect effect : getEffects())
		{
			if (!effect.checkActingCondition())
				return false;
		}

		return true;
	}

	private void onStart()
	{
		if(getSkill().isAbnormalCancelOnAction() && getEffected().isPlayable())
			getEffected().addListener(_listener = new ActionDispelListener());
		if(getEffected().isPlayer() && !getSkill().canUseTeleport())
			getEffected().getPlayer().getPlayerAccess().UseTeleport = false;

		for(AbnormalEffect abnormal : getSkill().getAbnormalEffects())
		{
			if(abnormal != AbnormalEffect.NONE)
				getEffected().startAbnormalEffect(abnormal);
		}

		for(Effect effect : _effects)
		{
			effect.onStart();
			getEffected().addStatFuncs(effect.getStatFuncs());
			getEffected().addTriggers(effect.getTemplate());
			getEffected().useTriggers(getEffected(), TriggerType.ON_START_EFFECT, null, getSkill(), effect.getTemplate(), 0);
		}
	}

	/**
	 * Cancel the effect in the the abnormal effect map of the effected L2Character.<BR><BR>
	 */
	private void onExit()
	{
		if (getSkill().isAbnormalCancelOnAction())
			getEffected().removeListener(_listener);

		if(getEffected().isPlayer())
		{
			if(checkAbnormalType(AbnormalType.hp_recover))
				getEffected().sendPacket(new ShortBuffStatusUpdatePacket());
			if(!getSkill().canUseTeleport() && !getEffected().getPlayer().getPlayerAccess().UseTeleport)
				getEffected().getPlayer().getPlayerAccess().UseTeleport = true;
		}

		if(isOfUseType(EffectUseType.START) || isOfUseType(EffectUseType.TICK))
		{
			if(getEffected() == getEffector().getCastingTarget() && getSkill() == getEffector().getCastingSkill())
				getEffector().abortCast(true, false);
		}

		for(AbnormalEffect abnormal : getSkill().getAbnormalEffects())
		{
			if(abnormal != AbnormalEffect.NONE)
				getEffected().stopAbnormalEffect(abnormal);
		}

		for(Effect effect : _effects)
		{
			effect.onExit();
			getEffected().removeStatsOwner(effect);
			getEffected().removeTriggers(effect.getTemplate());
			getEffected().useTriggers(getEffected(), TriggerType.ON_EXIT_EFFECT, null, getSkill(), effect.getTemplate(), 0);
		}
	}

	private void stopEffectTask()
	{
		if(_effectTask != null)
		{
			_effectTask.cancel(false);
			_effectTask = null;
		}
	}

	private void startEffectTask()
	{
		if(_effectTask == null)
		{
			_startTimeMillis = System.currentTimeMillis();
			_effectTask = EffectTaskManager.getInstance().scheduleAtFixedRate(this, 1000L, 1000L);
		}
	}

	public void restart()
	{
		_timeLeft = getDuration();

		stopEffectTask();
		startEffectTask();
	}

	public boolean apply(Creature aimingTarget)
	{
		if(getEffected().isDead() && !getSkill().isPreservedOnDeath())
			return false;

		if(getEffector() != getEffected() && isOfUseType(EffectUseType.NORMAL))
		{
			if(getEffected().isEffectImmune(getEffector()))
				return false;
		}
		if(getEffector() != getEffected() && ((getEffected().isBuffImmune() && !isOffensive()) || (getEffected().isDebuffImmune() && isOffensive())))
		{
			for(Abnormal abnormal : getEffected().getAbnormalList())
			{
				if(abnormal.checkDebuffImmunity())
					break;
			}
			if(!isHidden() && !getSkill().isHideStartMessage())
			{
				if(getEffected() == aimingTarget)
				{
					getEffector().sendPacket(new SystemMessagePacket(SystemMsg.C1_HAS_RESISTED_YOUR_S2).addName(getEffected()).addSkillName(getSkill().getDisplayId(), getSkill().getDisplayLevel()));
					getEffector().sendPacket(new ExMagicAttackInfo(getEffector().getObjectId(), getEffected().getObjectId(), ExMagicAttackInfo.RESISTED));
				}
			}
			return false;
		}

		if(schedule())
		{
			if(!isHidden() && !getSkill().isHideStartMessage())
				getEffected().sendPacket(new SystemMessagePacket(SystemMsg.S1S_EFFECT_CAN_BE_FELT).addSkillName(getDisplayId(), getDisplayLevel()));
			return true;
		}
		return false;
	}


	/**
	 * Добавляет эффект в список эффектов, в случае успешности вызывается метод start
	 */
	public boolean schedule()
	{
		Creature effected = getEffected();
		if(effected == null)
			return false;

		if(_effects.isEmpty())
			return false;

		if(!checkCondition())
			return false;

		return getEffected().getAbnormalList().add(this);
	}

	/**
	 * Переводит эффект в "фоновый" режим, эффект может быть запущен методом schedule
	 */
	public void suspend()
	{
		// Эффект создан, запускаем задачу в фоне
		if(setState(STARTING, SUSPENDED))
			startEffectTask();
		else if(setState(ACTING, SUSPENDED))
		{
			synchronized (this)
			{
				onExit();
			}
		}
	}

	/**
	 * Запускает задачу эффекта, в случае если эффект успешно добавлен в список
	 */
	public void start()
	{
		if(setState(SUSPENDED, ACTING))
		{
			synchronized(this)
			{
				onStart();
			}
		}
		else if(setState(STARTING, ACTING))
		{
			synchronized(this)
			{
				onStart();
				startEffectTask();
			}
		}
	}

	@Override
	public void runImpl() throws Exception
	{
		_timeLeft--;

		if(getState() == SUSPENDED)
		{
			if(isTimeLeft())
				return;

			exit();
			return;
		}

		boolean successActing = true;

		if(getState() == ACTING)
		{
			if(isTimeLeft())
			{
				if(checkActingCondition())
				{
					for(Effect effect : _effects)
					{
						if(getTimeLeft() % effect.getInterval() == 0)
						{
							successActing = effect.onActionTime();
							if(!successActing)
								break;
						}
					}
					if(successActing)
						return;
				}
			}
		}

		if(getDuration() == Integer.MAX_VALUE) // Если вдруг закончится время у безконечного эффекта.
		{
			if(checkActingCondition())
			{
				for(Effect effect : _effects)
				{
					if(getDuration() % effect.getInterval() == 0)
					{
						successActing = effect.onActionTime();
						if(!successActing)
							break;
					}
				}

				if(successActing)
				{
					_timeLeft = getDuration();
					return;
				}
			}
		}

		if(setState(ACTING, FINISHED))
		{
			if(checkActingCondition())
			{
				for(Effect effect : _effects)
				{
					if (getDuration() % effect.getInterval() == 0)
						effect.onActionTime();
				}
			}

			synchronized(this)
			{
				stopEffectTask();
				onExit();
			}

			boolean lastEffect = getEffected().getAbnormalList().getCount(getSkill()) == 1;
			boolean msg = successActing && !isHidden() && lastEffect;

			getEffected().getAbnormalList().remove(this);

			// Отображать сообщение только для последнего оставшегося эффекта скилла
			if(msg)
				getEffected().sendPacket(new SystemMessage(SystemMessage.S1_HAS_WORN_OFF).addSkillName(getDisplayId(), getDisplayLevel()));

			if(lastEffect)
				getSkill().onAbnormalTimeEnd(getEffector(), getEffected());

			//tigger on finish
			for(Effect effect : getEffects())
				getEffected().useTriggers(getEffected(), TriggerType.ON_FINISH_EFFECT, null, getSkill(), effect.getTemplate(), 0);
		}
	}

	/**
	 * Завершает эффект и все связанные, удаляет эффект из списка эффектов
	 */
	public void exit()
	{
		//Эффект запланирован на запуск, удаляем
		if(setState(STARTING, FINISHED))
			getEffected().getAbnormalList().remove(this);
			//Эффект работает в "фоне", останавливаем задачу в планировщике
		else if(setState(SUSPENDED, FINISHED))
			stopEffectTask();
		else if(setState(ACTING, FINISHED))
		{
			synchronized (this)
			{
				stopEffectTask();
				onExit();
			}
			getEffected().getAbnormalList().remove(this);
		}
	}

	public void addIcon(AbnormalStatusUpdatePacket abnormalStatus)
	{
		if(!isActive() || isHidden())
			return;
		int duration = isHideTime() ? AbnormalStatusUpdatePacket.INFINITIVE_EFFECT : getTimeLeft();
		abnormalStatus.addEffect(getDisplayId(), getDisplayLevel(), duration);
	}

	public void addIcon(ExAbnormalStatusUpdateFromTargetPacket abnormalStatus)
	{
		if(!isActive() || isHidden())
			return;
		int duration = isHideTime() ? AbnormalStatusUpdatePacket.INFINITIVE_EFFECT : getTimeLeft();
		abnormalStatus.addEffect(getEffector().getObjectId(), getDisplayId(), getDisplayLevel(), duration, 0);
	}

	public void addPartySpelledIcon(PartySpelledPacket ps)
	{
		if(!isActive() || isHidden())
			return;
		int duration = isHideTime() ? AbnormalStatusUpdatePacket.INFINITIVE_EFFECT : getTimeLeft();
		ps.addPartySpelledEffect(getDisplayId(), getDisplayLevel(), duration);
	}

	public void addOlympiadSpelledIcon(Player player, ExOlympiadSpelledInfoPacket os)
	{
		if(!isActive() || isHidden())
			return;
		int duration = isHideTime() ? AbnormalStatusUpdatePacket.INFINITIVE_EFFECT : getTimeLeft();
		os.addSpellRecivedPlayer(player);
		os.addEffect(getDisplayId(), getDisplayLevel(), duration);
	}

	@Override
	public int compareTo(Abnormal obj)
	{
		if(obj.equals(this))
			return 0;
		return 1;
	}

	public boolean isCancelable()
	{
		return getSkill().isCancelable() && !isHidden();
	}

	public boolean isSelfDispellable()
	{
		return getSkill().isSelfDispellable() && !isHidden();
	}

	public int getId()
	{
		return getSkill().getId();
	}

	public int getLevel()
	{
		return getSkill().getLevel();
	}

	public int getDisplayId()
	{
		return getSkill().getDisplayId();
	}

	public int getDisplayLevel()
	{
		return getSkill().getDisplayLevel();
	}

	@Override
	public String toString()
	{
		return "Skill: " + getSkill() + ", state: " + getState() + ", active : " + isActive();
	}

	public boolean checkBlockedAbnormalType(AbnormalType abnormal)
	{
		for(Effect effect : _effects)
		{
			if(effect.checkBlockedAbnormalType(abnormal))
				return true;
		}
		return false;
	}

	public boolean checkDebuffImmunity()
	{
		for(Effect effect : _effects)
		{
			if(effect.checkDebuffImmunity())
				return true;
		}
		return false;
	}

	public boolean isHidden()
	{
		if(getDisplayId() < 0 || isOfUseType(EffectUseType.START) || isOfUseType(EffectUseType.TICK))
			return true;
		for(Effect effect : _effects)
		{
			if(effect.isHidden())
				return true;
		}
		return false;
	}

	public boolean isSaveable()
	{
		if(!_saveable || !getSkill().isSaveable() || getTimeLeft() < Config.ALT_SAVE_EFFECTS_REMAINING_TIME || isHidden())
			return false;

		for(Effect effect : _effects)
		{
			if(!effect.isSaveable())
				return false;
		}
		return true;
	}

	public EffectUseType getUseType()
	{
		return _useType;
	}

	public boolean isOfUseType(EffectUseType useType)
	{
		return _useType == useType;
	}

	public boolean isOffensive()
	{
		if(isOfUseType(EffectUseType.SELF))
			return getSkill().isSelfOffensive();

		return getSkill().isOffensive();
	}

	public int getDuration()
	{
		return _duration;
	}

	public void setDuration(int value)
	{
		_duration = Math.min(Integer.MAX_VALUE, Math.max(0, value));
		_timeLeft = _duration;
	}

	public boolean isHideTime()
	{
		return getSkill().isAbnormalHideTime() || getDuration() == Integer.MAX_VALUE;
	}
}