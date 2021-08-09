package org.l2j.gameserver.network.clientpackets.collections;

import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.collections.ExCollectionList;

public class ExRequestCollectionList extends ClientPacket {
    @Override
    protected void readImpl() throws Exception {

    }

    @Override
    protected void runImpl() {
        client.sendPacket(new ExCollectionList(client.getPlayer()));
    }
}
