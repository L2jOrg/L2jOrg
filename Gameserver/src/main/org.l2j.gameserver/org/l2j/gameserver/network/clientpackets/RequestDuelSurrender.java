package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.instancemanager.DuelManager;

/**
 * Format:(ch) just a trigger
 *
 * @author -Wooden-
 */
public final class RequestDuelSurrender extends ClientPacket {
    @Override
    public void readImpl() {
    }

    @Override
    public void runImpl() {
        DuelManager.getInstance().doSurrender(client.getActiveChar());
    }
}
