package handlers.skillconditionhandlers;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.ISkillCondition;
import org.l2j.gameserver.model.skills.Skill;

/**
 * @author Sdw
 */
public class CannotUseInTransformSkillCondition implements ISkillCondition {

	public final int id;
	
	public CannotUseInTransformSkillCondition(StatsSet params) {
		id = params.getInt("transformId", -1);
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target) {
		return id > 0 ? caster.getTransformationId() != id : !caster.isTransformed();
	}
}