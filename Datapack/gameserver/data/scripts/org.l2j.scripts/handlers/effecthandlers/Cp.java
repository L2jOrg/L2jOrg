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

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;
import static org.l2j.gameserver.util.GameUtils.isDoor;

/**
 * CP change effect. It is mostly used for potions and static damage.
 * @author Nik
 * @author JoeAlisson
 */
public final class Cp extends AbstractEffect {

    private final int power;
    private final StatModifierType mode;

    private Cp(StatsSet params) {
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
        if (effected.isDead() || isDoor(effected) || effected.isHpBlocked()) {
            return;
        }

        int basicAmount = power;
        if ( nonNull(item) && (item.isPotion() || item.isElixir())) {
            basicAmount += effected.getStats().getValue(Stat.ADDITIONAL_POTION_CP, 0);
        }

        double amount = switch (mode) {
            case DIFF ->  Math.min(basicAmount, effected.getMaxRecoverableCp() - effected.getCurrentCp());
            case PER ->  Math.min((effected.getMaxCp() * basicAmount) / 100.0, effected.getMaxRecoverableCp() - effected.getCurrentCp());
        };

        if (amount != 0) {
            effected.setCurrentCp(amount + effected.getCurrentCp(), false);
            effected.broadcastStatusUpdate(effector);
        }

        if (amount >= 0) {
            SystemMessage sm;
            if (nonNull(effector) && (effector != effected)) {
                sm = getSystemMessage(SystemMessageId.S2_CP_HAS_BEEN_RESTORED_BY_C1).addString(effector.getName()).addInt((int) amount);
            } else {
                sm = getSystemMessage(SystemMessageId.S1_CP_HAS_BEEN_RESTORED);
            }
            effected.sendPacket(sm.addInt((int) amount));
        }
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new Cp(data);
        }

        @Override
        public String effectName() {
            return "cp";
        }
    }
}
