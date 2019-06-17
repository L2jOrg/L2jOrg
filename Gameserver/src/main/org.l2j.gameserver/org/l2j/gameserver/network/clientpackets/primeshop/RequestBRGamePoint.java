package org.l2j.gameserver.network.clientpackets.primeshop;

import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.primeshop.ExBRGamePoint;

/**
 * @author Gnacik, UnAfraid
 */
public final class RequestBRGamePoint extends ClientPacket {
    @Override
    public void readImpl() {
    }

    @Override
    public void runImpl() {
        client.sendPacket(new ExBRGamePoint());
    }
}