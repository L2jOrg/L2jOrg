package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;

import static org.l2j.gameserver.util.GameUtils.isAttackable;

/**
 * Passive effect implementation.
 * @author Adry_85
 * @author JoeAlisson
 */
public final class Passive extends AbstractEffect {
    private Passive() {
    }

    @Override
    public long getEffectFlags()
    {
        return EffectFlag.PASSIVE.getMask();
    }


    @Override
    public boolean canStart(Creature effector, Creature effected, Skill skill)
    {
        return isAttackable(effected);
    }

    public static class Factory implements SkillEffectFactory {
        private static final Passive INSTANCE = new Passive();
        @Override
        public AbstractEffect create(StatsSet data) {
            return INSTANCE;
        }

        @Override
        public String effectName() {
            return "Passive";
        }
    }
}
