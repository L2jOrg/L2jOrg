package handlers.skillconditionhandlers;

import io.github.joealisson.primitive.IntSet;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.w3c.dom.Node;

import static org.l2j.gameserver.util.GameUtils.isDoor;

/**
 * @author Mobius
 * @author JoeAlisson
 */
public class OpTargetDoorSkillCondition implements SkillCondition {

	public final IntSet doorIds;

	protected OpTargetDoorSkillCondition(IntSet doors) {
		this.doorIds = doors;
	}

	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target) {
		return isDoor(target) && doorIds.contains(target.getId());
	}

}
