package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author -Wooden-
 */
public class PledgeSkillList extends IClientOutgoingPacket {
    private final Skill[] _skills;
    private final SubPledgeSkill[] _subSkills;

    public PledgeSkillList(L2Clan clan) {
        _skills = clan.getAllSkills();
        _subSkills = clan.getAllSubSkills();
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.PLEDGE_SKILL_LIST.writeId(packet);

        packet.putInt(_skills.length);
        packet.putInt(_subSkills.length); // Squad skill length
        for (Skill sk : _skills) {
            packet.putInt(sk.getDisplayId());
            packet.putShort((short) sk.getDisplayLevel());
            packet.putShort((short) 0x00); // Sub level
        }
        for (SubPledgeSkill sk : _subSkills) {
            packet.putInt(sk._subType); // Clan Sub-unit types
            packet.putInt(sk._skillId);
            packet.putShort((short) sk._skillLvl);
            packet.putShort((short) 0x00); // Sub level
        }
    }

    public static class SubPledgeSkill {
        int _subType;
        int _skillId;
        int _skillLvl;

        public SubPledgeSkill(int subType, int skillId, int skillLvl) {
            _subType = subType;
            _skillId = skillId;
            _skillLvl = skillLvl;
        }
    }
}
