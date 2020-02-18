package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Enlarge Abnormal Slot effect implementation.
 * @author Zoey76
 * @author JoeAlisson
 */
public final class EnlargeAbnormalSlot extends AbstractEffect {
    private final int slots;

    private EnlargeAbnormalSlot(StatsSet params)
    {
        slots = params.getInt("power", 0);
    }

    @Override
    public boolean canStart(Creature effector, Creature effected, Skill skill)
    {
        return (effector != null) && isPlayer(effected);
    }

    @Override
    public void onStart(Creature effector, Creature effected, Skill skill, Item item)
    {
        effected.getStats().setMaxBuffCount(effected.getStats().getMaxBuffCount() + slots);
    }

    @Override
    public void onExit(Creature effector, Creature effected, Skill skill)
    {
        effected.getStats().setMaxBuffCount(Math.max(0, effected.getStats().getMaxBuffCount() - slots));
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new EnlargeAbnormalSlot(data);
        }

        @Override
        public String effectName() {
            return "EnlargeAbnormalSlot";
        }
    }
}
