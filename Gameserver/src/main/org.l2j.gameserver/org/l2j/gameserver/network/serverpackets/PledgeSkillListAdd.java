package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author -Wooden-
 */
public class PledgeSkillListAdd extends IClientOutgoingPacket {
    private final int _id;
    private final int _lvl;

    public PledgeSkillListAdd(int id, int lvl) {
        _id = id;
        _lvl = lvl;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.PLEDGE_SKILL_LIST_ADD.writeId(packet);

        packet.putInt(_id);
        packet.putInt(_lvl);
    }

    @Override
    protected int size(L2GameClient client) {
        return 13;
    }
}