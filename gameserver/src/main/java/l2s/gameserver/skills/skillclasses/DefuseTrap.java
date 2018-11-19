package l2s.gameserver.skills.skillclasses;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.TrapInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.templates.StatsSet;

public class DefuseTrap extends Skill
{
	public DefuseTrap(StatsSet set)
	{
		super(set);
	}

	@Override
	public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(!super.checkCondition(activeChar, target, forceUse, dontMove, first))
			return false;

		if(target == null || !target.isTrap())
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			return false;
		}

		return true;
	}

	@Override
	protected void useSkill(Creature activeChar, Creature target, boolean reflected)
	{
		if(target.isTrap())
		{
			TrapInstance trap = (TrapInstance) target;
			if(trap.getLevel() <= getPower())
				trap.deleteMe();
		}
	}
}