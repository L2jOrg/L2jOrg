package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.stats.Formulas;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Magical Attack effect implementation.
 * @author Adry_85
 * @author JoeAlisson
 */
public final class MagicalAttackRange extends AbstractEffect {
    private final double power;
    private final double shieldDefPercent;

    private MagicalAttackRange(StatsSet params) {
        power = params.getDouble("power");
        shieldDefPercent = params.getDouble("shield-defense", 0);
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
        if (isPlayer(effected) && effected.getActingPlayer().isFakeDeath()) {
            effected.stopFakeDeath(true);
        }

        double mDef = effected.getMDef();
        switch (Formulas.calcShldUse(effector, effected)) {
            case Formulas.SHIELD_DEFENSE_SUCCEED -> mDef += ((effected.getShldDef() * shieldDefPercent) / 100);
            case Formulas.SHIELD_DEFENSE_PERFECT_BLOCK -> mDef = -1;
        }

        double damage = 1;
        final boolean mcrit = Formulas.calcCrit(skill.getMagicCriticalRate(), effector, effected, skill);

        if (mDef != -1) {
            damage = Formulas.calcMagicDam(effector, effected, skill, effector.getMAtk(), power, mDef, mcrit);
        }

        effector.doAttack(damage, effected, skill, false, false, mcrit, false);
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new MagicalAttackRange(data);
        }

        @Override
        public String effectName() {
            return "magical-attack-range";
        }
    }
}