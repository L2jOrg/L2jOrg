package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.engine.skill.api.SkillType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.serverpackets.ExChangeMpCost;
import org.l2j.gameserver.util.MathUtil;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public class MagicMpCost extends AbstractEffect {
    private final SkillType magicType;
    private final double power;
    private final boolean anyType;

    private MagicMpCost(StatsSet params) {
        anyType = params.getBoolean("any-type");
        magicType = params.getEnum("type", SkillType.class);
        power = params.getDouble("power", 0);
    }

    @Override
    public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
        if (anyType) {
            SkillType.forEach(type -> onStart(effected, type));
        } else {
            onStart(effected, magicType);
        }
    }

    protected void onStart(Creature effected, SkillType type) {
        effected.getStats().mergeMpConsumeTypeValue(type, (power / 100) + 1, MathUtil::mul);
        effected.sendPacket(new ExChangeMpCost(power, type.ordinal()));
    }

    @Override
    public void onExit(Creature effector, Creature effected, Skill skill) {
        if(anyType) {
            SkillType.forEach(type -> onExit(effected, type));
        } else {
            onExit(effected, magicType);
        }
    }

    protected void onExit(Creature effected, SkillType type) {
        effected.getStats().mergeMpConsumeTypeValue(type, (power / 100) + 1, MathUtil::div);
        effected.sendPacket(new ExChangeMpCost(-power, type.ordinal()));
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new MagicMpCost(data);
        }

        @Override
        public String effectName() {
            return "magic-cost";
        }
    }
}
