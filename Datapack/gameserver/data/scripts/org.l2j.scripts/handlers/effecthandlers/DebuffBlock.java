package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;

/**
 * Effect that blocks all incoming debuffs.
 * @author Nik
 * @author JoeAlisson
 */
public final class DebuffBlock extends AbstractEffect {
    private DebuffBlock() {
    }

    @Override
    public long getEffectFlags()
    {
        return EffectFlag.DEBUFF_BLOCK.getMask();
    }

    public static class Factory implements SkillEffectFactory {
        private static final DebuffBlock INSTANCE = new DebuffBlock();

        @Override
        public AbstractEffect create(StatsSet data) {
            return INSTANCE ;
        }

        @Override
        public String effectName() {
            return "DebuffBlock";
        }
    }
}
