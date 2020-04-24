package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

public class ExChangeMpCost extends ServerPacket {

    private final double modifier;
    private final int skillType;

    public ExChangeMpCost(double modifier, int skillType) {
        this.modifier = modifier;
        this.skillType = skillType;
    }

    @Override
    protected void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_CHANGE_MP_COST);
        writeInt(skillType);
        writeDouble(modifier);
    }

}
