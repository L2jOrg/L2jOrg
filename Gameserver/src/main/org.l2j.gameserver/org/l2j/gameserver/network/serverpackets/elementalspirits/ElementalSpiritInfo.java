package org.l2j.gameserver.network.serverpackets.elementalspirits;

import org.l2j.gameserver.data.elemental.ElementalSpirit;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import static java.util.Objects.isNull;

public class ElementalSpiritInfo extends AbstractElementalSpiritPacket {

    private final byte spiritType;
    private final byte type;

    public ElementalSpiritInfo(byte spiritType, byte packetType) {
        this.spiritType = spiritType;
        this.type = packetType;
    }

    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_ELEMENTAL_SPIRIT_INFO);

        var player = client.getActiveChar();
        var spirits = player.getSpirits();

        if(isNull(spirits)) {
            writeByte(0);
            writeByte(0);
            writeByte(0);
            return;
        }

        writeByte(type); // show spirit info window 1; Change type 2;
        writeByte(spiritType);

        writeByte(spirits.length); // spirit count

        for (ElementalSpirit spirit : spirits) {
            writeByte(spirit.getType());
            writeByte(0x01); // spirit active ?
            // if active
            writeSpiritInfo(spirit);
        }

        writeInt(1); // talent count
        for (int j = 0; j < 1; j++) { // for each talent
            writeInt(57); // init talent item id
            writeLong(50000); // init talent item count
        }
    }
}