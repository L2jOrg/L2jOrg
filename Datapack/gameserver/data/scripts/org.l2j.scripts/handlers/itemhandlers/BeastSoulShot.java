package handlers.itemhandlers;

import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

/**
 * @author JoeAlisson
 */
public class BeastSoulShot extends AbstractBeastShot {

    @Override
    protected ShotType getShotType(Item item) {
        return ShotType.SOULSHOTS;
    }

    @Override
    protected void sendUsesMessage(Player player) {
        player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOUR_PET_USES_S1).addString("soulshot"));
    }

    @Override
    protected SystemMessageId getNotEnoughMessage() {
        return SystemMessageId.YOU_DON_T_HAVE_ENOUGH_SOULSHOTS_NEEDED_FOR_A_SERVITOR;
    }
}
