package org.l2j.gameserver.skills.skillclasses;

import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.model.World;
import org.l2j.gameserver.model.instances.TrapInstance;
import org.l2j.gameserver.network.l2.s2c.NpcInfoPacket;
import org.l2j.gameserver.skills.AbnormalType;
import org.l2j.gameserver.templates.StatsSet;

public class DetectTrap extends Skill
{
	public DetectTrap(StatsSet set)
	{
		super(set);
	}

	@Override
	protected void useSkill(Creature activeChar, Creature target, boolean reflected)
	{
		if(target.isTrap())
		{
			TrapInstance trap = (TrapInstance) target;
			if(trap.getLevel() <= getPower())
			{
				trap.setDetected(true);
				for(Player player : World.getAroundObservers(trap))
					player.sendPacket(new NpcInfoPacket(trap, player).init());
			}
		}
		else if(isDetectPC())
			target.getAbnormalList().stop(AbnormalType.hide);
	}
}