package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

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
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_PLEDGE_SKILL_ADD);

        writeInt(_id);
        writeInt(_lvl);
    }

}