package handlers.skillconditionhandlers;

import org.l2j.gameserver.enums.SkillConditionAffectType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.skills.ISkillCondition;
import org.l2j.gameserver.engine.skill.api.Skill;

import java.util.List;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author UnAfraid
 */
public class OpCheckClassListSkillCondition implements ISkillCondition {

	public final List<ClassId> classIds;
	public final SkillConditionAffectType affectType;
	public final boolean isWithin;
	
	public OpCheckClassListSkillCondition(StatsSet params) {
		classIds = params.getEnumList("classIds", ClassId.class);
		affectType = params.getEnum("affectType", SkillConditionAffectType.class);
		isWithin = params.getBoolean("isWithin");
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target) {
		return switch (affectType) {
			case CASTER -> isPlayer(caster) && isWithin == classIds.stream().anyMatch(classId -> classId == caster.getActingPlayer().getClassId());
			case TARGET -> isPlayer(target) && isWithin == classIds.stream().anyMatch(classId -> classId == target.getActingPlayer().getClassId());
			default ->  false;
		};
	}
}