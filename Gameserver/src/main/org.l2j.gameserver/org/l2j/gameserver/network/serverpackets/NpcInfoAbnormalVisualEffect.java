package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.skills.AbnormalVisualEffect;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.Set;

/**
 * @author Sdw
 */
public class NpcInfoAbnormalVisualEffect extends IClientOutgoingPacket {
    private final L2Npc _npc;

    public NpcInfoAbnormalVisualEffect(L2Npc npc) {
        _npc = npc;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.NPC_INFO_ABNORMAL_VISUAL_EFFECT.writeId(packet);

        packet.putInt(_npc.getObjectId());
        packet.putInt(_npc.getTransformationDisplayId());

        final Set<AbnormalVisualEffect> abnormalVisualEffects = _npc.getEffectList().getCurrentAbnormalVisualEffects();
        packet.putInt(abnormalVisualEffects.size());
        for (AbnormalVisualEffect abnormalVisualEffect : abnormalVisualEffects) {
            packet.putShort((short) abnormalVisualEffect.getClientId());
        }
    }
}
