package org.l2j.gameserver.skills.skillclasses;

import java.util.List;

import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.network.l2.components.CustomMessage;
import org.l2j.gameserver.templates.StatsSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Default extends Skill
{
	private static final Logger _log = LoggerFactory.getLogger(Default.class);

	public Default(StatsSet set)
	{
		super(set);
	}

	@Override
	public void onEndCast(Creature activeChar, List<Creature> targets)
	{
		super.onEndCast(activeChar, targets);

		if(activeChar.isPlayer())
			activeChar.sendMessage(new CustomMessage("org.l2j.gameserver.skills.skillclasses.Default.NotImplemented").addNumber(getId()).addString("" + getSkillType()));

		_log.warn("NOTDONE skill: " + getId() + ", used by" + activeChar);
	}
}