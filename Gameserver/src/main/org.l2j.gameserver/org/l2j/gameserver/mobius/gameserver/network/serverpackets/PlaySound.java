package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class PlaySound extends IClientOutgoingPacket {
    private final int _unknown1;
    private final String _soundFile;
    private final int _unknown3;
    private final int _unknown4;
    private final int _unknown5;
    private final int _unknown6;
    private final int _unknown7;
    private final int _unknown8;

    public PlaySound(String soundFile) {
        _unknown1 = 0;
        _soundFile = soundFile;
        _unknown3 = 0;
        _unknown4 = 0;
        _unknown5 = 0;
        _unknown6 = 0;
        _unknown7 = 0;
        _unknown8 = 0;
    }

    public PlaySound(int unknown1, String soundFile, int unknown3, int unknown4, int unknown5, int unknown6, int unknown7) {
        _unknown1 = unknown1;
        _soundFile = soundFile;
        _unknown3 = unknown3;
        _unknown4 = unknown4;
        _unknown5 = unknown5;
        _unknown6 = unknown6;
        _unknown7 = unknown7;
        _unknown8 = 0;
    }

    public String getSoundName() {
        return _soundFile;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.PLAY_SOUND.writeId(packet);

        packet.putInt(_unknown1); // unknown 0 for quest and ship;
        writeString(_soundFile, packet);
        packet.putInt(_unknown3); // unknown 0 for quest; 1 for ship;
        packet.putInt(_unknown4); // 0 for quest; objectId of ship
        packet.putInt(_unknown5); // x
        packet.putInt(_unknown6); // y
        packet.putInt(_unknown7); // z
        packet.putInt(_unknown8);
    }
}
