package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author -Wooden-
 */
public class PledgeSkillListAdd extends ServerPacket {
    private final int _id;
    private final int _lvl;

    public PledgeSkillListAdd(int id, int lvl) {
        _id = id;
        _lvl = lvl;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.PLEDGE_SKILL_LIST_ADD);

        writeInt(_id);
        writeInt(_lvl);
    }

}