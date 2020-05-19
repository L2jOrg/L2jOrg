package org.l2j.gameserver.network.serverpackets.pledge;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author -Wooden-
 */
public class PledgeSkillAdd extends ServerPacket {
    private final int id;
    private final int level;

    public PledgeSkillAdd(int id, int level) {
        this.id = id;
        this.level = level;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_PLEDGE_SKILL_ADD);
        writeInt(id);
        writeInt(level);
    }

}