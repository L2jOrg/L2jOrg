package org.l2j.gameserver.network.serverpackets.elementalspirits;

import org.l2j.gameserver.api.elemental.ElementalType;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.network.ServerPacketId.EX_ELEMENTAL_SPIRIT_EVOLUTION_INFO;

public class ElementalSpiritEvolutionInfo extends ServerPacket {

    private final byte type;

    public ElementalSpiritEvolutionInfo(byte type) {
        this.type = type;
    }

    @Override
    protected void writeImpl(GameClient client) {
        writeId(EX_ELEMENTAL_SPIRIT_EVOLUTION_INFO);

        var player = client.getPlayer();
        var spirit = player.getElementalSpirit(ElementalType.of(type));

        if(isNull(spirit)) {
            writeByte(0);
            writeInt(0);
            return;
        }

        writeByte(type);
        writeInt(spirit.getNpcId());
        writeInt(0x01); // unk
        writeInt(spirit.getStage());
        writeDouble(100); // chance ??

        var items = spirit.getItemsToEvolve();
        writeInt(items.size());
        for (ItemHolder item : items) {
            writeInt(item.getId());
            writeLong(item.getCount());
        }
    }
}
