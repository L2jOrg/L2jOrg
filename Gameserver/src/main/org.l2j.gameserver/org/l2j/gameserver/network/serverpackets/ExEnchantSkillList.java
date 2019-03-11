package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.SkillEnchantType;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

public class ExEnchantSkillList extends IClientOutgoingPacket {
    private final SkillEnchantType _type;
    private final List<Skill> _skills = new LinkedList<>();

    public ExEnchantSkillList(SkillEnchantType type) {
        _type = type;
    }

    public void addSkill(Skill skill) {
        _skills.add(skill);
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_ENCHANT_SKILL_LIST.writeId(packet);

        packet.putInt(_type.ordinal());
        packet.putInt(_skills.size());
        for (Skill skill : _skills) {
            packet.putInt(skill.getId());
            packet.putShort((short) skill.getLevel());
            packet.putShort((short) skill.getSubLevel());
        }
    }
}