package org.l2j.gameserver.mobius.gameserver.network.clientpackets.shuttle;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.clientpackets.IClientIncomingPacket;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
public class RequestShuttleGetOff extends IClientIncomingPacket {
    private int _x;
    private int _y;
    private int _z;

    @Override
    public void readImpl(ByteBuffer packet) {
        packet.getInt(); // charId
        _x = packet.getInt();
        _y = packet.getInt();
        _z = packet.getInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (activeChar.getShuttle() != null) {
            activeChar.getShuttle().removePassenger(activeChar, _x, _y, _z);
        }
    }
}
