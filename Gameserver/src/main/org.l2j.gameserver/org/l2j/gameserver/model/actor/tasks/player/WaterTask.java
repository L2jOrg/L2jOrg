package org.l2j.gameserver.model.actor.tasks.player;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.model.DamageInfo.DamageType;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;

/**
 * Task dedicated to make damage to the player while drowning.
 *
 * @author UnAfraid
 */
public class WaterTask implements Runnable {
    private final Player player;

    public WaterTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        if (nonNull(player)) {
            double reduceHp = Math.min(1, player.getMaxHp() / 100.0);
            player.reduceCurrentHp(reduceHp, null, null, false, true, false, false, DamageType.DROWN);
            player.sendPacket(getSystemMessage(SystemMessageId.YOU_HAVE_TAKEN_S1_DAMAGE_BECAUSE_YOU_WERE_UNABLE_TO_BREATHE).addInt((int) reduceHp));
        }
    }
}
