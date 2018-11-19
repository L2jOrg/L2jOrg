package l2s.gameserver.skills.skillclasses;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.templates.StatsSet;

public class PcBangPointsAdd extends Skill
{
	public PcBangPointsAdd(StatsSet set)
	{
		super(set);
	}

	@Override
	protected void useSkill(Creature activeChar, Creature target, boolean reflected)
	{
		if(!target.isPlayer())
			return;

		target.getPlayer().addPcBangPoints((int) getPower(), false, true);
	}
}