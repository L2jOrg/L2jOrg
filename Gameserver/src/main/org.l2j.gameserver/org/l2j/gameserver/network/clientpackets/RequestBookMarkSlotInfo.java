package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExGetBookMarkInfoPacket;

/**
 * @author ShanSoft Packets Structure: chddd
 */
public final class RequestBookMarkSlotInfo extends ClientPacket {

    @Override
    public void readImpl() {
    }

    @Override
    public void runImpl() {
        final Player player = client.getActiveChar();
        if (player != null) {
            player.sendPacket(new ExGetBookMarkInfoPacket(player));
        }
    }
}
