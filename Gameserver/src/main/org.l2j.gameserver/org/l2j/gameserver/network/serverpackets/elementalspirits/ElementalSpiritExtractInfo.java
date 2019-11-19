package org.l2j.gameserver.network.serverpackets.elementalspirits;

import org.l2j.gameserver.api.elemental.ElementalType;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import static java.util.Objects.isNull;

public class ElementalSpiritExtractInfo extends ServerPacket {

    private final byte type;

    public ElementalSpiritExtractInfo(byte type) {
        this.type = type;
    }

    @Override
    protected void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_ELEMENTAL_SPIRIT_EXTRACT_INFO);

        var spirit = client.getPlayer().getElementalSpirit(ElementalType.of(type));
        if(isNull(spirit)) {
            writeByte(0);
            writeByte(0);
            return;
        }

        writeByte(type); // active elemental spirit
        writeByte(1); // is extract ?
        
        writeByte(1); // cost count
         // for each cost count
        writeInt(57); // item id
        writeInt(1000000); // item count

        writeInt(spirit.getExtractItem());
        writeInt(spirit.getExtractAmount());
    }
}
