package org.l2j.gameserver.network.authcomm.gs2as;

import io.github.joealisson.primitive.maps.IntIntMap;
import io.github.joealisson.primitive.maps.impl.HashIntIntMap;
import org.l2j.gameserver.network.authcomm.AuthServerClient;
import org.l2j.gameserver.network.authcomm.SendablePacket;

import java.nio.ByteBuffer;

public class ServerStatus extends SendablePacket {

    public static final int SERVER_LIST_STATUS = 0x01;
    public static final int SERVER_LIST_CLOCK = 0x02;
    public static final int SERVER_LIST_SQUARE_BRACKET = 0x03;
    public static final int MAX_PLAYERS = 0x04;
    public static final int TEST_SERVER = 0x05;
    public static final int SERVER_LIST_TYPE = 0x06;

    private final IntIntMap status = new HashIntIntMap();

    public ServerStatus add(int status, int value) {
        this.status.put(status, value);
        return this;
    }

    @Override
    protected void writeImpl(AuthServerClient client, ByteBuffer buffer) {
        buffer.put((byte) 0x06);
        buffer.putInt(status.size());
        status.entrySet().forEach(entry -> buffer.putInt(entry.getKey()).putInt(entry.getValue()));
    }
}
