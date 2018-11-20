package org.l2j.gameserver.skills.skillclasses;

import java.util.List;

import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.templates.StatsSet;

public class Disablers extends Skill
{
	private final boolean _skillInterrupt;

	public Disablers(StatsSet set)
	{
		super(set);
		_skillInterrupt = set.getBool("skillInterrupt", false);
	}

	@Override
	protected void useSkill(Creature activeChar, Creature target, boolean reflected)
	{
		final Creature realTarget = reflected ? activeChar : target;
		if(_skillInterrupt)
		{
			if(!realTarget.isRaid())
			{
				if(realTarget.getCastingSkill() != null && !realTarget.getCastingSkill().isMagic())
					realTarget.abortCast(false, true);

				realTarget.abortAttack(true, true);
			}
		}
	}
}