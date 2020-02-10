package handlers.skillconditionhandlers;

import org.l2j.gameserver.enums.SkillConditionAffectType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.Skill;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class OpPkcountSkillCondition implements SkillCondition {

	private final SkillConditionAffectType affectType;
	
	public OpPkcountSkillCondition(StatsSet params)	{
		affectType = params.getEnum("affectType", SkillConditionAffectType.class);
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target) {
		return switch (affectType) {
			case CASTER -> isPlayer(caster) && (caster.getActingPlayer().getPkKills() > 0);
			case TARGET -> isPlayer(target) && target.getActingPlayer().getPkKills() > 0;
			default -> false;
		};
	}
}
