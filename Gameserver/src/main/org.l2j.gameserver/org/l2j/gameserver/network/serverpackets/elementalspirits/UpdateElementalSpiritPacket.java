package org.l2j.gameserver.network.serverpackets.elementalspirits;

import org.l2j.gameserver.api.elemental.ElementalType;
import org.l2j.gameserver.network.GameClient;

import static java.util.Objects.isNull;

public abstract class UpdateElementalSpiritPacket extends AbstractElementalSpiritPacket {

    private final byte type;
    private final boolean update;

    UpdateElementalSpiritPacket(byte type, boolean update) {
        this.type = type;
        this.update = update;
    }

    protected void writeUpdate(GameClient client) {
        var player = client.getPlayer();
        writeByte(update);
        writeByte(type);

        if(update) {
            var spirit = player.getElementalSpirit(ElementalType.of(type));

            if(isNull(spirit)) {
                return;
            }

            writeByte(type);
            writeSpiritInfo(spirit);
        }
    }
}
