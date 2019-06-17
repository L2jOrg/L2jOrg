package org.l2j.gameserver.network.clientpackets.vip;

import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.vip.ReceiveVipProductList;

public class RequestVipProductList extends ClientPacket {

    @Override
    protected void readImpl() throws Exception {
        // trigger packet
    }

    @Override
    protected void runImpl() {
        client.sendPacket(new ReceiveVipProductList());
    }
}
