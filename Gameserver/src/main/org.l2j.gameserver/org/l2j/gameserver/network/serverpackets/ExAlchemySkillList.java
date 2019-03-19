package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.data.xml.impl.SkillData;
import org.l2j.gameserver.data.xml.impl.SkillTreesData;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author UnAfraid
 */
public class ExAlchemySkillList extends IClientOutgoingPacket {
    private final List<Skill> _skills = new ArrayList<>();

    public ExAlchemySkillList(L2PcInstance player) {
        _skills.addAll(player.getAllSkills().stream().filter(s -> SkillTreesData.getInstance().isAlchemySkill(s.getId(), s.getLevel())).collect(Collectors.toList()));
        _skills.add(SkillData.getInstance().getSkill(CommonSkill.ALCHEMY_CUBE.getId(), 1));
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_ALCHEMY_SKILL_LIST.writeId(packet);

        packet.putInt(_skills.size());
        for (Skill skill : _skills) {
            packet.putInt(skill.getId());
            packet.putInt(skill.getLevel());
            packet.putLong(0x00); // Always 0 on Naia, SP i guess?
            packet.put((byte) (skill.getId() == CommonSkill.ALCHEMY_CUBE.getId() ? 0 : 1)); // This is type in flash, visible or not
        }
    }
}
