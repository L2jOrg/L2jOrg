package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public class ImmobileDamageResist extends AbstractEffect {
    public ImmobileDamageResist()

    {
    }

    public static class Factory implements SkillEffectFactory {
        private static final ImmobileDamageResist INSTANCE = new ImmobileDamageResist();

        @Override
        public AbstractEffect create(StatsSet data) {
            return INSTANCE;
        }

        @Override
        public String effectName() {
            return "ImmobileDamageResist";
        }
    }

}
