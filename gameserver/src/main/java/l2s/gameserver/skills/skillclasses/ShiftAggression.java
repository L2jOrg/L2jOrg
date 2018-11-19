package l2s.gameserver.skills.skillclasses;

import l2s.gameserver.model.AggroList.AggroInfo;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.World;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.StatsSet;

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