package org.l2j.gameserver.network.serverpackets.elementalspirits;

import org.l2j.gameserver.api.elemental.ElementalSpirit;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

abstract class AbstractElementalSpiritPacket extends ServerPacket {


    void writeSpiritInfo(ElementalSpirit spirit) {
        writeByte(spirit.getStage());
        writeInt(spirit.getNpcId());
        writeLong(spirit.getExperience());
        writeLong(spirit.getExperienceToNextLevel());
        writeLong(spirit.getExperienceToNextLevel());
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
            writeLong(100);
        }
    }
}
