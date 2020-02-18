package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public class PhysicalShieldAngleAll extends AbstractEffect {
    private PhysicalShieldAngleAll() {
    }

    @Override
    public long getEffectFlags() {
        return EffectFlag.PHYSICAL_SHIELD_ANGLE_ALL.getMask();
    }

    public static class Factory implements SkillEffectFactory {

        private static final PhysicalShieldAngleAll INSTANCE = new PhysicalShieldAngleAll();

        @Override
        public AbstractEffect create(StatsSet data) {
            return INSTANCE;
        }

        @Override
        public String effectName() {
            return "PhysicalShieldAngleAll";
        }
    }

}
