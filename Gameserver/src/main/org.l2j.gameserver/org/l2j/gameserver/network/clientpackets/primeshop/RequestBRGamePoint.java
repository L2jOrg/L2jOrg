package org.l2j.gameserver.network.clientpackets.primeshop;

import org.l2j.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2j.gameserver.network.serverpackets.primeshop.ExBRGamePoint;

import java.nio.ByteBuffer;

/**
 * @author Gnacik, UnAfraid
 */
public final class RequestBRGamePoint extends IClientIncomingPacket {
    @Override
    public void readImpl() {
    }

    @Override
    public void runImpl() {
        client.sendPacket(new ExBRGamePoint());
    }
}