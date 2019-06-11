package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.L2Party.MessageType;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;

import java.nio.ByteBuffer;

/**
 * This class ...
 *
 * @version $Revision: 1.3.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestOustPartyMember extends IClientIncomingPacket {
    private String _name;

    @Override
    public void readImpl() {
        _name = readString();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (activeChar.isInParty() && activeChar.getParty().isLeader(activeChar)) {
            activeChar.getParty().removePartyMember(_name, MessageType.EXPELLED);
        }
    }
}
