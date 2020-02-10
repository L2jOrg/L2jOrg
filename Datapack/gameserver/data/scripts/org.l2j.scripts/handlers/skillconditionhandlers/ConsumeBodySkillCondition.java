package handlers.skillconditionhandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.SystemMessageId;
import org.w3c.dom.Node;

import static org.l2j.gameserver.util.GameUtils.*;

/**
 * @author UnAfraid
 */
public class ConsumeBodySkillCondition implements SkillCondition {

	private ConsumeBodySkillCondition() {
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target) {
		if ((isMonster(target) || isSummon(target)))
		{
			final Creature character = (Creature) target;
			if (character.isDead() && character.isSpawned())
			{
				return true;
			}
		}
		
		if (isPlayer(caster))
		{
			caster.sendPacket(SystemMessageId.INVALID_TARGET);
		}
		return false;
	}

	public static final class Factory extends SkillConditionFactory {

		private static final ConsumeBodySkillCondition INSTANCE = new ConsumeBodySkillCondition();
		@Override
		public SkillCondition create(Node xmlNode) {
			return INSTANCE;
		}

		@Override
		public String conditionName() {
			return "ConsumeBody";
		}
	}
}
