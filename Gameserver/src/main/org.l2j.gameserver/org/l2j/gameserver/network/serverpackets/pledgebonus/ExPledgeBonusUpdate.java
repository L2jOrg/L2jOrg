package org.l2j.gameserver.network.serverpackets.pledgebonus;

import org.l2j.gameserver.enums.ClanRewardType;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
public class ExPledgeBonusUpdate extends IClientOutgoingPacket {
    private final ClanRewardType _type;
    private final int _value;

    public ExPledgeBonusUpdate(ClanRewardType type, int value) {
        _type = type;
        _value = value;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_PLEDGE_BONUS_UPDATE);
        writeByte((byte) _type.getClientId());
        writeInt(_value);
    }

}
