package org.l2j.gameserver.network.serverpackets.elementalspirits;

import org.l2j.gameserver.data.elemental.ElementalType;
import org.l2j.gameserver.network.L2GameClient;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.network.ServerPacketId.EX_ELEMENTAL_SPIRIT_ABSORB;

public class ElementalSpiritAbsorb extends AbstractElementalSpiritPacket {

    private final byte type;
    private final boolean absorbed;

    public ElementalSpiritAbsorb(byte type, boolean absorbed) {
        this.type = type;
        this.absorbed = absorbed;
    }

    @Override
    protected void writeImpl(L2GameClient client)  {
        writeId(EX_ELEMENTAL_SPIRIT_ABSORB);

        writeByte(absorbed);
        writeByte(type);

        if(absorbed) {
            var spirit = client.getActiveChar().getElementalSpirit(ElementalType.of(type));

            if(isNull(spirit)) {
                return;
            }

            writeByte(spirit.getType());
            writeSpiritInfo(spirit);
        }
    }
}
