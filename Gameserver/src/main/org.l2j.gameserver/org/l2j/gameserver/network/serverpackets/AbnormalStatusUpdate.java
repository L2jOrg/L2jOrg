package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.ArrayList;
import java.util.List;

public class AbnormalStatusUpdate extends ServerPacket {
    private final List<BuffInfo> _effects = new ArrayList<>();

    public void addSkill(BuffInfo info) {
        if (!info.getSkill().isHealingPotionSkill()) {
            _effects.add(info);
        }
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.ABNORMAL_STATUS_UPDATE);

        writeShort(_effects.size());
        for (BuffInfo info : _effects) {
            if ((info != null) && info.isInUse()) {
                writeInt(info.getSkill().getDisplayId());
                writeShort(info.getSkill().getDisplayLevel());
                writeInt(info.getSkill().getAbnormalType().getClientId());
                writeOptionalD(info.getSkill().isAura() ? -1 : info.getTime());
            }
        }
    }

}
