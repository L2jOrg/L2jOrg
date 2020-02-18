package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;

/**
 * Dispel All effect implementation.
 * @author UnAfraid
 * @author JoeAlisson
 */
public final class DispelAll extends AbstractEffect {

    private DispelAll() {
    }

    @Override
    public EffectType getEffectType()
    {
        return EffectType.DISPEL;
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item)
    {
        effected.stopAllEffects();
    }

    public static class Factory implements SkillEffectFactory {
        private static final DispelAll INSTANCE = new DispelAll();
        @Override
        public AbstractEffect create(StatsSet data) {
            return INSTANCE;
        }

        @Override
        public String effectName() {
            return "DispelAll";
        }
    }
}
