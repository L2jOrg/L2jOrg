package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ExAbnormalStatusUpdateFromTarget extends ServerPacket {
    private final Creature _character;
    private final List<BuffInfo> _effects;

    public ExAbnormalStatusUpdateFromTarget(Creature character) {
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
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_ABNORMAL_STATUS_UPDATE_FROM_TARGET);

        writeInt(_character.getObjectId());
        writeShort((short) _effects.size());

        for (BuffInfo info : _effects) {
            writeInt(info.getSkill().getDisplayId());
            writeShort((short) info.getSkill().getDisplayLevel());
            // writeShort((short)info.getSkill().getSubLevel());
            writeShort((short) info.getSkill().getAbnormalType().getClientId());
            writeOptionalD(info.getSkill().isAura() ? -1 : info.getTime());
            writeInt(info.getEffectorObjectId());
        }
    }

}
