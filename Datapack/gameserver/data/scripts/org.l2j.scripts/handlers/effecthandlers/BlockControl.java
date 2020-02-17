package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.effects.EffectType;

/**
 * An effect that blocks the player (NPC?) control. <br>
 * It prevents moving, casting, social actions, etc.
 * @author Nik
 * @author JoeAlisson
 */
public class BlockControl extends AbstractEffect {

    private BlockControl() {
    }

    @Override
    public long getEffectFlags()
    {
        return EffectFlag.BLOCK_CONTROL.getMask();
    }

    @Override
    public EffectType getEffectType()
    {
        return EffectType.BLOCK_CONTROL;
    }

    public static class Factory implements SkillEffectFactory {
        private static final BlockControl INSTANCE = new BlockControl();

        @Override
        public AbstractEffect create(StatsSet data) {
            return INSTANCE;
        }

        @Override
        public String effectName() {
            return "BlockControl";
        }
    }
}
