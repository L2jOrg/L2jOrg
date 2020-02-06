package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author -Wooden-
 */
public class PledgeSkillList extends ServerPacket {
    private final Skill[] _skills;
    private final SubPledgeSkill[] _subSkills;

    public PledgeSkillList(Clan clan) {
        _skills = clan.getAllSkills();
        _subSkills = clan.getAllSubSkills();
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.PLEDGE_SKILL_LIST);

        writeInt(_skills.length);
        writeInt(_subSkills.length); // Squad skill length
        for (Skill sk : _skills) {
            writeInt(sk.getDisplayId());
            writeShort((short) sk.getDisplayLevel());
            writeShort((short) 0x00); // Sub level
        }
        for (SubPledgeSkill sk : _subSkills) {
            writeInt(sk._subType); // Clan Sub-unit types
            writeInt(sk._skillId);
            writeShort((short) sk._skillLvl);
            writeShort((short) 0x00); // Sub level
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
