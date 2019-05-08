package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.Collection;

public class GMViewSkillInfo extends IClientOutgoingPacket {
    private final L2PcInstance _activeChar;
    private final Collection<Skill> _skills;

    public GMViewSkillInfo(L2PcInstance cha) {
        _activeChar = cha;
        _skills = _activeChar.getSkillList();
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.GM_VIEW_SKILL_INFO.writeId(packet);

        writeString(_activeChar.getName(), packet);
        packet.putInt(_skills.size());

        final boolean isDisabled = (_activeChar.getClan() != null) && (_activeChar.getClan().getReputationScore() < 0);

        for (Skill skill : _skills) {
            packet.putInt(skill.isPassive() ? 1 : 0);
            packet.putShort((short) skill.getDisplayLevel());
            packet.putShort((short) skill.getSubLevel());
            packet.putInt(skill.getDisplayId());
            packet.putInt(0x00);
            packet.put((byte) (isDisabled && skill.isClanSkill() ? 1 : 0));
            packet.put((byte)(skill.isEnchantable() ? 1 : 0));
        }
    }

    @Override
    protected int size(L2GameClient client) {
        return 11 + _activeChar.getName().length() * 2 + _skills.size() * 18;
    }
}