package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.mobius.gameserver.model.skills.Skill;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author godson
 */
public class ExOlympiadSpelledInfo extends IClientOutgoingPacket {
    private final int _playerId;
    private final List<BuffInfo> _effects = new ArrayList<>();
    private final List<Skill> _effects2 = new ArrayList<>();

    public ExOlympiadSpelledInfo(L2PcInstance player) {
        _playerId = player.getObjectId();
    }

    public void addSkill(BuffInfo info) {
        _effects.add(info);
    }

    public void addSkill(Skill skill) {
        _effects2.add(skill);
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_OLYMPIAD_SPELLED_INFO.writeId(packet);

        packet.putInt(_playerId);
        packet.putInt(_effects.size() + _effects2.size());
        for (BuffInfo info : _effects) {
            if ((info != null) && info.isInUse()) {
                packet.putInt(info.getSkill().getDisplayId());
                packet.putShort((short) info.getSkill().getDisplayLevel());
                packet.putShort((short) 0x00); // Sub level
                packet.putInt(info.getSkill().getAbnormalType().getClientId());
                writeOptionalD(packet, info.getSkill().isAura() ? -1 : info.getTime());
            }
        }
        for (Skill skill : _effects2) {
            if (skill != null) {
                packet.putInt(skill.getDisplayId());
                packet.putShort((short) skill.getDisplayLevel());
                packet.putShort((short) 0x00); // Sub level
                packet.putInt(skill.getAbnormalType().getClientId());
                packet.putShort((short) -1);
            }
        }
    }
}
