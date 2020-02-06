package handlers.skillconditionhandlers;

import org.l2j.gameserver.enums.Position;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.ISkillCondition;
import org.l2j.gameserver.engine.skill.api.Skill;

import static org.l2j.gameserver.util.MathUtil.convertHeadingToDegree;

/**
 * @author Sdw
 */
public class OpBlinkSkillCondition implements ISkillCondition {
	private final int angle;
	private final int range;
	
	public OpBlinkSkillCondition(StatsSet params) {
		angle = switch (params.getEnum("direction", Position.class)) {
			case BACK -> 0;
			case FRONT -> 180;
			default -> -1;
		};
		
		range = params.getInt("range");
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target) {

		final double angle = convertHeadingToDegree(caster.getHeading());
		final double radian = Math.toRadians(angle);
		final double course = Math.toRadians(this.angle);
		final int x1 = (int) (Math.cos(Math.PI + radian + course) * range);
		final int y1 = (int) (Math.sin(Math.PI + radian + course) * range);
		
		final int x = caster.getX() + x1;
		final int y = caster.getY() + y1;
		final int z = caster.getZ();
		
		return GeoEngine.getInstance().canMoveToTarget(caster.getX(), caster.getY(), caster.getZ(), x, y, z, caster.getInstanceWorld());
	}
}
