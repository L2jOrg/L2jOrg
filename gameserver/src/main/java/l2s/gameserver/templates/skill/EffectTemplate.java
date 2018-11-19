package l2s.gameserver.templates.skill;

import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.skills.EffectType;
import l2s.gameserver.skills.EffectUseType;
import l2s.gameserver.skills.effects.Effect;
import l2s.gameserver.skills.effects.i_abstract_effect;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.StatTemplate;
import l2s.gameserver.stats.conditions.Condition;
import l2s.gameserver.templates.StatsSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EffectTemplate extends StatTemplate
{
	private static final Logger _log = LoggerFactory.getLogger(EffectTemplate.class);

	public static final EffectTemplate[] EMPTY_ARRAY = new EffectTemplate[0];

	private Condition _attachCond;
	private final double _value;

	public final EffectType _effectType;

	private final StatsSet _paramSet;
	private final int _chance;

	private final int _interval;

	private final EffectUseType _useType;

	public EffectTemplate(final StatsSet set, EffectUseType useType)
	{
		_effectType = set.getEnum("name", EffectType.class, EffectType.Buff);

		final boolean instant = set.getBool("instant", _effectType.getEffectClass() == i_abstract_effect.class || i_abstract_effect.class.isAssignableFrom(_effectType.getEffectClass()));
		if(instant)
		{
			switch(useType)
			{
				case START:
					useType = EffectUseType.START_INSTANT;
					break;
				case TICK:
					useType = EffectUseType.TICK_INSTANT;
					break;
				case SELF:
					useType = EffectUseType.SELF_INSTANT;
					break;
				case NORMAL:
					useType = EffectUseType.NORMAL_INSTANT;
					break;
			}
		}

		_useType = useType;

		_value = set.getDouble("value", 0D);

		_interval = set.getInteger("interval", Integer.MAX_VALUE);
		_chance = set.getInteger("chance", -1);
		_paramSet = set;
	}

	public Effect getEffect(Abnormal abnormal, Env env)
	{
		try
		{
			return _effectType.makeEffect(abnormal, env, this);
		}
		catch(Exception e)
		{
			_log.error("", e);
		}

		return null;
	}

	public void attachCond(Condition c)
	{
		_attachCond = c;
	}

	public boolean checkCondition(Effect effect)
	{
		return _attachCond == null || _attachCond.test(effect.getEnv());
	}

	public Condition getCondition()
	{
		return _attachCond;
	}

	public EffectType getEffectType()
	{
		return _effectType;
	}

	public StatsSet getParam()
	{
		return _paramSet;
	}

	public int getChance()
	{
		return _chance;
	}

	public int getInterval()
	{
		return _interval;
	}

	public EffectUseType getUseType()
	{
		return _useType;
	}

	public boolean isInstant()
	{
		return _useType.isInstant();
	}

	public final double getValue()
	{
		return _value;
	}
}