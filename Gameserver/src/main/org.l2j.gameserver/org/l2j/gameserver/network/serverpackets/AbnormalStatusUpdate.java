package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.network.L2GameClient;
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
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.ABNORMAL_STATUS_UPDATE);

        writeShort((short) _effects.size());
        for (BuffInfo info : _effects) {
            if ((info != null) && info.isInUse()) {
                writeInt(info.getSkill().getDisplayId());
                writeShort((short) info.getSkill().getDisplayLevel());
                // writeShort((short)info.getSkill().getSubLevel());
                writeInt(info.getSkill().getAbnormalType().getClientId());
                writeOptionalD(info.getSkill().isAura() ? -1 : info.getTime());
            }
        }
    }

}
