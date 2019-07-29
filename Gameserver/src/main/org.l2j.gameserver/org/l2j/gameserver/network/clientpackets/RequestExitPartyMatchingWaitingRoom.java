package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.instancemanager.MatchingRoomManager;
import org.l2j.gameserver.model.actor.instance.Player;

/**
 * @author Gnacik
 */
public final class RequestExitPartyMatchingWaitingRoom extends ClientPacket {
    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        MatchingRoomManager.getInstance().removeFromWaitingList(player);
    }
}