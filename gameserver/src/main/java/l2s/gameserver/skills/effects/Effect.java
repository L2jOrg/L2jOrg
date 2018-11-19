package l2s.gameserver.skills.effects;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.skills.AbnormalType;
import l2s.gameserver.skills.EffectType;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.funcs.Func;
import l2s.gameserver.stats.funcs.FuncOwner;
import l2s.gameserver.templates.skill.EffectTemplate;

public class Effect implements FuncOwner
{
	private final Abnormal _abnormal;
	private final Env _env;
	private final EffectTemplate _template;

	public Effect(Abnormal abnormal, Env env, EffectTemplate template)
	{
		_abnormal = abnormal;
		_env = env;
		_template = template;
	}

	public Abnormal getAbnormal()
	{
		return _abnormal;
	}

	public int getTimeLeft()
	{
		return _abnormal == null ? 0 : _abnormal.getTimeLeft();
	}

	public int getDuration()
	{
		return _abnormal == null ? 0 : _abnormal.getDuration();
	}

	public Env getEnv()
	{
		return _env;
	}

	public final Skill getSkill()
	{
		return _env.skill;
	}

	public final Creature getEffector()
	{
		return _env.character;
	}

	public final Creature getEffected()
	{
		return _env.target;
	}

	public final boolean isReflected()
	{
		return _env.reflected;
	}

	public final EffectTemplate getTemplate()
	{
		return _template;
	}

	public final EffectType getEffectType()
	{
		return _template.getEffectType();
	}

	public double getValue()
	{
		return _template.getValue();
	}

	public int getInterval()
	{
		return _template.getInterval();
	}

	public boolean checkCondition()
	{
		return _template.checkCondition(this);
	}

	public boolean checkActingCondition()
	{
		return _template.checkCondition(this);
	}

	public void onStart()
	{}

	public boolean onActionTime()
	{
		return true;
	}

	public void onExit()
	{}

	public void instantUse()
	{
		onStart();
		onActionTime();
		onExit();
	}

	public boolean checkBlockedAbnormalType(AbnormalType abnormal)
	{
		return false;
	}

	public boolean checkDebuffImmunity()
	{
		return false;
	}

	public boolean isHidden()
	{
		return false;
	}

	public boolean isSaveable()
	{
		return true;
	}

	public Func[] getStatFuncs()
	{
		return _template.getStatFuncs(this);
	}

	@Override
	public boolean isFuncEnabled()
	{
		return true;
	}

	@Override
	public boolean overrideLimits()
	{
		return false;
	}
}