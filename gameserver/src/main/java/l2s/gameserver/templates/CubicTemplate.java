package l2s.gameserver.templates;

import gnu.trove.map.hash.TIntIntHashMap;

import l2s.commons.math.random.RndSelector;
import l2s.gameserver.model.Skill;

/**
 * @author VISTALL
 * @date  15:20/22.12.2010
 */
public class CubicTemplate
{
	public static class SkillInfo
	{
		private final Skill _skill;
		private final int _chance;
		private final ActionType _actionType;
		private final boolean _canAttackDoor;
		private final TIntIntHashMap _chanceList;

		public SkillInfo(Skill skill, int chance, ActionType actionType, boolean canAttackDoor, TIntIntHashMap set)
		{
			_skill = skill;
			_chance = chance;
			_actionType = actionType;
			_canAttackDoor = canAttackDoor;
			_chanceList = set;
		}

		public int getChance()
		{
			return _chance;
		}

		public ActionType getActionType()
		{
			return _actionType;
		}

		public Skill getSkill()
		{
			return _skill;
		}

		public boolean isCanAttackDoor()
		{
			return _canAttackDoor;
		}

		public int getChance(int a)
		{
			return _chanceList.get(a);
		}
	}

	public static enum ActionType
	{
		ATTACK,
		BUFF,
		DEBUFF,
		CANCEL,
		HEAL,
		MANA
	}

	public static enum UseUpType
	{
		INCREASE_DELAY,
		DISPELL
	}

	public static enum TargetType
	{
		TARGET,
		HEAL,
		BY_SKILL
	}

	private final int _id;
	private final int _level;
	private final int _slot;
	private final int _duration;
	private final int _delay;
	private final int _maxCount;
	private final UseUpType _useUp;
	private final double _power;
	private final TargetType _targetType;

	private RndSelector<SkillInfo> _skills = new RndSelector<SkillInfo>(true);

	public CubicTemplate(int id, int level, int slot, int duration, int delay, int maxCount, UseUpType useUp, double power, TargetType targetType)
	{
		_id = id;
		_level = level;
		_slot = slot;
		_duration = duration;
		_delay = delay;
		_maxCount = maxCount;
		_useUp = useUp;
		_power = power;
		_targetType = targetType;
	}

	public void putSkill(SkillInfo skill, int chance)
	{
		_skills.add(skill, chance);
	}

	public SkillInfo getRandomSkill()
	{
		return _skills.chance();
	}

	public int getDuration()
	{
		return _duration;
	}

	public int getDelay()
	{
		return _delay;
	}

	public int getId()
	{
		return _id;
	}

	public int getLevel()
	{
		return _level;
	}

	public int getSlot()
	{
		return _slot;
	}

	public int getMaxCount()
	{
		return _maxCount;
	}

	public UseUpType getUseUp()
	{
		return _useUp;
	}

	public double getPower()
	{
		return _power;
	}

	public TargetType getTargetType()
	{
		return _targetType;
	}
}