package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ExAbnormalStatusUpdateFromTarget extends IClientOutgoingPacket {
    private final L2Character _character;
    private final List<BuffInfo> _effects;

    public ExAbnormalStatusUpdateFromTarget(L2Character character) {
        //@formatter:off
        _character = character;
        _effects = character.getEffectList().getEffects()
                .stream()
                .filter(Objects::nonNull)
                .filter(BuffInfo::isInUse)
                .filter(b -> !b.getSkill().isToggle())
                .collect(Collectors.toList());
        //@formatter:on
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_ABNORMAL_STATUS_UPDATE_FROM_TARGET.writeId(packet);

        packet.putInt(_character.getObjectId());
        packet.putShort((short) _effects.size());

        for (BuffInfo info : _effects) {
            packet.putInt(info.getSkill().getDisplayId());
            packet.putShort((short) info.getSkill().getDisplayLevel());
            // packet.putShort((short)info.getSkill().getSubLevel());
            packet.putShort((short) info.getSkill().getAbnormalType().getClientId());
            writeOptionalD(packet, info.getSkill().isAura() ? -1 : info.getTime());
            packet.putInt(info.getEffectorObjectId());
        }
    }
}
