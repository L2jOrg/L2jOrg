package org.l2j.gameserver.network.authcomm.gs2as;

import io.github.joealisson.primitive.HashIntIntMap;
import io.github.joealisson.primitive.IntIntMap;
import org.l2j.gameserver.network.authcomm.AuthServerClient;
import org.l2j.gameserver.network.authcomm.SendablePacket;

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
    protected void writeImpl(AuthServerClient client) {
        writeByte((byte) 0x06);
        writeInt(status.size());
        status.entrySet().forEach(entry ->  { writeInt(entry.getKey()); writeInt(entry.getValue()); });
    }
}
