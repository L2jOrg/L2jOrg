package handlers.skillconditionhandlers;

import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.world.zone.ZoneType;
import org.w3c.dom.Node;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class NotInUnderwaterSkillCondition implements SkillCondition {

	private NotInUnderwaterSkillCondition(){
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target)
	{
		return !caster.isInsideZone(ZoneType.WATER);
	}

	public static final class Factory extends SkillConditionFactory {
		private static final NotInUnderwaterSkillCondition INSTANCE = new NotInUnderwaterSkillCondition();
		@Override
		public SkillCondition create(Node xmlNode) {
			return INSTANCE;
		}

		@Override
		public String conditionName() {
			return "NotInUnderwater";
		}
	}
}
