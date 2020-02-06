package handlers.skillconditionhandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.ISkillCondition;
import org.l2j.gameserver.model.skills.Skill;

import static org.l2j.gameserver.util.GameUtils.isCreature;

/**
 * @author Sdw
 */
public class OpSkillAcquireSkillCondition implements ISkillCondition {

	public final int skillId;
	public final boolean hasLearned;
	
	public OpSkillAcquireSkillCondition(StatsSet params) {
		skillId = params.getInt("skillId");
		hasLearned = params.getBoolean("hasLearned");
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target) {
		if (!isCreature(target)) {
			return false;
		}
		final int skillLevel = ((Creature) target).getSkillLevel(skillId);
		return hasLearned == (skillLevel != 0);
	}
}
