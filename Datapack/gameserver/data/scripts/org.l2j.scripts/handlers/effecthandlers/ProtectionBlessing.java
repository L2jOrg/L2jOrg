package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.engine.skill.api.Skill;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Protection Blessing effect implementation.
 * @author kerberos_20
 * @author JoeAlisson
 */
public final class ProtectionBlessing extends AbstractEffect {
    private ProtectionBlessing() {
    }

    @Override
    public boolean canStart(Creature effector, Creature effected, Skill skill)
    {
        return nonNull(effector) && isPlayer(effected);
    }

    @Override
    public long getEffectFlags()
    {
        return EffectFlag.PROTECTION_BLESSING.getMask();
    }

    public static class Factory implements SkillEffectFactory {

        private static final ProtectionBlessing INSTANCE = new ProtectionBlessing();

        @Override
        public AbstractEffect create(StatsSet data) {
            return INSTANCE;
        }

        @Override
        public String effectName() {
            return "ProtectionBlessing";
        }
    }
}