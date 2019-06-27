package org.l2j.gameserver.network.serverpackets.elementalspirits;

import org.l2j.gameserver.data.elemental.ElementalType;
import org.l2j.gameserver.network.L2GameClient;

import static java.util.Objects.isNull;

public abstract class UpdateElementalSpiritPacket extends  AbstractElementalSpiritPacket {

    private final byte type;
    private final boolean update;

    UpdateElementalSpiritPacket(byte type, boolean update) {
        this.type = type;
        this.update = update;
    }

    protected void writeUpdate(L2GameClient client) {

        writeByte(update);
        writeByte(type);

        if(update) {
            var spirit = client.getActiveChar().getElementalSpirit(ElementalType.of(type));

            if(isNull(spirit)) {
                return;
            }

            writeByte(spirit.getType());
            writeSpiritInfo(spirit);
        }

    }
}
