package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;

import java.nio.ByteBuffer;

public final class RequestDeleteMacro extends IClientIncomingPacket {
    private int _id;

    @Override
    public void readImpl(ByteBuffer packet) {
        _id = packet.getInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }
        activeChar.deleteMacro(_id);
    }
}
