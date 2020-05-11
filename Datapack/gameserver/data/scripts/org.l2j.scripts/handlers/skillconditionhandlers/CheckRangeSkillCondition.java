package handlers.skillconditionhandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.w3c.dom.Node;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.util.MathUtil.calculateDistanceSq2D;
import static org.l2j.gameserver.util.MathUtil.calculateDistanceSq3D;

/**
 * @author JoeAlisson
 */
public class CheckRangeSkillCondition implements SkillCondition {

    private final int min;
    private final int max;
    private final boolean check3D;

    public CheckRangeSkillCondition(int min, int max, boolean check3D) {
        this.min = min * min;
        this.max = max > 0 ?  max * max : max;
        this.check3D = check3D;
    }

    @Override
    public boolean canUse(Creature caster, Skill skill, WorldObject target) {
        if(isNull(target)) {
            return false;
        }
        final var radius = caster.getCollisionRadius() + ( target instanceof Creature creature ? creature.getCollisionRadius() : 0);
        final var distance = (check3D ? calculateDistanceSq3D(caster, target) : calculateDistanceSq2D(caster, target) ) - (radius * radius);
        return distance >= min && (max <= 0 || distance <= max);
    }

    public static final class Factory extends SkillConditionFactory {

        @Override
        public SkillCondition create(Node xmlNode) {
            var attr = xmlNode.getAttributes();
            return new CheckRangeSkillCondition(parseInt(attr, "min"), parseInt(attr,"max"), parseBoolean(attr, "check-3d"));
        }

        @Override
        public String conditionName() {
            return "check-range";
        }
    }
}

