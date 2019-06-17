package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.AllyCrest;

/**
 * This class ...
 *
 * @version $Revision: 1.3.4.4 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestAllyCrest extends ClientPacket {
    private int _crestId;

    @Override
    public void readImpl() {
        _crestId = readInt();
        readInt(); // Ally ID
        readInt(); // Server ID
    }

    @Override
    public void runImpl() {
        client.sendPacket(new AllyCrest(_crestId));
    }
}
