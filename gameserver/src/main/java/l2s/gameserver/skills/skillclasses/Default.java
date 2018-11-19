package l2s.gameserver.skills.skillclasses;

import java.util.List;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.templates.StatsSet;

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
			activeChar.sendMessage(new CustomMessage("l2s.gameserver.skills.skillclasses.Default.NotImplemented").addNumber(getId()).addString("" + getSkillType()));

		_log.warn("NOTDONE skill: " + getId() + ", used by" + activeChar);
	}
}