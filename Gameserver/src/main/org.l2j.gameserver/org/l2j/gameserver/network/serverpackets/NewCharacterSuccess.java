package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.templates.L2PcTemplate;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public final class NewCharacterSuccess extends IClientOutgoingPacket {
    private final List<L2PcTemplate> _chars = new ArrayList<>();

    public void addChar(L2PcTemplate template) {
        _chars.add(template);
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.NEW_CHARACTER_SUCCESS.writeId(packet);

        packet.putInt(_chars.size());
        for (L2PcTemplate chr : _chars) {
            if (chr == null) {
                continue;
            }

            // TODO: Unhardcode these
            packet.putInt(chr.getRace().ordinal());
            packet.putInt(chr.getClassId().getId());

            packet.putInt(99);
            packet.putInt(chr.getBaseSTR());
            packet.putInt(1);

            packet.putInt(99);
            packet.putInt(chr.getBaseDEX());
            packet.putInt(1);

            packet.putInt(99);
            packet.putInt(chr.getBaseCON());
            packet.putInt(1);

            packet.putInt(99);
            packet.putInt(chr.getBaseINT());
            packet.putInt(1);

            packet.putInt(99);
            packet.putInt(chr.getBaseWIT());
            packet.putInt(1);

            packet.putInt(99);
            packet.putInt(chr.getBaseMEN());
            packet.putInt(1);
        }
    }
}
