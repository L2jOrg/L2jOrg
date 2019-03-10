package org.l2j.gameserver.mobius.gameserver.network.clientpackets.primeshop;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.primeshop.ExBRGamePoint;

import java.nio.ByteBuffer;

/**
 * @author Gnacik, UnAfraid
 */
public final class RequestBRGamePoint extends IClientIncomingPacket {
    @Override
    public void readImpl(ByteBuffer packet) {
    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();
        if (player != null) {
            client.sendPacket(new ExBRGamePoint(player));
        }
    }
}