package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;
import static org.l2j.gameserver.util.GameUtils.isDoor;

/**
 * Cp Heal Percent effect implementation.
 * @author UnAfraid
 * @author JoeAlisson
 */
public final class CpHealPercent extends AbstractEffect {
    private final double power;

    private CpHealPercent(StatsSet params)
    {
        power = params.getDouble("power", 0);
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (effected.isDead() || isDoor(effected) || effected.isHpBlocked()) {
            return;
        }

        double amount;
        final boolean full = power == 100;

        amount = full ? effected.getMaxCp() : (effected.getMaxCp() * power) / 100.0;
        if (nonNull(item) && (item.isPotion() || item.isElixir()))
        {
            amount += effected.getStats().getValue(Stat.ADDITIONAL_POTION_CP, 0);
        }
        // Prevents overheal and negative amount
        amount = Math.max(Math.min(amount, effected.getMaxRecoverableCp() - effected.getCurrentCp()), 0);
        if (amount != 0) {
            effected.setCurrentCp(amount + effected.getCurrentCp(), false);
            effected.broadcastStatusUpdate(effector);
        }

        SystemMessage sm;
        if (nonNull(effector) && (effector != effected)) {
            sm = getSystemMessage(SystemMessageId.S2_CP_HAS_BEEN_RESTORED_BY_C1).addString(effector.getName());
        } else {
            sm = getSystemMessage(SystemMessageId.S1_CP_HAS_BEEN_RESTORED);
        }
        effected.sendPacket(sm.addInt((int) amount));
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new CpHealPercent(data);
        }

        @Override
        public String effectName() {
            return "CpHealPercent";
        }
    }
}
