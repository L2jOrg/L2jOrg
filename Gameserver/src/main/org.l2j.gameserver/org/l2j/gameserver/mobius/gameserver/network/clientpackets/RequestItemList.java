package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import java.nio.ByteBuffer;

/**
 * This class ...
 *
 * @version $Revision: 1.3.4.3 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestItemList extends IClientIncomingPacket {
    @Override
    public void readImpl(ByteBuffer packet) {

    }

    @Override
    public void runImpl() {
        if ((client != null) && (client.getActiveChar() != null) && !client.getActiveChar().isInventoryDisabled()) {
            client.getActiveChar().sendItemList();
        }
    }
}
