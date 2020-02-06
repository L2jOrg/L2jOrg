package handlers.skillconditionhandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.ISkillCondition;
import org.l2j.gameserver.engine.skill.api.Skill;

import java.util.List;

import static org.l2j.gameserver.util.GameUtils.isDoor;

/**
 * @author Mobius
 */
public class OpTargetDoorSkillCondition implements ISkillCondition {

	public final List<Integer> doorIds;
	
	public OpTargetDoorSkillCondition(StatsSet params) {
		doorIds = params.getList("doorIds", Integer.class);
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target) {
		return isDoor(target) && doorIds.contains(target.getId());
	}
}
