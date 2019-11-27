package org.l2j.gameserver.network.clientpackets;

import static java.util.Objects.nonNull;

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
        var player = client.getPlayer();
        if (nonNull(player)  && !player.isInventoryDisabled()) {
            player.sendItemList();
        }
    }
}
