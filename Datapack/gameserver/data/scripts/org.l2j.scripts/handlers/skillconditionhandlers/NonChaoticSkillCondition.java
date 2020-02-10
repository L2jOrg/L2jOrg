package handlers.skillconditionhandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.w3c.dom.Node;

/**
 * @author JoeAlisson
 */
public class NonChaoticSkillCondition implements SkillCondition {

    private NonChaoticSkillCondition() {
    }

    @Override
    public boolean canUse(Creature caster, Skill skill, WorldObject target) {
        return caster.getActingPlayer().getReputation() >= 0;
    }

    public static final class Factory extends SkillConditionFactory {
        private static final NonChaoticSkillCondition INSTANCE = new NonChaoticSkillCondition();

        @Override
        public SkillCondition create(Node xmlNode) {
            return INSTANCE;
        }

        @Override
        public String conditionName() {
            return "NonChaotic";
        }
    }
}
