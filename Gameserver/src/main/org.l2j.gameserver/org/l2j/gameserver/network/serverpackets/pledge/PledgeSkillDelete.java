package org.l2j.gameserver.network.serverpackets.pledge;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author JoeAlisson
 */
public class PledgeSkillDelete extends ServerPacket {

    private final Skill skill;

    public PledgeSkillDelete(Skill skill) {
        this.skill = skill;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_PLEDGE_SKILL_DELETE);
        writeInt(skill.getId());
        writeInt(skill.getLevel());
    }

}