package handlers.effecthandlers;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.network.SystemMessageId;

import static org.l2j.gameserver.util.GameUtils.isSummon;

/**
 * Unsummon effect implementation.
 * @author Adry_85
 * @author JoeAlisson
 */
public final class Unsummon extends AbstractEffect {
    private final int power;

    private Unsummon(StatsSet params)
    {
        power = params.getInt("power", -1);
    }

    @Override
    public boolean calcSuccess(Creature effector, Creature effected, Skill skill) {
        if (power < 0) {
            return true;
        }

        final int magicLevel = skill.getMagicLevel();
        if ((magicLevel <= 0) || ((effected.getLevel() - 9) <= magicLevel))
        {
            final double chance = this.power * Formulas.calcAttributeBonus(effector, effected, skill) * Formulas.calcGeneralTraitBonus(effector, effected, skill.getTrait(), false);
            return Rnd.chance(chance);
        }

        return false;
    }

    @Override
    public boolean canStart(Creature effector, Creature effected, Skill skill)
    {
        return isSummon(effected);
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (effected.isServitor()) {
            final Summon servitor = (Summon) effected;
            final Player summonOwner = servitor.getOwner();

            servitor.abortAttack();
            servitor.abortCast();
            servitor.stopAllEffects();

            servitor.unSummon(summonOwner);
            summonOwner.sendPacket(SystemMessageId.YOUR_SERVITOR_HAS_VANISHED_YOU_LL_NEED_TO_SUMMON_A_NEW_ONE);
        }
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new Unsummon(data);
        }

        @Override
        public String effectName() {
            return "Unsummon";
        }
    }
}
