package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class AbnormalStatusUpdate extends IClientOutgoingPacket {
    private final List<BuffInfo> _effects = new ArrayList<>();

    public void addSkill(BuffInfo info) {
        if (!info.getSkill().isHealingPotionSkill()) {
            _effects.add(info);
        }
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.ABNORMAL_STATUS_UPDATE.writeId(packet);

        packet.putShort((short) _effects.size());
        for (BuffInfo info : _effects) {
            if ((info != null) && info.isInUse()) {
                packet.putInt(info.getSkill().getDisplayId());
                packet.putShort((short) info.getSkill().getDisplayLevel());
                // packet.putShort((short)info.getSkill().getSubLevel());
                packet.putInt(info.getSkill().getAbnormalType().getClientId());
                writeOptionalD(packet, info.getSkill().isAura() ? -1 : info.getTime());
            }
        }
    }
}
