package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.ArrayList;
import java.util.List;

public class PartySpelled extends ServerPacket {
    private final List<BuffInfo> _effects = new ArrayList<>();
    private final List<Skill> _effects2 = new ArrayList<>();
    private final Creature _activeChar;

    public PartySpelled(Creature cha) {
        _activeChar = cha;
    }

    public void addSkill(BuffInfo info) {
        _effects.add(info);
    }

    public void addSkill(Skill skill) {
        _effects2.add(skill);
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.PARTY_SPELLED);

        writeInt(_activeChar.isServitor() ? 2 : _activeChar.isPet() ? 1 : 0);
        writeInt(_activeChar.getObjectId());
        writeInt(_effects.size() + _effects2.size());
        for (BuffInfo info : _effects) {
            if ((info != null) && info.isInUse()) {
                writeInt(info.getSkill().getDisplayId());
                writeShort((short) info.getSkill().getDisplayLevel());
                writeInt(info.getSkill().getAbnormalType().getClientId());
                writeOptionalD(info.getTime());
            }
        }
        for (Skill skill : _effects2) {
            if (skill != null) {
                writeInt(skill.getDisplayId());
                writeShort((short) skill.getDisplayLevel());
                writeInt(skill.getAbnormalType().getClientId());
                writeShort((short) -1);
            }
        }
    }

}
