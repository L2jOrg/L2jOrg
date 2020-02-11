package handlers.skillconditionhandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.enums.SkillConditionAffectType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.w3c.dom.Node;

import static org.l2j.gameserver.util.GameUtils.isCreature;

/**
 * @author JoeAlisson
 */
public class RemainStatusSkillCondition implements SkillCondition {

    StatusType stat;
    boolean lower;
    int amount;
    SkillConditionAffectType affect;

    public RemainStatusSkillCondition(StatusType stat, int amount, boolean lower, SkillConditionAffectType affect) {
        this.stat = stat;
        this.amount = amount;
        this.lower = lower;
        this.affect = affect;
    }

    @Override
    public boolean canUse(Creature caster, Skill skill, WorldObject target) {
        if(affect == SkillConditionAffectType.TARGET && !isCreature(target)) {
            return false;
        }
        var creature = affect == SkillConditionAffectType.CASTER ? caster:  (Creature) target;

        return switch (stat) {
            case CP -> lower == creature.getCurrentCpPercent() <= amount;
            case HP -> lower == creature.getCurrentHpPercent() <= amount;
            case MP -> lower == creature.getCurrentMpPercent() <= amount;
        };
    }

    private enum StatusType {
        CP,
        HP,
        MP
    }

    public static final class Factory extends SkillConditionFactory {

        @Override
        public SkillCondition create(Node xmlNode) {
            var attr = xmlNode.getAttributes();
            return new RemainStatusSkillCondition(parseEnum(attr, StatusType.class, "stat"), parseInt(attr, "amount"), parseBoolean(attr,"lower"), parseEnum(attr, SkillConditionAffectType.class, "affect"));
        }

        @Override
        public String conditionName() {
            return "remain-status";
        }
    }

}
