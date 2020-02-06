package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.Collection;

public class GMViewSkillInfo extends ServerPacket {
    private final Player _activeChar;
    private final Collection<Skill> _skills;

    public GMViewSkillInfo(Player cha) {
        _activeChar = cha;
        _skills = _activeChar.getSkillList();
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.GM_VIEW_SKILL_INFO);

        writeString(_activeChar.getName());
        writeInt(_skills.size());

        final boolean isDisabled = (_activeChar.getClan() != null) && (_activeChar.getClan().getReputationScore() < 0);

        for (Skill skill : _skills) {
            writeInt(skill.isPassive() ? 1 : 0);
            writeShort((short) skill.getDisplayLevel());
            writeShort((short) skill.getSubLevel());
            writeInt(skill.getDisplayId());
            writeInt(0x00);
            writeByte((byte) (isDisabled && skill.isClanSkill() ? 1 : 0));
            writeByte((byte)(skill.isEnchantable() ? 1 : 0));
        }
    }

}