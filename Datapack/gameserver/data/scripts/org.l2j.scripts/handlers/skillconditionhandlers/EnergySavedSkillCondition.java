package handlers.skillconditionhandlers;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.ISkillCondition;
import org.l2j.gameserver.model.skills.Skill;

/**
 * @author Sdw
 */
public class EnergySavedSkillCondition implements ISkillCondition {

	public final int charges;
	
	public EnergySavedSkillCondition(StatsSet params) {
		charges = params.getInt("amount");
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target)
	{
		return caster.getActingPlayer().getCharges() >= charges;
	}
}
