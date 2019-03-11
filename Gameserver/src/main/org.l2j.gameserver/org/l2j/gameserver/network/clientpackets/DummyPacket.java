package org.l2j.gameserver.network.clientpackets;

import java.nio.ByteBuffer;

/**
 * @author zabbix Lets drink to code!
 */
public final class DummyPacket extends IClientIncomingPacket {
    @Override
    public void readImpl(ByteBuffer packet) {
    }

    @Override
    public void runImpl() {

    }
}
