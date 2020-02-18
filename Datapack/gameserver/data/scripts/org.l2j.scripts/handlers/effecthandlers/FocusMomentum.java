package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.EtcStatusUpdate;

import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Focus Energy effect implementation.
 *
 * @author DS
 * @author JoeAlisson
 */
public final class FocusMomentum extends AbstractEffect {
    private final int maxCharges;

    private FocusMomentum(StatsSet params) {
        maxCharges = params.getInt("power", 0);
    }

    @Override
    public boolean isInstant() {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (!isPlayer(effected)) {
            return;
        }

        final Player player = effected.getActingPlayer();
        final int currentCharges = player.getCharges();
        final int maxCharges = Math.min(this.maxCharges, (int) effected.getStats().getValue(Stat.MAX_MOMENTUM, 0));

        if (currentCharges >= maxCharges) {
            if (!skill.isTriggeredSkill()) {
                player.sendPacket(SystemMessageId.YOUR_FORCE_HAS_REACHED_MAXIMUM_CAPACITY);
            }
            return;
        }

        final int newCharge = Math.min(currentCharges + 1, maxCharges);

        player.setCharges(newCharge);

        if (newCharge == maxCharges) {
            player.sendPacket(SystemMessageId.YOUR_FORCE_HAS_REACHED_MAXIMUM_CAPACITY);
        } else {
            player.sendPacket(getSystemMessage(SystemMessageId.YOUR_FORCE_HAS_INCREASED_TO_LEVEL_S1).addInt(newCharge));
        }

        player.sendPacket(new EtcStatusUpdate(player));
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new FocusMomentum(data);
        }

        @Override
        public String effectName() {
            return "FocusMomentum";
        }
    }
}