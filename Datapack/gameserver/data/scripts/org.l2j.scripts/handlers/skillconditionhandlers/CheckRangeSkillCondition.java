package handlers.skillconditionhandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.util.MathUtil;
import org.w3c.dom.Node;

import static java.util.Objects.isNull;

/**
 * @author JoeAlisson
 */
public class CheckRangeSkillCondition implements SkillCondition {

    private final int min;
    private final int max;

    public CheckRangeSkillCondition(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean canUse(Creature caster, Skill skill, WorldObject target) {
        if(isNull(target)) {
            return false;
        }
        var radius = caster.getCollisionRadius() + ( target instanceof Creature creature ? creature.getCollisionRadius() : 0);
        final var distance = MathUtil.calculateDistanceSq3D(caster, target) - radius;
        return distance >= min * min && (max <= 0 || distance <= max * max);
    }

    public static final class Factory extends SkillConditionFactory {

        @Override
        public SkillCondition create(Node xmlNode) {
            var attr = xmlNode.getAttributes();
            return new CheckRangeSkillCondition(parseInt(attr, "min"), parseInt(attr,"max"));
        }

        @Override
        public String conditionName() {
            return "check-range";
        }
    }
}

