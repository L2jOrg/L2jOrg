package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.model.L2Party;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;

import java.nio.ByteBuffer;

/**
 * This packet is received from client when a party leader requests to change the leadership to another player in his party.
 */
public final class RequestChangePartyLeader extends IClientIncomingPacket {
    private String _name;

    @Override
    public void readImpl(ByteBuffer packet) {
        _name = readString(packet);
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        final L2Party party = activeChar.getParty();
        if ((party != null) && party.isLeader(activeChar)) {
            party.changePartyLeader(_name);
        }
    }
}
