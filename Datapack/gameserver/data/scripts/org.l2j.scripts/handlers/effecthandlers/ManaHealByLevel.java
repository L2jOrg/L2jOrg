package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;
import static org.l2j.gameserver.util.GameUtils.isDoor;

/**
 * Mana Heal By Level effect implementation.
 * @author UnAfraid
 * @author JoeAlisson
 */
public final class ManaHealByLevel extends AbstractEffect {
    private final double power;

    private ManaHealByLevel(StatsSet params)
    {
        power = params.getDouble("power", 0);
    }

    @Override
    public EffectType getEffectType()
    {
        return EffectType.MANAHEAL_BY_LEVEL;
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (effected.isDead() || isDoor(effected) || effected.isMpBlocked()) {
            return;
        }

        if (effected != effector && effected.isAffected(EffectFlag.FACEOFF)) {
            return;
        }

        double amount = power;

        // recharged mp influenced by difference between target level and skill level
        // if target is within 5 levels or lower then skill level there's no penalty.
        amount = effected.getStats().getValue(Stat.MANA_CHARGE, amount);
        if (effected.getLevel() > skill.getMagicLevel()) {
            final int lvlDiff = effected.getLevel() - skill.getMagicLevel();
            // if target is too high compared to skill level, the amount of recharged mp gradually decreases.
            if (lvlDiff == 6) {
                amount *= 0.9; // only 90% effective
            }
            else if (lvlDiff == 7) {
                amount *= 0.8; // 80%
            }
            else if (lvlDiff == 8) {
                amount *= 0.7; // 70%
            }
            else if (lvlDiff == 9) {
                amount *= 0.6; // 60%
            }
            else if (lvlDiff == 10) {
                amount *= 0.5; // 50%
            }
            else if (lvlDiff == 11) {
                amount *= 0.4; // 40%
            }
            else if (lvlDiff == 12) {
                amount *= 0.3; // 30%
            }
            else if (lvlDiff == 13) {
                amount *= 0.2; // 20%
            }
            else if (lvlDiff == 14) {
                amount *= 0.1; // 10%
            }
            else if (lvlDiff >= 15) {
                amount = 0; // 0mp recharged
            }
        }

        // Prevents overheal and negative amount
        amount = Math.max(Math.min(amount, effected.getMaxRecoverableMp() - effected.getCurrentMp()), 0);
        if (amount != 0) {
            effected.setCurrentMp(amount + effected.getCurrentMp(), false);
            effected.broadcastStatusUpdate(effector);
        }

        final SystemMessage sm = getSystemMessage(effector.getObjectId() != effected.getObjectId() ? SystemMessageId.S2_MP_HAS_BEEN_RESTORED_BY_C1 : SystemMessageId.S1_MP_HAS_BEEN_RESTORED);
        if (effector.getObjectId() != effected.getObjectId()) {
            sm.addString(effector.getName());
        }
        effected.sendPacket(sm.addInt((int) amount));
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new ManaHealByLevel(data);
        }

        @Override
        public String effectName() {
            return "ManaHealByLevel";
        }
    }
}
