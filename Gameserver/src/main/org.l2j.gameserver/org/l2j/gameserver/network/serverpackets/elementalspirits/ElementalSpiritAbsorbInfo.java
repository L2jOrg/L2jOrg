package org.l2j.gameserver.network.serverpackets.elementalspirits;

import org.l2j.gameserver.data.elemental.AbsorbItem;
import org.l2j.gameserver.data.elemental.ElementalType;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import static java.util.Objects.isNull;
import static org.l2j.commons.util.Util.zeroIfNullElseCompute;
import static org.l2j.gameserver.network.ServerPacketId.EX_ELEMENTAL_SPIRIT_ABSORB_INFO;

public class ElementalSpiritAbsorbInfo extends ServerPacket {

    private final byte type;

    public ElementalSpiritAbsorbInfo(byte type) {
        this.type = type;
    }

    @Override
    protected void writeImpl(L2GameClient client) {
        writeId(EX_ELEMENTAL_SPIRIT_ABSORB_INFO);

        var player = client.getActiveChar();
        var spirit = player.getElementalSpirit(ElementalType.of(type));

        if(isNull(spirit)) {
            writeByte(0x00);
            writeByte(0x00);
            return;
        }

        writeByte(0x01);
        writeByte(type);
        writeByte(spirit.getStage());
        writeLong(spirit.getExperience());
        writeLong(spirit.getExperienceToNextLevel()); //NextExp
        writeLong(spirit.getExperienceToNextLevel()); //MaxExp
        writeInt(spirit.getLevel());
        writeInt(spirit.getMaxLevel());

        var absorbItems = spirit.getAbsorbItems();

        writeInt(absorbItems.size()); //AbsorbCount
        for (AbsorbItem absorbItem : absorbItems) {
            writeInt(absorbItem.getId());
            writeInt(zeroIfNullElseCompute( player.getInventory().getItemByItemId(absorbItem.getId()), item -> (int) item.getCount()));
            writeInt(absorbItem.getExperience());
        }
    }
}
