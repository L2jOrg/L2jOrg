package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.stats.Formulas;

import static org.l2j.gameserver.util.GameUtils.isPlayable;

/**
 * Target Me Probability effect implementation.
 * @author Adry_85
 * @author JoeAlisson
 */
public final class TargetMeProbability extends AbstractEffect {
    private final int power;

    private TargetMeProbability(StatsSet params)
    {
        power = params.getInt("power", 100);
    }

    @Override
    public boolean calcSuccess(Creature effector, Creature effected, Skill skill)
    {
        return Formulas.calcProbability(power, effector, effected, skill);
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (isPlayable(effected)) {
            if (effected.getTarget() != effector) {
                effected.setTarget(effector);
            }
        }
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new TargetMeProbability(data);
        }

        @Override
        public String effectName() {
            return "TargetMeProbability";
        }
    }
}
