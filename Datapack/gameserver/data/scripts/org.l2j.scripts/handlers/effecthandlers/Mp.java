package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.enums.StatModifierType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;
import static org.l2j.gameserver.util.GameUtils.isDoor;

/**
 * MP change effect. It is mostly used for potions and static damage.
 * @author Nik
 * @author JoeAlisson
 */
public final class Mp extends AbstractEffect {
    private final int power;
    private final StatModifierType mode;

    private Mp(StatsSet params) {
        power = params.getInt("power", 0);
        mode = params.getEnum("mode", StatModifierType.class, StatModifierType.DIFF);
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

        int basicAmount = power;
        if ((item != null) && (item.isPotion() || item.isElixir())) {
            basicAmount += effected.getStats().getValue(Stat.ADDITIONAL_POTION_MP, 0);
        }

        double amount = switch (mode) {
            case DIFF ->  Math.min(basicAmount, effected.getMaxRecoverableMp() - effected.getCurrentMp());
            case PER -> Math.min((effected.getMaxMp() * basicAmount) / 100.0, effected.getMaxRecoverableMp() - effected.getCurrentMp());
        };

        if (amount >= 0) {
            if (amount != 0) {
                effected.setCurrentMp(amount + effected.getCurrentMp(), false);
                effected.broadcastStatusUpdate(effector);
            }

            SystemMessage sm;
            if (effector.getObjectId() != effected.getObjectId()) {
                sm = getSystemMessage(SystemMessageId.S2_MP_HAS_BEEN_RESTORED_BY_C1).addString(effector.getName());
            } else{
                sm = getSystemMessage(SystemMessageId.S1_MP_HAS_BEEN_RESTORED);
            }
            effected.sendPacket(sm.addInt((int) amount));
        } else {
            final double damage = -amount;
            effected.reduceCurrentMp(damage);
        }
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new Mp(data);
        }

        @Override
        public String effectName() {
            return "mp";
        }
    }
}
