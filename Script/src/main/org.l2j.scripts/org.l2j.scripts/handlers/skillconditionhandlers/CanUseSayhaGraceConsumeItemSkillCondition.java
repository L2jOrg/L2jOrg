package org.l2j.scripts.handlers.skillconditionhandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.w3c.dom.Node;

public class CanUseSayhaGraceConsumeItemSkillCondition implements SkillCondition {

    @Override
    public boolean canUse(Creature caster, Skill skill, WorldObject target) {
        return true;
    }
    public static final class Factory extends SkillConditionFactory {
        private static final CanUseSayhaGraceConsumeItemSkillCondition INSTANCE = new CanUseSayhaGraceConsumeItemSkillCondition();

        @Override
        public SkillCondition create(Node xmlNode) {
            return INSTANCE;
        }

        @Override
        public String conditionName() {
            return "CanUseSayhaGraceConsumeItem";
        }
    }
}
