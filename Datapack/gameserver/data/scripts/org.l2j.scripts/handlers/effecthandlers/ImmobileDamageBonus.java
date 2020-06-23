package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.stats.Stat;

public class ImmobileDamageBonus extends AbstractEffect {
    public ImmobileDamageBonus()

    {
    }

    public static class Factory implements SkillEffectFactory {
        private static final ImmobileDamageBonus INSTANCE = new ImmobileDamageBonus();

        @Override
        public AbstractEffect create(StatsSet data) {
            return INSTANCE;
        }

        @Override
        public String effectName() {
            return "ImmobileDamageBonus";
        }
    }

}
