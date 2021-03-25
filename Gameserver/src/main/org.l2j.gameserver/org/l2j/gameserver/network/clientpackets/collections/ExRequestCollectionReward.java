package org.l2j.gameserver.network.clientpackets.collections;

import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.collections.ExCollectionReward;

public class ExRequestCollectionReward extends ClientPacket {
    @Override
    protected void readImpl() throws Exception {

    }

    @Override
    protected void runImpl() {
        client.sendPacket(new ExCollectionReward(client.getPlayer()));
    }
}