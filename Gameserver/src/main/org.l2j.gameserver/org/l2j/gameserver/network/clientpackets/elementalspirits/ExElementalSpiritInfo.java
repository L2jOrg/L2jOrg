package org.l2j.gameserver.network.clientpackets.elementalspirits;


import org.l2j.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ElementalSpiritExtractInfo;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ElementalSpiritInfo;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ExElementalSpiritAbsorbInfo;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ExElementalSpiritEvolutionInfo;

public class ExElementalSpiritInfo extends IClientIncomingPacket {

    private int _id;

    public void readImpl() {
        this._id = readByte();
    }

    public void runImpl() {
        client.sendPacket(new ElementalSpiritInfo());
        client.sendPacket(new ElementalSpiritExtractInfo());
        client.sendPacket(new ExElementalSpiritAbsorbInfo());
        client.sendPacket(new ExElementalSpiritEvolutionInfo());
    }

}