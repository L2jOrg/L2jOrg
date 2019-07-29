package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;

/**
 * @author Mobius
 */
public class RequestStopMove extends ClientPacket {
    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player != null) {
            player.stopMove(player.getLocation());
        }
    }
}
