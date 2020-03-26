package handlers.itemhandlers;

import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

/**
 * @author JoeAlisson
 */
public class BeastSoulShot extends AbstractBeastShot {

    @Override
    protected ShotType getShotType() {
        return ShotType.SOULSHOTS;
    }

    @Override
    protected boolean isBlessed() {
        return false;
    }

    @Override
    protected double getBonus(Summon summon) {
        return summon.getStats().getValue(Stat.SOUL_SHOTS_BONUS, 1) * 2;
    }

    @Override
    protected void sendUsesMessage(Player player) {
        player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOUR_PET_USES_S1).addString("soulshot"));
    }
}
