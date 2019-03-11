package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public final class KeyPacket extends IClientOutgoingPacket {
    private final byte[] _key;
    private final int _result;

    public KeyPacket(byte[] key, int result) {
        _key = key;
        _result = result;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.VERSION_CHECK.writeId(packet);

        packet.put((byte) _result); // 0 - wrong protocol, 1 - protocol ok
        for (int i = 0; i < 8; i++) {
            packet.put(_key[i]); // key
        }
        packet.putInt(0x01);
        packet.putInt(Config.SERVER_ID); // server id
        packet.put((byte) 0x01);
        packet.putInt(0x00); // obfuscation key
        packet.put((byte) ((Config.SERVER_LIST_TYPE & 0x400) == 0x400 ? 0x01 : 0x00)); // isClassic
    }
}
