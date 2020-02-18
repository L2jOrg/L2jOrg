package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.util.GameUtils;

/**
 * Transfer Hate effect implementation.
 * @author Adry_85
 * @author JoeAlisson
 */
public final class TransferHate extends AbstractEffect {
    private final int power;

    private TransferHate(StatsSet params)
    {
        power = params.getInt("power", 100);
    }

    @Override
    public boolean calcSuccess(Creature effector, Creature effected, Skill skill)
    {
        return Formulas.calcProbability(power, effector, effected, skill);
    }

    @Override
    public boolean canStart(Creature effector, Creature effected, Skill skill)
    {
        return GameUtils.checkIfInRange(skill.getEffectRange(), effector, effected, true);
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        World.getInstance().forEachVisibleObjectInRange(effector, Attackable.class, skill.getAffectRange(), hater -> {
            if (hater.isDead()) {
                return;
            }
            final int hate = hater.getHating(effector);
            if (hate <= 0) {
                return;
            }

            hater.reduceHate(effector, -hate);
            hater.addDamageHate(effected, 0, hate);
        });
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new TransferHate(data);
        }

        @Override
        public String effectName() {
            return "TransferHate";
        }
    }
}
