package org.l2j.gameserver.skills;

import org.l2j.gameserver.model.Skill;

public class LockedSkillEntry extends SkillEntry
{
	private final Skill _lockedSkill;

	public LockedSkillEntry(SkillEntryType key, Skill value, Skill locked)
	{
		super(key, value);

		_lockedSkill = locked;
	}

	@Override
	public Skill getLockedSkill()
	{
		return _lockedSkill;
	}
}