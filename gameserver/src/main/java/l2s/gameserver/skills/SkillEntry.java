package l2s.gameserver.skills;

import java.util.List;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.stats.funcs.Func;

/**
 * @author VISTALL
 * @date 0:15/03.06.2011
 */
public class SkillEntry
{
	public static final SkillEntry[] EMPTY_ARRAY = new SkillEntry[0];

	private final SkillEntryType _entryType;
	private final Skill _skill;

	private boolean _disabled;

	public SkillEntry(SkillEntryType key, Skill value)
	{
		_entryType = key;
		_skill = value;
	}

	public boolean isDisabled()
	{
		return _disabled;
	}

	public void setDisabled(boolean disabled)
	{
		_disabled = disabled;
	}

	public SkillEntryType getEntryType()
	{
		return _entryType;
	}

	public Skill getTemplate()
	{
		return _skill;
	}

	public int getId()
	{
		return _skill.getId();
	}

	public int getDisplayId()
	{
		return _skill.getDisplayId();
	}

	public int getLevel()
	{
		return _skill.getLevel();
	}

	public int getDisplayLevel()
	{
		return _skill.getDisplayLevel();
	}

	public Skill.SkillType getSkillType()
	{
		return _skill.getSkillType();
	}

	public String getName()
	{
		return _skill.getName();
	}

	public String getName(Player player)
	{
		return _skill.getName(player);
	}

	public final boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		return _skill.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	public final boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first, boolean sendMsg, boolean trigger)
	{
		return _skill.checkCondition(activeChar, target, forceUse, dontMove, first, sendMsg, trigger);
	}

	public final SystemMsg checkTarget(Creature activeChar, Creature target, Creature aimingTarget, boolean forceUse, boolean first)
	{
		return _skill.checkTarget(activeChar, target, aimingTarget, forceUse, first);
	}

	public final SystemMsg checkTarget(Creature activeChar, Creature target, Creature aimingTarget, boolean forceUse, boolean first, boolean trigger)
	{
		return _skill.checkTarget(activeChar, target, aimingTarget, forceUse, first, trigger);
	}

	public final boolean getEffects(Creature effector, Creature effected)
	{
		return _skill.getEffects(effector, effected);
	}

	public final boolean getEffects(Creature effector, Creature effected, int timeConst, double timeMult)
	{
		return _skill.getEffects(effector, effected, timeConst, timeMult);
	}

	public final void onEndCast(Creature activeChar, List<Creature> targets)
	{
		_skill.onEndCast(activeChar, targets);
	}

	public SkillEntry copyTo(SkillEntryType entryType)
	{
		return new SkillEntry(entryType, _skill);
	}

	public Func[] getStatFuncs()
	{
		return _skill.getStatFuncs();
	}

	public Skill getLockedSkill()
	{
		return null;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		return hashCode() == obj.hashCode();
	}

	@Override
	public int hashCode()
	{
		return _skill.hashCode();
	}

	@Override
	public String toString()
	{
		return _skill.toString();
	}
}