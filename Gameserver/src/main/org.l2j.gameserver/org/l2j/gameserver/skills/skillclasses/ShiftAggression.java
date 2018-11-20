package org.l2j.gameserver.skills.skillclasses;

import org.l2j.gameserver.model.AggroList.AggroInfo;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.model.World;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.templates.StatsSet;

public class ShiftAggression extends Skill
{
	public ShiftAggression(StatsSet set)
	{
		super(set);
	}

	@Override
	protected void useSkill(Creature activeChar, Creature target, boolean reflected)
	{
		if(activeChar.getPlayer() == null)
			return;

		if(!target.isPlayer())
			return;

		for(NpcInstance npc : World.getAroundNpc(activeChar, getAffectRange(), getAffectRange()))
		{
			AggroInfo ai = npc.getAggroList().get(activeChar);
			if(ai == null)
				continue;

			npc.getAggroList().addDamageHate(target.getPlayer(), 0, ai.hate);
			npc.getAggroList().remove(activeChar, true);
		}
	}
}