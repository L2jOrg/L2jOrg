package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.util.GameUtils;

import java.util.ArrayList;
import java.util.List;

import static org.l2j.gameserver.util.GameUtils.isPet;

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
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.PARTY_SPELLED);

        writeInt(GameUtils.isServitor(_activeChar) ? 2 : isPet(_activeChar) ? 1 : 0);
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
