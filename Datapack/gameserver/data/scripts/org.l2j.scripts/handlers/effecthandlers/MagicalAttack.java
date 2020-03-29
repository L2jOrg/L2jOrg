package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.stats.Formulas;

import static org.l2j.gameserver.util.GameUtils.isAttackable;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Magical Attack effect implementation.
 * @author Adry_85
 * @author JoeAlisson
 */
public final class MagicalAttack extends AbstractEffect {
    private final double power;
    private final boolean overHit;

    private MagicalAttack(StatsSet params) {
        power = params.getDouble("power", 0);
        overHit = params.getBoolean("over-hit", false);
    }

    @Override
    public EffectType getEffectType()
    {
        return EffectType.MAGICAL_ATTACK;
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (effector.isAlikeDead()) {
            return;
        }

        if (isPlayer(effected) && effected.getActingPlayer().isFakeDeath()) {
            effected.stopFakeDeath(true);
        }

        if (overHit && isAttackable(effected)) {
            ((Attackable) effected).overhitEnabled(true);
        }

        final boolean mcrit = Formulas.calcCrit(skill.getMagicCriticalRate(), effector, effected, skill);
        double damage = Formulas.calcMagicDam(effector, effected, skill, effector.getMAtk(), power, effected.getMDef(), mcrit);

        effector.doAttack(damage, effected, skill, false, false, mcrit, false);
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new MagicalAttack(data);
        }

        @Override
        public String effectName() {
            return "magical-attack";
        }
    }
}