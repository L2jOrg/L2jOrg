package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.vip.ReceiveVipInfo;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

public class ExRequestVipInfo extends IClientIncomingPacket {

    @Override
    protected void readImpl(ByteBuffer packet) throws Exception {
        // just trigger packet
    }

    @Override
    protected void runImpl() {
        client.sendPacket(new ReceiveVipInfo());
    }
}
