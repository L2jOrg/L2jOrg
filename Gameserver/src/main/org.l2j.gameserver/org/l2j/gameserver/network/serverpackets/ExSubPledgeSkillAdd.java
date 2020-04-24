package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author VISTALL
 */
public class ExSubPledgeSkillAdd extends ServerPacket {
    private final int _type;
    private final int _skillId;
    private final int _skillLevel;

    public ExSubPledgeSkillAdd(int type, int skillId, int skillLevel) {
        _type = type;
        _skillId = skillId;
        _skillLevel = skillLevel;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_SUBPLEDGE_SKILL_ADD);

        writeInt(_type);
        writeInt(_skillId);
        writeInt(_skillLevel);
    }

}
