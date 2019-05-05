package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.settings.ServerSettings;

import java.nio.ByteBuffer;

import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.gameserver.ServerType.CLASSIC;

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
        var serverSettings = getSettings(ServerSettings.class);
        packet.putInt(0x01);
        packet.putInt(serverSettings.serverId());
        packet.put((byte) 0x01);
        packet.putInt(0x00); // obfuscation key
        packet.put((byte) ((serverSettings.type() & CLASSIC.getMask()) != 0 ? 0x01 : 0x00)); // isClassic
    }
}
