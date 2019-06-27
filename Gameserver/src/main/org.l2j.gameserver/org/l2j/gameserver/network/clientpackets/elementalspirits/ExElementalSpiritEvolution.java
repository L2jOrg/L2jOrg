package org.l2j.gameserver.network.clientpackets.elementalspirits;

import org.l2j.gameserver.data.elemental.ElementalType;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ElementalSpiritEvolution;

import static java.util.Objects.isNull;

public class ExElementalSpiritEvolution extends ClientPacket {

    private byte type;

    @Override
    protected void readImpl() throws Exception {
        type = readByte();
    }

    @Override
    protected void runImpl()  {
        var player = client.getActiveChar();
        var spirit = player.getElementalSpirit(ElementalType.of(type));

        player.sendPacket(new ElementalSpiritEvolution(type, false));
    }
}
