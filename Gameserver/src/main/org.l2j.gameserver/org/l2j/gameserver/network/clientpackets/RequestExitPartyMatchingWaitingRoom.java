package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.instancemanager.MatchingRoomManager;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author Gnacik
 */
public final class RequestExitPartyMatchingWaitingRoom extends ClientPacket {
    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();
        if (player == null) {
            return;
        }

        MatchingRoomManager.getInstance().removeFromWaitingList(player);
    }
}