package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.mobius.gameserver.model.skills.Skill;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class PartySpelled extends IClientOutgoingPacket {
    private final List<BuffInfo> _effects = new ArrayList<>();
    private final List<Skill> _effects2 = new ArrayList<>();
    private final L2Character _activeChar;

    public PartySpelled(L2Character cha) {
        _activeChar = cha;
    }

    public void addSkill(BuffInfo info) {
        _effects.add(info);
    }

    public void addSkill(Skill skill) {
        _effects2.add(skill);
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.PARTY_SPELLED.writeId(packet);

        packet.putInt(_activeChar.isServitor() ? 2 : _activeChar.isPet() ? 1 : 0);
        packet.putInt(_activeChar.getObjectId());
        packet.putInt(_effects.size() + _effects2.size());
        for (BuffInfo info : _effects) {
            if ((info != null) && info.isInUse()) {
                packet.putInt(info.getSkill().getDisplayId());
                packet.putShort((short) info.getSkill().getDisplayLevel());
                packet.putInt(info.getSkill().getAbnormalType().getClientId());
                writeOptionalD(packet, info.getTime());
            }
        }
        for (Skill skill : _effects2) {
            if (skill != null) {
                packet.putInt(skill.getDisplayId());
                packet.putShort((short) skill.getDisplayLevel());
                packet.putInt(skill.getAbnormalType().getClientId());
                packet.putShort((short) -1);
            }
        }
    }
}
