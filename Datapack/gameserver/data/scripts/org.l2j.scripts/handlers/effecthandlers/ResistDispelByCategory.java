package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.enums.DispelSlotType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public class ResistDispelByCategory extends AbstractEffect {
    private final DispelSlotType slot;
    private final double power;

    private ResistDispelByCategory(StatsSet params) {
        power = params.getDouble("power", 0);
        slot = params.getEnum("category", DispelSlotType.class, DispelSlotType.BUFF);
    }

    @Override
    public void pump(Creature effected, Skill skill) {
        // Only this one is in use it seems
        if (slot == DispelSlotType.BUFF) {
            effected.getStats().mergeMul(Stat.RESIST_DISPEL_BUFF, 1 + (power / 100));
        }
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new ResistDispelByCategory(data);
        }

        @Override
        public String effectName() {
            return "resist-dispel-by-category";
        }
    }
}
