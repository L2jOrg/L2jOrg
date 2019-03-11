package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.PledgeCrest;

import java.nio.ByteBuffer;

/**
 * This class ...
 *
 * @version $Revision: 1.4.4.4 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestPledgeCrest extends IClientIncomingPacket {
    private int _crestId;

    @Override
    public void readImpl(ByteBuffer packet) {
        _crestId = packet.getInt();
        packet.getInt(); // clanId
    }

    @Override
    public void runImpl() {
        client.sendPacket(new PledgeCrest(_crestId));
    }
}
