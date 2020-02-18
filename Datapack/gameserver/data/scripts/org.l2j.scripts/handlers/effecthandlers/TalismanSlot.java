package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Talisman Slot effect implementation.
 * @author Adry_85
 * @author JoeAlisson
 */
public final class TalismanSlot extends AbstractEffect {
    private final int power;

    private TalismanSlot(StatsSet params)
    {
        power = params.getInt("power", 0);
    }

    @Override
    public boolean canStart(Creature effector, Creature effected, Skill skill)
    {
        return (effector != null) && isPlayer(effected);
    }

    @Override
    public void onStart(Creature effector, Creature effected, Skill skill, Item item)
    {
        effected.getActingPlayer().getStats().addTalismanSlots(power);
    }

    @Override
    public void onExit(Creature effector, Creature effected, Skill skill)
    {
        effected.getActingPlayer().getStats().addTalismanSlots(-power);
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new TalismanSlot(data);
        }

        @Override
        public String effectName() {
            return "TalismanSlot";
        }
    }
}
