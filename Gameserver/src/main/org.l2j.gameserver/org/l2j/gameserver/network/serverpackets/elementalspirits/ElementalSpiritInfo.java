package org.l2j.gameserver.network.serverpackets.elementalspirits;


import org.l2j.gameserver.data.elemental.ElementalSpirit;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import static java.util.Objects.isNull;

public class ElementalSpiritInfo extends ServerPacket {

    private final byte spiritId;
    private final byte type;

    public ElementalSpiritInfo(byte id) {
        spiritId = id;
        type = 1;
    }

    public ElementalSpiritInfo(byte id, byte type) {
        this.spiritId = id;
        this.type = type;
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

        writeByte(type); // show spirit info window 1; Change type
        writeByte(player.getActiveElementalSpiritType());

        writeByte(spirits.length); // spirit count

        for (ElementalSpirit spirit : spirits) {
            writeByte(spirit.getType());
            writeByte(0x01); // spirit active ?
            // if active
            writeByte(spirit.getStage());
            writeInt(spirit.getNpcId());
            writeLong(spirit.getExperience());
            writeLong(spirit.getExperienceToNextLevel()); // next exp
            writeLong(spirit.getExperienceToNextLevel()); // max exp
            writeInt(spirit.getLevel());
            writeInt(spirit.getMaxLevel());
            writeInt(spirit.getAvailableCharacteristicsPoints());
            writeInt(spirit.getAttackPoints());
            writeInt(spirit.getDefensePoints());
            writeInt(spirit.getCriticalRatePoints());
            writeInt(spirit.getCriticalDamagePoints());
            writeInt(spirit.getMaxCharacteristics());
            writeInt(spirit.getMaxCharacteristics());
            writeInt(spirit.getMaxCharacteristics());
            writeInt(spirit.getMaxCharacteristics());

            writeByte(1); // unk

            for (int j = 0; j < 1; j++) { // unk
                writeShort(2);
                writeLong(10);
            }
        }

        writeInt(1); // talent count
        for (int j = 0; j < 1; j++) { // for each talent
            writeInt(57); // init talent item id
            writeLong(50000); // init talent item count
        }
    }
}