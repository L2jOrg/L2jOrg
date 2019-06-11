package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class ExChangeMpCost extends IClientOutgoingPacket {

    private final double modifier;
    private final int skillType;

    public ExChangeMpCost(double modifier, int skillType) {
        this.modifier = modifier;
        this.skillType = skillType;
    }

    @Override
    protected void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_CHANGE_MPCOST);
        writeInt(skillType);
        writeDouble(modifier);
    }

}
