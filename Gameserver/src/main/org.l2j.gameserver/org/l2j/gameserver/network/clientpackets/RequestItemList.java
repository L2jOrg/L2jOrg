package org.l2j.gameserver.network.clientpackets;

/**
 * This class ...
 *
 * @version $Revision: 1.3.4.3 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestItemList extends ClientPacket {
    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() {
        if ((client != null) && (client.getPlayer() != null) && !client.getPlayer().isInventoryDisabled()) {
            client.getPlayer().sendItemList();
        }
    }
}
