package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.model.L2World;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;

import java.nio.ByteBuffer;

/**
 * @author -Wooden-
 */
public final class SnoopQuit extends IClientIncomingPacket {
    private int _snoopID;

    @Override
    public void readImpl(ByteBuffer packet) {
        _snoopID = packet.getInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance player = L2World.getInstance().getPlayer(_snoopID);
        if (player == null) {
            return;
        }

        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        player.removeSnooper(activeChar);
        activeChar.removeSnooped(player);

    }
}
