package org.l2j.gameserver.network.serverpackets.pledge;

import org.l2j.gameserver.enums.ClanRewardType;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author UnAfraid
 */
public class ExPledgeBonusUpdate extends ServerPacket {
    private final ClanRewardType _type;
    private final int _value;

    public ExPledgeBonusUpdate(ClanRewardType type, int value) {
        _type = type;
        _value = value;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_PLEDGE_BONUS_UPDATE);
        writeByte((byte) _type.getClientId());
        writeInt(_value);
    }

}
