package org.l2j.gameserver.network.clientpackets.vip;

import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.vip.ReceiveVipInfo;

public class ExRequestVipInfo extends ClientPacket {

    @Override
    protected void readImpl() throws Exception {
        // just trigger packet
    }

    @Override
    protected void runImpl() {
        client.sendPacket(new ReceiveVipInfo());
    }
}
