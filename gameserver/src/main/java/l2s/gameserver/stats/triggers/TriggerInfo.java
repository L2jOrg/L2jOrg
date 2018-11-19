package l2s.gameserver.stats.triggers;

import l2s.commons.lang.ArrayUtils;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.skills.SkillEntry;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.conditions.Condition;

/**
 * @author VISTALL
 * @date 15:03/22.01.2011
 */
public class TriggerInfo extends Skill.AddedSkill
{
	private final TriggerType _type;
	private final double _chance;
	private final boolean _increasing;
	private final int _delay;
	private final boolean _cancelEffectsOnRemove;
	private final String _args;
	private Condition[] _conditions = Condition.EMPTY_ARRAY;

	public TriggerInfo(int id, int level, TriggerType type, double chance, boolean increasing, int delay, boolean cancel, String args)
	{
		super(id, level);
		_type = type;
		_chance = chance;
		_increasing = increasing;
		_delay = delay;
		_cancelEffectsOnRemove = cancel;
		_args = args;
	}

	public final void addCondition(Condition c)
	{
		_conditions = ArrayUtils.add(_conditions, c);
	}

	public boolean checkCondition(Creature actor, Creature target, Creature aimTarget, Skill owner, double damage)
	{
		SkillEntry skillEntry = getSkill();
		if(skillEntry == null)
			return false;

		// Скилл проверяется и кастуется на aimTarget
		if(skillEntry.checkTarget(actor, aimTarget, aimTarget, false, false, true) != null)
			return false;

		Env env = new Env();
		env.character = actor;
		env.skill = owner;
		env.target = target; // В условии проверяется реальная цель.
		env.value = damage;

		for(Condition c : _conditions)
		{
			if(!c.test(env))
				return false;
		}
		return true;
	}

	public TriggerType getType()
	{
		return _type;
	}

	public double getChance()
	{
		return _chance;
	}

	public boolean isIncreasing()
	{
		return _increasing;
	}

	public int getDelay()
	{
		return _delay;
	}

	public boolean cancelEffectsOnRemove()
	{
		return _cancelEffectsOnRemove;
	}

	public String getArgs()
	{
		return _args;
	}
}