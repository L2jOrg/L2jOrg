package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public class VampiricAttack extends AbstractEffect {
    private final double amount;
    private final double sum;

    private VampiricAttack(StatsSet params) {
        amount = params.getDouble("power");
        sum = amount * params.getDouble("chance");
    }

    @Override
    public void pump(Creature effected, Skill skill) {
        effected.getStats().mergeAdd(Stat.ABSORB_DAMAGE_PERCENT, amount / 100);
        effected.getStats().addToVampiricSum(sum);
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new VampiricAttack(data);
        }

        @Override
        public String effectName() {
            return "vampiric-attack";
        }
    }
}
