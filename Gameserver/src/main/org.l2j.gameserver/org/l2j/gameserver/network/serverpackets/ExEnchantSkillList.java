package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.SkillEnchantType;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.LinkedList;
import java.util.List;

public class ExEnchantSkillList extends ServerPacket {
    private final SkillEnchantType _type;
    private final List<Skill> _skills = new LinkedList<>();

    public ExEnchantSkillList(SkillEnchantType type) {
        _type = type;
    }

    public void addSkill(Skill skill) {
        _skills.add(skill);
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_ENCHANT_SKILL_LIST);

        writeInt(_type.ordinal());
        writeInt(_skills.size());
        for (Skill skill : _skills) {
            writeInt(skill.getId());
            writeShort((short) skill.getLevel());
            writeShort((short) skill.getSubLevel());
        }
    }

}