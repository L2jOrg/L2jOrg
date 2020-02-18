package handlers.effecthandlers;

import java.util.List;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.enums.DispelSlotType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.stats.Formulas;

/**
 * Dispel By Category effect implementation.
 * @author DS, Adry_85
 * @author JoeAlisson
 */
public final class DispelByCategory extends AbstractEffect {
    private final DispelSlotType category;
    private final int power;
    private final int max;

    private DispelByCategory(StatsSet params) {
        category = params.getEnum("category", DispelSlotType.class, DispelSlotType.BUFF);
        power = params.getInt("power", 0);
        max = params.getInt("max", 0);
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
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (effected.isDead()) {
            return;
        }

        final List<BuffInfo> canceled = Formulas.calcCancelStealEffects(effector, effected, skill, category, power, max);
        canceled.forEach(b -> effected.getEffectList().stopSkillEffects(true, b.getSkill()));
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new DispelByCategory(data);
        }

        @Override
        public String effectName() {
            return "dispel-by-category";
        }
    }
}